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

import * as systemPropertyList from '../../../assets/i18n/system-properties.json';

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

const createBoolConfig = (callbackMethod?: string) => {
  return {
    valueSet: BOOL_VALUE_SET,
    displayMode: 'toggle',
    callbackMethod: callbackMethod
  };
};

const createAlgorithmConfig = () => { return { valueSet: ALGORITHM_SET, displayMode: 'list' } };
const createTextConfig = () => {
  return {
    displayMode: 'text'
  }
};

const createUnitBasedTextConfig = (callbackMethod?: string) => {
  return {
    displayMode: 'text',
    hint: 'UNITS',
    callbackMethod: callbackMethod
  }
};

export const PROPERTY_TEXT_MAP: any = {
  'ACCESS_JWT_EXPIRATION_TIME_SECONDS': createTextConfig(),
  'ACCESS_JWT_ALGORITHM': createAlgorithmConfig(),
  'REFRESH_JWT_EXPIRATION_TIME_SECONDS': createTextConfig(),
  'REFRESH_JWT_ALGORITHM': createAlgorithmConfig(),
  'ORGANIZATION_NAME': createTextConfig(),
  'ORGANIZATION_CITY': createTextConfig(),
  'ENABLE_GLOBAL_MFA': createBoolConfig(),
  'ENABLE_MFA': createBoolConfig(),
  'FAILED_ATTEMPTS_LIMIT': createTextConfig(),
  'JOB_OLD_EVENT_LIMIT': createUnitBasedTextConfig(),
  'JOB_OLD_MESSAGE_LIMIT': createUnitBasedTextConfig(),
  'OLD_JOB_ENTRY_LIMIT': createUnitBasedTextConfig(),
  'EVENT_MAINTENANCE_JOB_ENABLED': createBoolConfig(),
  'EVENT_MAINTENANCE_RUNNER_CONTAINER_ID': createTextConfig(),
  'JOB_MAINTENANCE_RUNNER_CONTAINER_ID': createTextConfig(),
  'JOB_MAINTENANCE_JOB_ENABLED': createBoolConfig(),
  'KEYSTORE_CLEANUP_JOB_ENABLED': createBoolConfig(),
  'KEYSTORE_CLEANUP_RUNNER_CONTAINER_ID': createTextConfig(),
  'LDAP_SYNC_JOB_ENABLED': createBoolConfig(),
  'LDAP_SYNC_RUNNER_CONTAINER_ID': createTextConfig(),
  'MESSAGE_CLEANUP_JOB_ENABLED': createBoolConfig(),
  'MESSAGE_CLEANUP_RUNNER_CONTAINER_ID': createTextConfig(),
  'SECRET_ROTATION_JOB_ENABLED': createBoolConfig(),
  'SECRET_ROTATION_RUNNER_CONTAINER_ID': createTextConfig(),
  'USER_ANONYMIZATION_JOB_ENABLED': createBoolConfig(),
  'USER_ANONYMIZATION_RUNNER_CONTAINER_ID': createTextConfig(),
  'USER_DELETION_JOB_ENABLED': createBoolConfig(),
  'USER_DELETION_RUNNER_CONTAINER_ID': createTextConfig(),
  'ENABLE_MULTI_NODE': createBoolConfig(),
  'ENABLE_AUTOMATIC_LOGOUT': createBoolConfig('checkSystemReady'),
  'AUTOMATIC_LOGOUT_TIME_IN_MINUTES': createUnitBasedTextConfig('checkSystemReady'),
  'ENABLE_DETAILED_AUDIT': createBoolConfig()
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
    standalone: false
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
      const hint = PROPERTY_TEXT_MAP[property.key]?.hint;
      return {
        ...property,
        textDescription: this.getResolvedPropertyText(property.key),
        valueSet: PROPERTY_TEXT_MAP[property.key]?.valueSet ?? [],
        value: "BOOLEAN" === property.type ? property.value === 'true' : property.value,
        mode: undefined,
        inputType: TYPE_MAP[property.type],
        hint: hint ? this.getResolvedPropertyText(hint) : undefined,
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
        this.openInformationDialog("dialog.save.systemProperty", true, 'information');
        this.executeCallbackMethod(element.callbackMethod);
      },
      error: (err) => {
        this.openInformationDialog("dialog.save.error", false, 'warning', getErrorMessage(err));
        this.reloadPage();
      }
    });
  }

  public promptDelete(element: SystemProperty) {
    this.confirmDeleteDialogRef = this.dialogService.openConfirmDeleteDialog({
      key: 'dialog.deleteSystemProperty',
      result: true
    });

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

  openInformationDialog(message: string, navigateToList: boolean, dialogType: string, errorMessage?: string) {
    this.infoDialogRef = this.dialogService.openNewDialog({ text: message, type: dialogType, arg: errorMessage });

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

  private getResolvedPropertyText(key: string): string {
    return this.getSystemPropertyMap()[key] ?? "N/A";
  }

  private getSystemPropertyMap(): any {
    return systemPropertyList[this.getLanguage() as keyof typeof systemPropertyList];
  }

  private getLanguage(): string {
    return localStorage.getItem('language') ?? 'en';
  }
}