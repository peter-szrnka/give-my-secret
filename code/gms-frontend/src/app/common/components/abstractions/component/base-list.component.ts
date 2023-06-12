import { ArrayDataSource } from "@angular/cdk/collections";
import { Directive, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { catchError } from "rxjs";
import { User } from "../../../../components/user/model/user.model";
import { BaseList } from "../../../model/base-list";
import { PageConfig } from "../../../model/common.model";
import { SharedDataService } from "../../../service/shared-data-service";
import { checkRights } from "../../../utils/permission-utils";
import { ConfirmDeleteDialog } from "../../confirm-delete/confirm-delete-dialog.component";
import { ServiceBase } from "../service/service-base";

/**
 * @author Peter Szrnka
 */
@Directive()
export abstract class BaseListComponent<T, S extends ServiceBase<T, BaseList<T>>> implements OnInit {

    public datasource : ArrayDataSource<T>;

    public tableConfig = {
      count : 0,
      pageIndex : 0,
      pageSize : localStorage.getItem(this.getPageConfig().scope + "_pageSize") ?? 25
    };

    constructor(
      protected router : Router,
      protected sharedData : SharedDataService, 
      protected service : S,
      public dialog: MatDialog,
      protected activatedRoute: ActivatedRoute) {}

    ngOnInit(): void {
      this.fetchData();
    }

    abstract getPageConfig() : PageConfig;

    public toggle(entityId : number, status : string) : void {
      this.service.toggle(entityId, status !== 'ACTIVE').subscribe(() => this.reloadPage());
    }

    public onFetch(event : any) {
      localStorage.setItem(this.getPageConfig().scope + "_pageSize", event.pageSize);
      this.tableConfig.pageIndex = event.pageIndex;
      this.reloadPage();
    }

    protected fetchData() {
      const user : User | undefined = this.sharedData.getUserInfo();

      if(checkRights(user)) {
        this.initDefaultDataTable();
        return;
      }

      this.activatedRoute.data
      .pipe(catchError(async() => this.initDefaultDataTable()))
      .subscribe((response : any) => {
        this.tableConfig.count = response.data.totalElements;
        this.datasource = new ArrayDataSource<T>(response.data.resultList);
      });
    }

    public promptDelete(id : number) {
      const dialogRef = this.dialog.open(ConfirmDeleteDialog, {
        width: '250px',
        data: true,
      });
  
      dialogRef.afterClosed().subscribe(result => {
        if (result !== true) {
          return;
        }

        this.service.delete(id).subscribe(() => this.reloadPage());
      });
    }

    private reloadPage() : void {
      void this.router.navigate(['/' + this.getPageConfig().scope + "/list"], { queryParams : { "page" : this.tableConfig.pageIndex }});
    }

    private initDefaultDataTable() {
      this.datasource = new ArrayDataSource<T>([]);
    }
}