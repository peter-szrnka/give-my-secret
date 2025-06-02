import { Component, Inject } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { takeUntil } from "rxjs";
import { BaseComponent } from "../../common/components/abstractions/component/base.component";
import { VmOption } from "../../common/model/common.model";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { WINDOW_TOKEN } from "../../window.provider";
import { SystemProperty } from "../system_property/model/system-property.model";
import { UserData } from "../user/model/user-data.model";
import { SetupService } from "./service/setup-service";

const SYSTEM_STATUS_INDEX: {[key:string] :number} = {
    "NEED_SETUP": 0,
    "NEED_ADMIN_USER": 1,
    "NEED_AUTH_CONFIG": 2,
    "NEED_ORG_DATA": 3,
    "COMPLETE": 4
};

export const EMPTY_ADMIN_DATA : UserData = {
    status: 'ACTIVE',
    credential: undefined,
    role: 'ROLE_ADMIN'
};

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'setup-component',
    templateUrl: './setup.component.html',
    styleUrls: ['./setup.component.scss'],
    standalone: false
})
export class SetupComponent extends BaseComponent {

    loading: boolean = true;
    systemStatus: string = '';
    currentStep: number = 0;
    userData : UserData = EMPTY_ADMIN_DATA;
    public errorMessage : string | undefined = undefined;
    vmOptions: VmOption[] = [];
    systemPropertyData: any = {
        "FAILED_ATTEMPTS_LIMIT": 3,
        "ENABLE_AUTOMATIC_LOGOUT": false,
        "AUTOMATIC_LOGOUT_TIME_IN_MINUTES": undefined,
        "ENABLE_MFA": false,
        "ORGANIZATION_NAME": undefined,
        "ORGANIZATION_CITY": undefined
    };

    constructor(
        @Inject(WINDOW_TOKEN) private readonly window: Window,
        private readonly router : Router, 
        private readonly route: ActivatedRoute,
        private readonly splashScreenService : SplashScreenStateService,
        private readonly setupService : SetupService) {
            super();
        }

    override ngOnInit(): void {
        this.route.queryParams.pipe(takeUntil(this.destroy$)).subscribe(params => {
            this.systemStatus = params['systemStatus'];
            this.currentStep = SYSTEM_STATUS_INDEX[this.systemStatus];

            if (this.systemStatus === 'NEED_SETUP') {
                this.loading = false;
                this.splashScreenService.stop();
            } else if (this.systemStatus === 'NEED_ADMIN_USER') {
                this.getCurrentAdminUserData();
            }
        });
    }

    stepBack() {
        this.prepareHttpCall();

        this.setupService.stepBack().pipe(takeUntil(this.destroy$)).subscribe({
            next: (newStatus) => this.handleNextStep(newStatus),
            error: (err) => this.handleError(err)
        });
    }

    getCurrentAdminUserData() {
        this.prepareHttpCall();

        this.setupService.getAdminUserData().pipe(takeUntil(this.destroy$)).subscribe({
            next: (data) => {
                this.splashScreenService.stop();
                this.userData = data ?? EMPTY_ADMIN_DATA;
                this.loading = false;
            },
            error: (err) => this.handleError(err)
        });
    }

    saveInitialStep() {
        this.prepareHttpCall();

        this.setupService.saveInitialStep().pipe(takeUntil(this.destroy$)).subscribe({
            next: () => this.handleNextStep('NEED_ADMIN_USER'),
            error: (err) => this.handleError(err)
        });
    }

    saveAdminUser() {
        this.prepareHttpCall();
        this.userData.role = 'ROLE_ADMIN';

        this.setupService.saveAdminUser(this.userData).pipe(takeUntil(this.destroy$)).subscribe({
            next: () => this.handleNextStep('NEED_AUTH_CONFIG'),
            error: (err) => this.handleError(err)
        });
    }

    saveSystemProperties() {
        this.prepareHttpCall();
        const systemProperties: SystemProperty[] = [];

        systemProperties.push(this.initPropertyData("FAILED_ATTEMPTS_LIMIT"));
        systemProperties.push(this.initPropertyData("ENABLE_AUTOMATIC_LOGOUT"));
        systemProperties.push(this.initPropertyData("AUTOMATIC_LOGOUT_TIME_IN_MINUTES"));
        systemProperties.push(this.initPropertyData("ENABLE_MFA"));

        this.setupService.saveSystemProperties(systemProperties).pipe(takeUntil(this.destroy$)).subscribe({
            next: () => this.handleNextStep('NEED_ORG_DATA'),
            error: (err) => this.handleError(err)
        });
    }

    saveOrganizationData() {
        this.prepareHttpCall();
        const systemProperties: SystemProperty[] = [];

        systemProperties.push(this.initPropertyData("ORGANIZATION_NAME"));
        systemProperties.push(this.initPropertyData("ORGANIZATION_CITY"));

        this.setupService.saveOrganizationData(systemProperties).pipe(takeUntil(this.destroy$)).subscribe({
            next: () => this.handleNextStep('COMPLETE'),
            error: (err) => this.handleError(err)
        });
    }

    navigateToHome() : void {
        this.setupService.completeSetup().pipe(takeUntil(this.destroy$)).subscribe({
            next: () => this.window.location.reload(),
            error: (err) => this.handleError(err)
        });
    }

    private prepareHttpCall(): void {
        this.loading = true;
        this.splashScreenService.start();
    }

    private handleNextStep(newStatus: string): void {
        this.splashScreenService.stop();
        void this.router.navigateByUrl('/setup?systemStatus=' + newStatus);
        this.loading = false;
    }

    private handleError(err: any): void {
        this.splashScreenService.stop();
        if (err.status === 404) {
            void this.router.navigate(['']);
        } else {
            this.errorMessage = getErrorMessage(err);
        }
    }

    private initPropertyData(key: string): SystemProperty {
        return {
            key: key,
            value: this.systemPropertyData[key]
        } as SystemProperty;
    }
}