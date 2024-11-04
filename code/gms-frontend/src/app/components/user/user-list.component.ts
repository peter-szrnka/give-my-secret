import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { PageConfig } from "../../common/model/common.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { PAGE_CONFIG_USER, UserData } from "./model/user-data.model";
import { UserService } from "./service/user-service";

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
    authMode: string;

    constructor(
        override router: Router,
        override sharedData: SharedDataService,
        override service: UserService,
        public override dialogService: DialogService,
        override activatedRoute: ActivatedRoute,
        private readonly splashScreenService: SplashScreenStateService) {
        super(router, sharedData, service, dialogService, activatedRoute);
    }

    override async ngOnInit(): Promise<void> {
        super.ngOnInit();

        this.sharedData.authModeSubject$.subscribe(authMode => this.authMode = authMode);
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_USER;
    }

    manualLdapUserSync() {
        if (this.authMode !== 'ldap') {
            return;
        }

        this.splashScreenService.start();
        this.service.manualLdapUserSync().subscribe(() => this.handleLdapSyncResult());
    }

    handleLdapSyncResult(): void {
        this.splashScreenService.stop();
        const dialogRef = this.dialogService.openNewDialog({ text: "dialog.ldapSync.succeeded", type: "information" });

        dialogRef.afterClosed().subscribe(() => this.reloadPage());
    }
}