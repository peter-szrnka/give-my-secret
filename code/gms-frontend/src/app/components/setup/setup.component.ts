import { Component, Inject, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { WINDOW_TOKEN } from "../../window.provider";
import { SystemProperty } from "../system_property/model/system-property.model";
import { UserData } from "../user/model/user-data.model";
import { SetupService } from "./service/setup-service";

interface VmOption {
    key: string;
    value: string;
}

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
    styleUrls: ['./setup.component.scss']
})
export class SetupComponent implements OnInit {

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
        private readonly setupService : SetupService) {}

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            this.systemStatus = params['systemStatus'];
            this.currentStep = SYSTEM_STATUS_INDEX[this.systemStatus];

            if (this.systemStatus === 'NEED_ADMIN_USER') {
                this.getCurrentAdminUserData();
            }
        });

        this.getVmOptions();
    }

    getVmOptions() {
        this.prepareHttpCall();

        this.setupService.getVmOptions().subscribe(data => {
                this.splashScreenService.stop();
                Object.keys(data).forEach(key => this.vmOptions.push({key: key, value: data[key]}) );
                this.loading = false;
        });
    }

    stepBack() {
        this.prepareHttpCall();

        this.setupService.stepBack().subscribe({
            next: (newStatus) => this.handleNextStep(newStatus),
            error: (err) => this.handleError(err)
        });
    }

    getCurrentAdminUserData() {
        this.prepareHttpCall();

        this.setupService.getAdminUserData().subscribe({
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

        this.setupService.saveInitialStep().subscribe({
            next: () => this.handleNextStep('NEED_ADMIN_USER'),
            error: (err) => this.handleError(err)
        });
    }

    saveAdminUser() {
        this.prepareHttpCall();
        this.userData.role = 'ROLE_ADMIN';

        this.setupService.saveAdminUser(this.userData).subscribe({
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

        this.setupService.saveSystemProperties(systemProperties).subscribe({
            next: () => this.handleNextStep('NEED_ORG_DATA'),
            error: (err) => this.handleError(err)
        });
    }

    saveOrganizationData() {
        this.prepareHttpCall();
        const systemProperties: SystemProperty[] = [];

        systemProperties.push(this.initPropertyData("ORGANIZATION_NAME"));
        systemProperties.push(this.initPropertyData("ORGANIZATION_CITY"));

        this.setupService.saveOrganizationData(systemProperties).subscribe({
            next: () => this.handleNextStep('COMPLETE'),
            error: (err) => this.handleError(err)
        });
    }

    navigateToHome() : void {
        this.setupService.completeSetup().subscribe({
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