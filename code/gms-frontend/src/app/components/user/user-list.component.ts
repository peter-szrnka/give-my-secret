import { Component } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseListComponent } from "../../common/components/abstractions/base-list.component";
import { PageConfig } from "../../common/model/common.model";
import { UserData, PAGE_CONFIG_USER } from "../../common/model/user-data.model";
import { SharedDataService } from "../../common/service/shared-data-service";
import { UserService } from "../../common/service/user-service";


@Component({
    selector: 'user-list-component',
    templateUrl: './user-list.component.html',
    styleUrls: ['./user-list.component.scss']
})
export class UserListComponent extends BaseListComponent<UserData, UserService> {

    userColumns: string[] = [ 'id','username', 'email' , 'status', 'roles', 'creationDate', 'operations' ];

    constructor(
      override router : Router,
      override sharedData : SharedDataService, 
      override service : UserService,
      public override dialog: MatDialog,
      override activatedRoute: ActivatedRoute) {
        super(router, sharedData, service, dialog, activatedRoute);
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_USER;
    }
}