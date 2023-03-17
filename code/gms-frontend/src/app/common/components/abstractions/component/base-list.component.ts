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
    protected count  = 0;

    public tableConfig = {
      pageSize : 20
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
      this.service.toggle(entityId || 0, status !== 'ACTIVE').subscribe(() => this.router.navigate(['/' + this.getPageConfig().scope + "/list"]));
    }

    public getCount() : number {
      return this.count;
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
        this.count = response.itemList.length;
        this.datasource = new ArrayDataSource<T>(response.itemList);
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

        this.service.delete(id).subscribe(() => this.router.navigate(['/' + this.getPageConfig().scope + "/list"]));
      });
    }

    private initDefaultDataTable() {
      this.datasource = new ArrayDataSource<T>([]);
    }
}