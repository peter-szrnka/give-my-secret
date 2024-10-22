import { Component } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Params, Router } from "@angular/router";
import { catchError } from "rxjs";
import { ConfirmDeleteDialog } from "../../common/components/confirm-delete/confirm-delete-dialog.component";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { checkRights } from "../../common/utils/permission-utils";
import { User } from "../user/model/user.model";
import { SystemProperty } from "./model/system-property.model";
import { SystemPropertyService } from "./service/system-property.service";

const ALGORITHM_SET: any = [
  'HS256', 'HS384', 'HS512'
];

const TYPE_MAP: any = {
  'LONG': 'number',
  'STRING': 'text',
  'BOOLEAN': 'boolean',
}

const TIME_UNITS: any = [
  { 'key': 'm', value: 'minute' },
  { 'key': 'd', value: 'day' },
  { 'key': 'M', value: 'month' },
  { 'key': 'y', value: 'year' },
  { 'key': 'w', value: 'week' }
];

const BOOL_VALUE_SET: string[] = ['true', 'false'];

const createBoolConfig = (text: string, callbackMethod?: string) => {
  return {
    text: text,
    valueSet: BOOL_VALUE_SET,
    displayMode: 'list',
    callbackMethod: callbackMethod
  };
};
const createAlgorithmConfig = (text: string) => { return { text: text, valueSet: ALGORITHM_SET, displayMode: 'list' } };
const createTextConfig = (text: string, hint?: string, callbackMethod?: string) => { return { 
  text: text, 
  displayMode: 'text', 
  hint: hint,
  callbackMethod: callbackMethod 
} };

export const PROPERTY_TEXT_MAP: any = {
  'ACCESS_JWT_EXPIRATION_TIME_SECONDS': createTextConfig('Access JWT expiration time in seconds'),
  'ACCESS_JWT_ALGORITHM': createAlgorithmConfig('Access JWT Algorithom'),
  'REFRESH_JWT_EXPIRATION_TIME_SECONDS': createTextConfig('Refresh JWT expiration time in seconds'),
  'REFRESH_JWT_ALGORITHM': createAlgorithmConfig('Refresh JWT Algorithom'),
  'ORGANIZATION_NAME': createTextConfig('Organization / Company name'),
  'ORGANIZATION_CITY': createTextConfig('Location (city) of the organization'),
  'ENABLE_GLOBAL_MFA': createBoolConfig('Global MFA usage is enabled or not'),
  'ENABLE_MFA': createBoolConfig('MFA usage is enabled or not for the users'),
  'FAILED_ATTEMPTS_LIMIT': createTextConfig('Limit of failed login attempts before blocking the user'),
  'JOB_OLD_EVENT_LIMIT': createTextConfig('Limit of deletion of the old events', 'Units: m=minute, d=day, M=month, y=year, w=week. Format: "1;d"'),
  'JOB_OLD_MESSAGE_LIMIT': createTextConfig('Limit of deletion of the old messages', 'Units: m=minute, d=day, M=month, y=year, w=week. Format: "1;d"'),
  'OLD_JOB_ENTRY_LIMIT': createTextConfig('Limit of deletion of the old job entries', 'Units: m=minute, d=day, M=month, y=year, w=week. Format: "1;d"'),
  'EVENT_MAINTENANCE_JOB_ENABLED': createBoolConfig('Event maintenance job is enabled or not'),
  'EVENT_MAINTENANCE_RUNNER_CONTAINER_ID': createTextConfig('Main container ID for running event maintenance job'),
  'JOB_MAINTENANCE_RUNNER_CONTAINER_ID': createTextConfig('Main container ID for running job maintenance job'),
  'JOB_MAINTENANCE_JOB_ENABLED': createBoolConfig('Job maintenance job is enabled or not'),
  'KEYSTORE_CLEANUP_JOB_ENABLED': createBoolConfig('Keystore maintenance job is enabled or not'),
  'KEYSTORE_CLEANUP_RUNNER_CONTAINER_ID': createTextConfig('Main container ID for running keystore maintenance job'),
  'LDAP_SYNC_JOB_ENABLED': createBoolConfig('LDAP sync job is enabled or not'),
  'LDAP_SYNC_RUNNER_CONTAINER_ID': createTextConfig('Main container ID for running LDAP sync runner job'),
  'MESSAGE_CLEANUP_JOB_ENABLED': createBoolConfig('Message cleanup job is enabled or not'),
  'MESSAGE_CLEANUP_RUNNER_CONTAINER_ID': createTextConfig('Main container ID for message cleanup job'),
  'SECRET_ROTATION_JOB_ENABLED': createBoolConfig('Secret rotation job is enabled or not'),
  'SECRET_ROTATION_RUNNER_CONTAINER_ID': createTextConfig('Main container ID for secret rotation job'),
  'USER_ANONYMIZATION_JOB_ENABLED': createBoolConfig('User anonymization job is enabled or not'),
  'USER_ANONYMIZATION_RUNNER_CONTAINER_ID': createTextConfig('Main container ID for running user anonymization job'),
  'USER_DELETION_JOB_ENABLED': createBoolConfig('User deletion job is enabled or not'),
  'USER_DELETION_RUNNER_CONTAINER_ID': createTextConfig('Main container ID for running user deletion job'),
  'ENABLE_MULTI_NODE': createBoolConfig('Multi-node usage is enabled or not'),
  'ENABLE_AUTOMATIC_LOGOUT': createBoolConfig('Automatic logout is enabled or not', 'checkSystemReady'),
  'AUTOMATIC_LOGOUT_TIME_IN_MINUTES': createTextConfig('Automatic logout is performed after T minutes', undefined, 'checkSystemReady')
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
  templateUrl: './system-property-list.component.html'
})
export class SystemPropertyListComponent {
  columns: string[] = ['key', 'value', 'type', 'lastModified', 'operations'];
  timeUnits: any[] = TIME_UNITS;

  public datasource: MatTableDataSource<SystemPropertyElement> = new MatTableDataSource<SystemPropertyElement>([]);
  protected count = 0;

  public confirmDeleteDialogRef: MatDialogRef<ConfirmDeleteDialog, any>;
  public infoDialogRef: MatDialogRef<InfoDialog, any>;

  public tableConfig = {
    count: 0,
    pageIndex: 0,
    pageSize: localStorage.getItem("system_property_pageSize") ?? 25
  };

  constructor(
    protected router: Router,
    public sharedData: SharedDataService,
    protected service: SystemPropertyService,
    public dialogService: DialogService,
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
        this.datasource = new MatTableDataSource<SystemPropertyElement>(this.convertToElements(response.data.resultList));
      });
  }

  applyFilter(event: any) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.datasource.filter = filterValue.trim().toLowerCase();
  }

  private convertToElements(resultList: SystemProperty[]): SystemPropertyElement[] {
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

  public onFetch(event: any) {
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
    this.confirmDeleteDialogRef = this.dialogService.openConfirmDeleteDialog();

    this.confirmDeleteDialogRef.afterClosed().subscribe((data: any) => {
      if (data.result !== true) {
        return;
      }

      this.service.delete(element.key).subscribe(() => {
        this.executeCallbackMethod(element.callbackMethod);
        this.reloadPage();
      });
    });
  }

  openInformationDialog(message: string, navigateToList: boolean, dialogType: string) {
    this.infoDialogRef = this.dialogService.openCustomDialog(message, dialogType);

    this.infoDialogRef.afterClosed().subscribe(() => {
      if (navigateToList === false) {
        return;
      }

      this.reloadPage();
    });
  }

  private initDefaultDataTable() {
    this.datasource = new MatTableDataSource<SystemPropertyElement>([]);
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