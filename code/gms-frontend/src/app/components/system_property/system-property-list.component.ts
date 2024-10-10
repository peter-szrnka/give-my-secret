import { ArrayDataSource } from "@angular/cdk/collections";
import { Component } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ActivatedRoute, Params, Router } from "@angular/router";
import { catchError } from "rxjs";
import { ConfirmDeleteDialog } from "../../common/components/confirm-delete/confirm-delete-dialog.component";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { SharedDataService } from "../../common/service/shared-data-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { checkRights } from "../../common/utils/permission-utils";
import { User } from "../user/model/user.model";
import { SystemProperty } from "./model/system-property.model";
import { SystemPropertyService } from "./service/system-property.service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";

const ALGORITHM_SET: any = [
  'HS256', 'HS384', 'HS512'
];

const TYPE_MAP : any = {
  'LONG' : 'number',
  'STRING' : 'text',
  'BOOLEAN': 'boolean',
}

const TIME_UNITS : any = [
  { 'key' : 'm', value : 'minute' },
  { 'key' : 'd', value : 'day' },
  { 'key' : 'M', value : 'month' },
  { 'key' : 'y', value : 'year' },
  { 'key' : 'w', value : 'week' }
];

const BOOL_VALUE_SET : string[] = ['true','false'];

export const PROPERTY_TEXT_MAP: any = {
  'ACCESS_JWT_EXPIRATION_TIME_SECONDS': { text: 'Access JWT expiration time in seconds', displayMode: 'text' },
  'ACCESS_JWT_ALGORITHM': { text: 'Access JWT Algorithom', valueSet: ALGORITHM_SET, displayMode: 'list' },
  'REFRESH_JWT_EXPIRATION_TIME_SECONDS': { text: 'Refresh JWT expiration time in seconds', displayMode: 'text' },
  'REFRESH_JWT_ALGORITHM': { text: 'Refresh JWT Algorithom', valueSet: ALGORITHM_SET, displayMode: 'list' },
  'ORGANIZATION_NAME' : { text: 'Organization / Company name', displayMode: 'text' },
  'ORGANIZATION_CITY' : { text: 'Location (city) of the organization', displayMode: 'text' },
  'ENABLE_GLOBAL_MFA' : { text: 'Global MFA usage is enabled or not', valueSet: BOOL_VALUE_SET, displayMode: 'list' },
	'ENABLE_MFA': { text: 'MFA usage is enabled or not for the users', valueSet: BOOL_VALUE_SET, displayMode: 'list' },
  'FAILED_ATTEMPTS_LIMIT' : { text: 'Limit after users should be blocked', displayMode: 'text' },
  'JOB_OLD_EVENT_LIMIT' : { text: 'Limit of deletion of the old events', hint: 'Units: m=minute, d=day, M=month, y=year, w=week. Format: "1;d"', displayMode: 'text' },
  'JOB_OLD_MESSAGE_LIMIT' : { text: 'Limit of deletion of the old messages', hint: 'Units: m=minute, d=day, M=month, y=year, w=week. Format: "1;d"', displayMode: 'text' },
  'EVENT_MAINTENANCE_RUNNER_CONTAINER_ID' : { text: 'Main container ID for running event maintenance job', displayMode: 'text' },
  'KEYSTORE_CLEANUP_RUNNER_CONTAINER_ID' : { text: 'Main container ID for running keystore maintenance job', displayMode: 'text' },
  'LDAP_SYNC_RUNNER_CONTAINER_ID' : { text: 'Main container ID for running LDAP sync runner job', displayMode: 'text' },
  'MESSAGE_CLEANUP_RUNNER_CONTAINER_ID' : { text: 'Main container ID for message cleanup job', displayMode: 'text' },
  'SECRET_ROTATION_RUNNER_CONTAINER_ID' : { text: 'Main container ID for secret rotation job', displayMode: 'text' },
  'USER_DELETION_RUNNER_CONTAINER_ID' : { text: 'Main container ID for running user deletion job', displayMode: 'text' },
  'ENABLE_MULTI_NODE' : { text: 'Multi-node usage is enabled or not', valueSet: BOOL_VALUE_SET, displayMode: 'list' },
  'ENABLE_AUTOMATIC_LOGOUT' : { text: 'Automatic logout is enabled or not', valueSet: BOOL_VALUE_SET, displayMode: 'list', callbackMethod: 'checkSystemReady' },
  'AUTOMATIC_LOGOUT_TIME_IN_MINUTES' : { text: 'Automatic logout is performed after T minutes', displayMode: 'text', hint: 'Minimum value is 15 minutes', callbackMethod: 'checkSystemReady' }
};

interface SystemPropertyElement extends SystemProperty {
  textDescription: string;
  mode?: string;
  inputType?: string;
  hint?: string;
  displayMode: string;
};

/**
 * @author Peter Szrnka
 */
@Component({
  selector: 'system-property',
  templateUrl: './system-property-list.component.html',
  styleUrls: ['./system-property-list.component.scss']
})
export class SystemPropertyListComponent {
  columns: string[] = ['key', 'value', 'type', 'lastModified', 'operations'];
  timeUnits: any[] = TIME_UNITS;

  public datasource: ArrayDataSource<SystemPropertyElement>;
  protected count = 0;

  public tableConfig = {
    count : 0,
    pageIndex : 0,
    pageSize : localStorage.getItem("system_property_pageSize") ?? 25
  };

  constructor(
    protected router: Router,
    public sharedData: SharedDataService,
    protected service: SystemPropertyService,
    public dialog: MatDialog,
    protected activatedRoute: ActivatedRoute,
    private readonly splashScreenService: SplashScreenStateService) { }

  ngOnInit(): void {
    this.fetchData();
  }

  protected async fetchData() {
    const user: User | undefined = await this.sharedData.getUserInfo();

    if (checkRights(user)) {
      this.initDefaultDataTable();
      return;
    }

    this.activatedRoute.data
      .pipe(catchError(async () => this.initDefaultDataTable()))
      .subscribe((response: any) => {
        this.count = response.data.totalElements;
        this.datasource = new ArrayDataSource<SystemPropertyElement>(this.convertToElements(response.data.resultList));
      });
  }

  private convertToElements(resultList: SystemProperty[]): readonly SystemPropertyElement[] {
    return resultList.map((property: SystemProperty) => {
      return {
        ...property,
        textDescription: PROPERTY_TEXT_MAP[property.key]?.text,
        valueSet: PROPERTY_TEXT_MAP[property.key]?.valueSet || [],
        mode: undefined,
        inputType: TYPE_MAP[property.type],
        hint: PROPERTY_TEXT_MAP[property.key]?.hint || undefined,
        displayMode: PROPERTY_TEXT_MAP[property.key]?.displayMode,
        callbackMethod: PROPERTY_TEXT_MAP[property.key]?.callbackMethod
      };
    }) as SystemPropertyElement[];
  }

  public onFetch(event : any) {
    localStorage.setItem("system_property_pageSize", event.pageSize);
    this.fetchData();
  }

  public save(element: SystemProperty) {
    element.mode = undefined;
    element.valueSet = undefined;
    this.service.save(element).subscribe({
      next: () => {
        this.openInformationDialog("System property has been saved!", true, 'information');
        this.executeCallbackMethod(element.callbackMethod);
      },
      error: (err) => {
        this.openInformationDialog("Error: " + getErrorMessage(err), false, 'warning');
        this.reloadPage();
      }
    });
  }

  public promptDelete(element: SystemProperty) {
    const dialogRef = this.dialog.open(ConfirmDeleteDialog, {
      width: '250px',
      data: true,
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result !== true) {
        return;
      }

      this.service.delete(element.key).subscribe(() => {
        this.executeCallbackMethod(element.callbackMethod);
        this.reloadPage();
      });
    });
  }

  openInformationDialog(message: string, navigateToList: boolean, dialogType: string) {
    const dialogRef: MatDialogRef<InfoDialog, any> = this.dialog.open(InfoDialog, {
      data: { text: message, type: dialogType },
    });

    dialogRef.afterClosed().subscribe(() => {
      if (navigateToList === false) {
        return;
      }

      this.reloadPage();
    });
  }

  private initDefaultDataTable() {
    this.datasource = new ArrayDataSource<SystemPropertyElement>([]);
  }

  private executeCallbackMethod(callbackMethod?: string) {
    if (!callbackMethod) {
      return;
    }

    this.splashScreenService.start();
    if (callbackMethod === 'checkSystemReady') {
      this.sharedData.checkSystemReady();
    }
  }

  private reloadPage() {
    const queryParams: Params = { t: new Date().getTime() };
      this.router.navigate([], {
        relativeTo: this.activatedRoute,
        queryParams,
        queryParamsHandling: 'merge'
      });
  }
}