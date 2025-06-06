import { ArrayDataSource } from "@angular/cdk/collections";
import { Directive, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { catchError, takeUntil } from "rxjs";
import { User } from "../../../../components/user/model/user.model";
import { BaseList } from "../../../model/base-list";
import { PageConfig } from "../../../model/common.model";
import { DialogService } from "../../../service/dialog-service";
import { SharedDataService } from "../../../service/shared-data-service";
import { checkRights } from "../../../utils/permission-utils";
import { ServiceBase } from "../service/service-base";
import { BaseComponent } from "./base.component";

/**
 * @author Peter Szrnka
 */
@Directive()
export abstract class BaseListComponent<T, S extends ServiceBase<T, BaseList<T>>> extends BaseComponent implements OnInit {

  protected loading = true;
  public datasource: ArrayDataSource<T>;
  public error? : string;

  public tableConfig = {
    count: 0,
    pageIndex: 0,
    pageSize: localStorage.getItem(this.getPageConfig().scope + "_pageSize") ?? 25
  };

  constructor(
    protected router: Router,
    protected sharedData: SharedDataService,
    protected service: S,
    public dialogService: DialogService,
    protected activatedRoute: ActivatedRoute) {
      super();
    }

  ngOnInit() : void {
    this.sharedData.refreshCurrentUserInfo();
    this.fetchData().then(() => {});
  }

  abstract getPageConfig(): PageConfig;

  public toggle(entityId: number, status: string): void {
    this.service.toggle(entityId, status !== 'ACTIVE').pipe(takeUntil(this.destroy$)).subscribe(() => this.reloadPage());
  }

  public onFetch(event: any) {
    localStorage.setItem(this.getPageConfig().scope + "_pageSize", event.pageSize);
    this.tableConfig.pageIndex = event.pageIndex;
    this.reloadPage();
  }

  protected async fetchData(): Promise<void> {
    this.tableConfig.pageIndex = this.activatedRoute.snapshot.queryParams['page'] ?? 0;
    const user: User | undefined = await this.sharedData.getUserInfo();

    if (checkRights(user)) {
      this.initDefaultDataTable();
      return;
    }

    this.activatedRoute.data
      .pipe(catchError(async () => this.initDefaultDataTable()))
      .pipe(takeUntil(this.destroy$))
      .subscribe((response: any) => {
        this.tableConfig.count = response.data.totalElements;
        this.datasource = new ArrayDataSource<T>(response.data.resultList);
        this.error = response.data.error;
        this.loading = false;
      });
  }

  public promptDelete(id: number) {
    const dialogRef = this.dialogService.openConfirmDeleteDialog({ result: true, key: "dialog.delete." + this.getPageConfig().scope });

    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(data => {
      if (data.result !== true) {
        return;
      }

      this.service.delete(id).pipe(takeUntil(this.destroy$)).subscribe(() => this.reloadPage());
    });
  }

  protected reloadPage(): void {
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['/' + this.getPageConfig().scope + "/list"], { queryParams: { "page": this.tableConfig.pageIndex } });
    });
  }

  private initDefaultDataTable() {
    this.datasource = new ArrayDataSource<T>([]);
  }
}