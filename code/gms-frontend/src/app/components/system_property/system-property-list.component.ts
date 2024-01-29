import { ArrayDataSource } from "@angular/cdk/collections";
import { Component } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { catchError } from "rxjs";
import { ConfirmDeleteDialog } from "../../common/components/confirm-delete/confirm-delete-dialog.component";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { SharedDataService } from "../../common/service/shared-data-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { checkRights } from "../../common/utils/permission-utils";
import { User } from "../user/model/user.model";
import { SystemProperty } from "./model/system-property.model";
import { SystemPropertyService } from "./service/system-property.service";

const ALGORITHM_SET: any = [
  'HS256', 'HS384', 'HS512'
];

const TYPE_MAP : any = {
  'LONG' : 'number',
  'STRING' : 'text',
  'BOOLEAN': 'boolean',
}

const BOOL_VALUE_SET : string[] = ['true','false'];

export const PROPERTY_TEXT_MAP: any = {
  'ACCESS_JWT_EXPIRATION_TIME_SECONDS': { text: 'Access JWT expiration time in seconds' },
  'ACCESS_JWT_ALGORITHM': { text: 'Access JWT Algorithom', valueSet: ALGORITHM_SET },
  'REFRESH_JWT_EXPIRATION_TIME_SECONDS': { text: 'Refresh JWT expiration time in seconds' },
  'REFRESH_JWT_ALGORITHM': { text: 'Refresh JWT Algorithom', valueSet: ALGORITHM_SET },
  'OLD_EVENT_TIME_LIMIT_DAYS': { text: 'Limit of old events deletion in days' },
  'ORGANIZATION_NAME' : { text: 'Organization / Company name' },
  'ORGANIZATION_CITY' : { text: 'Location (city) of the organization' },
  'ENABLE_GLOBAL_MFA' : { text: 'Global MFA usage is enabled or not', valueSet: BOOL_VALUE_SET },
	'ENABLE_MFA': { text: 'MFA usage is enabled or not for the users', valueSet: BOOL_VALUE_SET },
  'FAILED_ATTEMPTS_LIMIT' : { text: 'Limit after users should be blocked' }
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

  public datasource: ArrayDataSource<SystemProperty>;
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
    protected activatedRoute: ActivatedRoute) { }

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
        this.datasource = new ArrayDataSource<SystemProperty>(response.data.resultList);
      });
  }

  public onFetch(event : any) {
    localStorage.setItem("system_property_pageSize", event.pageSize);
    this.fetchData();
  }

  public getTextDescription(key: string): string {
    return PROPERTY_TEXT_MAP[key].text;
  }

  public getInputType(type : string) : string {
    return TYPE_MAP[type];
  }

  public getValueSet(key: string): string[] {
    try {
      return PROPERTY_TEXT_MAP[key].valueSet;
    } catch(e) {
      return [];
    }
  }

  public save(element: any) {
    element.mode = undefined;
    element.valueSet = undefined;
    this.service.save(element).subscribe({
      next: () => {
        this.openInformationDialog("System property has been saved!", true, 'information');
      },
      error: (err) => {
        this.openInformationDialog("Error: " + getErrorMessage(err), false, 'warning');
      }
    });
  }

  public promptDelete(key: string) {
    const dialogRef = this.dialog.open(ConfirmDeleteDialog, {
      width: '250px',
      data: true,
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result !== true) {
        return;
      }

      this.service.delete(key).subscribe(() => void this.router.navigate(['/system_property/list']));
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

      void this.router.navigate(['/system_property/list']);
    });
  }

  private initDefaultDataTable() {
    this.datasource = new ArrayDataSource<SystemProperty>([]);
  }
}