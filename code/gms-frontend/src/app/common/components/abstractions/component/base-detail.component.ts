import { Directive, OnInit } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseDetail } from "../../../model/base-detail.model";
import { BaseList } from "../../../model/base-list";
import { PageConfig } from "../../../model/common.model";
import { DialogService } from "../../../service/dialog-service";
import { SharedDataService } from "../../../service/shared-data-service";
import { SplashScreenStateService } from "../../../service/splash-screen-service";
import { InfoDialog } from "../../info-dialog/info-dialog.component";
import { ServiceBase } from "../service/service-base";

/**
 * @author Peter Szrnka
 */
@Directive()
export abstract class BaseDetailComponent<T extends BaseDetail, S extends ServiceBase<T, BaseList<T>>> implements OnInit {

  data: T;
  public error? : string;

  constructor(
    protected router: Router,
    protected sharedData: SharedDataService,
    protected service: S,
    public dialog: DialogService,
    protected activatedRoute: ActivatedRoute,
    protected splashScreenStateService: SplashScreenStateService) { }

  ngOnInit(): void {
    this.sharedData.refreshCurrentUserInfo();
    this.fetchData();
  }

  abstract getPageConfig(): PageConfig;

  abstract dataLoadingCallback(data: T) : void;

  private fetchData() {
    this.activatedRoute.data.subscribe((response: any) => {
      this.data = response['entity'];
      this.error = this.data.error;
      this.dataLoadingCallback(this.data);
    });
  }

  public openInformationDialog(key: string, navigateToList: boolean, dialogType : string, arg?: any, errorCode?: string) {
    const dialogRef: MatDialogRef<InfoDialog, any> = this.dialog.openNewDialog({ text: key, arg: arg, type: dialogType, errorCode: errorCode });

    dialogRef.afterClosed().subscribe(() => {
      if (navigateToList === false) {
        return;
      }

      void this.router.navigate(['/' + this.getPageConfig().scope + '/list']);
    });
  }
}