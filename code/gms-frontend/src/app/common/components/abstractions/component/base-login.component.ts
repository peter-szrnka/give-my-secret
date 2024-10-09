import { Directive, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute } from "@angular/router";
import { Observable, of } from "rxjs";
import { User } from "../../../../components/user/model/user.model";
import { checker } from "../../../interceptor/role-guard";
import { SharedDataService } from "../../../service/shared-data-service";
import { SplashScreenStateService } from "../../../service/splash-screen-service";
import { ROLE_ROUTE_MAP } from "../../../utils/route-utils";
import { DialogData } from "../../info-dialog/dialog-data.model";
import { InfoDialog } from "../../info-dialog/info-dialog.component";

/**
 * @author Peter Szrnka
 */
@Directive()
export abstract class BaseLoginComponent implements OnInit {

    constructor(
        protected route: ActivatedRoute,
        protected sharedDataService: SharedDataService,
        protected dialog: MatDialog,
        protected splashScreenStateService: SplashScreenStateService
    ) {}

    ngOnInit(): void {
        this.splashScreenStateService.stop();
    }
    
    private showErrorModal() {
        this.dialog.open(InfoDialog, {
            width: '250px',
            data: { text : "Login failed!", type : 'warning' } as DialogData
        });
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    protected handleError(err : any): Observable<any> {  
        console.error("Unexpected error occurred during login", err);
        return of(null);
    }

    protected displayErrorModal() {
        this.splashScreenStateService.stop();
        this.showErrorModal();
    }

    protected finalizeSuccessfulLogin(currentUser : User) {
        this.splashScreenStateService.stop();
        this.sharedDataService.refreshCurrentUserInfo();
        const previousUrl: string = this.getPreviousUrl();
        const expectedRoles = previousUrl ? (ROLE_ROUTE_MAP[previousUrl.substring(1)] ?? []) : [];
        const canActivate = checker(expectedRoles, currentUser.role);
        this.sharedDataService.navigationChangeEvent.emit(canActivate === true ? previousUrl : '');
    }

    private getPreviousUrl() {
        return (this.route.snapshot.queryParams['previousUrl'] && this.route.snapshot.queryParams['previousUrl'] !== '') ? this.route.snapshot.queryParams['previousUrl'] : '';
    }
}