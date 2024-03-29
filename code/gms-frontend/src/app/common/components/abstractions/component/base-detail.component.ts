import { Directive, OnInit } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseList } from "../../../model/base-list";
import { PageConfig } from "../../../model/common.model";
import { ServiceBase } from "../service/service-base";
import { SharedDataService } from "../../../service/shared-data-service";
import { InfoDialog } from "../../info-dialog/info-dialog.component";
import { SplashScreenStateService } from "../../../service/splash-screen-service";
import { BaseDetail } from "../../../model/base-detail.model";

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
    public dialog: MatDialog,
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

  public openInformationDialog(message: string, navigateToList: boolean, dialogType : string) {
    const dialogRef: MatDialogRef<InfoDialog, any> = this.dialog.open(InfoDialog, {
      data: { text : message, type : dialogType },
    });

    dialogRef.afterClosed().subscribe(() => {
      if (navigateToList === false) {
        return;
      }

      void this.router.navigate(['/' + this.getPageConfig().scope + '/list']);
    });
  }
}