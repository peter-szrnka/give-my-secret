import { Component } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { PAGE_CONFIG_API_KEY } from "../../common/model/apikey.model";
import { PageConfig } from "../../common/model/common.model";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SystemProperty } from "../../common/model/system-property.model";
import { SystemPropertyService } from "../../common/service/system-property.service";
import { ArrayDataSource } from "@angular/cdk/collections";
import { catchError } from "rxjs";
import { ConfirmDeleteDialog } from "../../common/components/confirm-delete/confirm-delete-dialog.component";
import { User } from "../../common/model/user.model";
import { checkRights } from "../../common/utils/permission-utils";

const ALGORITHM_SET : any = [
  'HS256', 'HS384', 'HS512'
];

const PROPERTY_TEXT_MAP : any = {
  'ACCESS_JWT_EXPIRATION_TIME_SECONDS' : { text : 'Access JWT expiration time in seconds' },
  'ACCESS_JWT_ALGORITHM' : { text : 'Access JWT Algorithom', valueSet : ALGORITHM_SET },
  'REFRESH_JWT_EXPIRATION_TIME_SECONDS' : { text : 'Refresh JWT expiration time in seconds' },
  'REFRESH_JWT_ALGORITHM' : { text : 'Refresh JWT Algorithom', valueSet : ALGORITHM_SET },
  'OLD_EVENT_TIME_LIMIT_DAYS' : { text : 'Limit of old events deletion in days' }
};

@Component({
    selector: 'system-property',
    templateUrl: './system-property-list.component.html',
    styleUrls : ['./system-property-list.component.scss']
})
export class SystemPropertyListComponent {
    columns: string[] = ['key', 'value', 'lastModified', 'operations'];
    
    public datasource : ArrayDataSource<SystemProperty>;
    protected count  = 0;

    public tableConfig = {
      pageSize : 20
    };

    constructor(
      protected router : Router,
      public sharedData : SharedDataService, 
      protected service : SystemPropertyService,
      public dialog: MatDialog,
      protected activatedRoute: ActivatedRoute) {}

    ngOnInit(): void {
      this.fetchData();
    }

    public getCount() : number {
      return this.count;
    }

    protected fetchData() {
      const user : User | undefined = this.sharedData.getUserInfo();

      if(checkRights(user, undefined)) {
        this.initDefaultDataTable();
        return;
      }

      this.activatedRoute.data
      .pipe(catchError(async() => this.initDefaultDataTable()))
      .subscribe((response : any) => {
        this.count = response.itemList.length;
        this.datasource = new ArrayDataSource<SystemProperty>(response.itemList);
      });
    }

    public getTextDescription(key : string) : string {
      return PROPERTY_TEXT_MAP[key].text;
    }

    public getValueSet(key : string) : string[] {
      return PROPERTY_TEXT_MAP[key].valueSet;
    }

    public save(element : any) {
      this.service.save(element).subscribe(() => { this.router.navigate(['/system_property/list']) });
    }

    public promptDelete(key : string) {
      const dialogRef = this.dialog.open(ConfirmDeleteDialog, {
        width: '250px',
        data: true,
      });
  
      dialogRef.afterClosed().subscribe(result => {
        if (result !== true) {
          return;
        }

        this.service.delete(key).subscribe(() => this.router.navigate(['/system_property/list']));
      });
    }

    private initDefaultDataTable() {
      this.datasource = new ArrayDataSource<SystemProperty>([]);
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_API_KEY;
    }
}