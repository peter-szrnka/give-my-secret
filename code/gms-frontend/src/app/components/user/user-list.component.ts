import { Component } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { PageConfig } from "../../common/model/common.model";
import { UserData, PAGE_CONFIG_USER } from "./model/user-data.model";
import { SharedDataService } from "../../common/service/shared-data-service";
import { UserService } from "./service/user-service";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'user-list-component',
    templateUrl: './user-list.component.html',
    styleUrls: ['./user-list.component.scss']
})
export class UserListComponent extends BaseListComponent<UserData, UserService> {

    userColumns: string[] = ['id', 'username', 'email', 'status', 'roles', 'creationDate', 'operations'];
    editEnabled: boolean = true;

    constructor(
        override router: Router,
        override sharedData: SharedDataService,
        override service: UserService,
        public override dialog: MatDialog,
        override activatedRoute: ActivatedRoute,
        private splashScreenService: SplashScreenStateService) {
        super(router, sharedData, service, dialog, activatedRoute);
    }

    override async ngOnInit(): Promise<void> {
        super.ngOnInit();

        this.sharedData.authModeSubject$.subscribe(authMode => this.editEnabled = authMode === 'db');
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_USER;
    }

    manualLdapUserSync() {
        if (this.editEnabled === true) {
            return;
        }

        this.splashScreenService.start();
        this.service.manualLdapUserSync().subscribe(() => this.handleLdapSyncResult());
    }

    handleLdapSyncResult(): void {
        this.splashScreenService.stop();
        const dialogRef = this.dialog.open(InfoDialog, {
            width: '250px',
            data: { type: "information", text: "LDAP users synchronized successfully!" }
        });

        dialogRef.afterClosed().subscribe(() => this.reloadPage());
    }
}