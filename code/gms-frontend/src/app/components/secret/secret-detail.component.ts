import { ArrayDataSource } from "@angular/cdk/collections";
import { COMMA, ENTER } from "@angular/cdk/keycodes";
import { AsyncPipe } from "@angular/common";
import { Component, ElementRef, ViewChild } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatAutocompleteSelectedEvent } from "@angular/material/autocomplete";
import { MatChipInputEvent } from "@angular/material/chips";
import { ActivatedRoute, Router } from "@angular/router";
import { Observable } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { BaseDetailComponent } from "../../common/components/abstractions/component/base-detail.component";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { ButtonConfig } from "../../common/components/nav-back/button-config";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { PageConfig } from "../../common/model/common.model";
import { IdNamePair } from "../../common/model/id-name-pair.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorCode, getErrorMessage } from "../../common/utils/error-utils";
import { ApiKeyService } from "../apikey/service/apikey-service";
import { IpRestriction } from "../ip_restriction/model/ip-restriction.model";
import { KeystoreService } from "../keystore/service/keystore-service";
import { PAGE_CONFIG_SECRET, Secret } from "./model/secret.model";
import { SecretService } from "./service/secret-service";

interface KeyValuePair {
    key: string,
    value: string;
}

export enum ValidationState {
    UNDEFINED = 'UNDEFINED',
    VALID = 'VALID',
    INVALID = 'INVALID',
    INVALID_INPUT = 'INVALID_INPUT',
    IN_PROGRESS = 'IN_PROGRESS'
}

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'secret-detail',
    templateUrl: './secret-detail.component.html',
    styleUrls: ['./secret-detail.component.scss'],
    imports: [
        AngularMaterialModule,
        FormsModule,
        AsyncPipe,
        NavBackComponent,
        MomentPipe,
        InformationMessageComponent,
        TranslatorModule
    ]
})
export class SecretDetailComponent extends BaseDetailComponent<Secret, SecretService> {

    buttonConfig: ButtonConfig[] = [
        { primary: true, url: '/secret/list', label: 'navback.back2List' }
    ];

    rotationPeriods: string[] = [
        'MINUTES', 'HOURLY', 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'
    ];
    displayedColumns: string[] = ['key', 'value', 'operations'];
    displayedIpRestrictionColumns: string[] = ['ipPattern', 'allow', 'operations'];

    public ipRestrictionsDatasource: ArrayDataSource<IpRestriction>;
    public datasource: ArrayDataSource<KeyValuePair>;
    multipleCredential: KeyValuePair[] = [];
    filteredKeystoreOptions$: Observable<IdNamePair[]>;
    filteredKeystoreAliasOptions$: Observable<IdNamePair[]>;
    filteredApiKeyOptions$: Observable<IdNamePair[]>;
    selectableApiKeys: IdNamePair[] = [];
    selectedApiKeys: IdNamePair[] = [];
    allApiKeys: IdNamePair[] = [];
    ipRestrictions: IpRestriction[] = [];

    formData = {
        unmaskedValue: undefined,
        allApiKeysAllowed: true
    };

    valueDisplayed = false;
    private keyPressTimeout: any;
    validationState: ValidationState = ValidationState.UNDEFINED;

    readonly separatorKeysCodes = [ENTER, COMMA] as const;
    @ViewChild('roleInput') roleInput: ElementRef<HTMLInputElement>;

    constructor(
        protected override router: Router,
        protected override sharedData: SharedDataService,
        protected override service: SecretService,
        public override dialog: DialogService,
        protected override activatedRoute: ActivatedRoute,
        private readonly keystoreService: KeystoreService,
        private readonly apiKeyService: ApiKeyService,
        protected override splashScreenStateService: SplashScreenStateService) {
        super(router, sharedData, service, dialog, activatedRoute, splashScreenStateService);
    }

    override getPageConfig(): PageConfig {
        return PAGE_CONFIG_SECRET;
    }

    override ngOnInit(): void {
        super.ngOnInit();
        this.filteredKeystoreOptions$ = this.keystoreService.getAllKeystoreNames();
        this.filteredApiKeyOptions$ = this.apiKeyService.getAllApiKeyNames();

        this.filteredApiKeyOptions$.subscribe(apiKeyOptions => {
            this.selectableApiKeys = apiKeyOptions;
            this.allApiKeys = apiKeyOptions;

            this.selectedApiKeys = this.selectableApiKeys.filter(apiKey => this.data.apiKeyRestrictions.indexOf(apiKey.id) >= 0);
            this.formData.allApiKeysAllowed = this.selectedApiKeys.length === 0;

            this.refreshSelectableRoles();
        });
        this.valueDisplayed = this.data.id === undefined;
        this.validateSecretLength();
    }

    override dataLoadingCallback(data: Secret): void {
        this.onKeystoreNameChanged(data.keystoreId);
        this.ipRestrictions = this.data.ipRestrictions || [];
        this.refreshIpRestrictions();

        if (data.type !== 'MULTIPLE_CREDENTIAL') {
            return;
        }

        this.multipleCredential = this.parseValue(data.value);
        this.refreshTable();

    }

    save() {
        this.splashScreenStateService.start();
        this.data.apiKeyRestrictions = this.formData.allApiKeysAllowed ? [] : this.selectedApiKeys.map(apiKey => apiKey.id);
        this.addMissingDataToIpRestrictions();
        this.transformMultipleCredentials();
        this.validateForm();
        this.cleanOptionalFields();

        this.service.save(this.data)
        .subscribe({
            next: () => {
                this.openInformationDialog("dialog.save." + this.getPageConfig().scope, true, 'information');
            },
            error: (err) => {
                this.openInformationDialog("dialog.save.error", false, 'warning', getErrorMessage(err), getErrorCode(err));
            },
            complete: () => {
                this.splashScreenStateService.stop();
            }
        });
    }

    showValue() {
        this.service.getValue(this.data.id).subscribe(value => {
            this.data.value = value;
            this.valueDisplayed = true;

            if (this.data.type !== 'MULTIPLE_CREDENTIAL') {
                return;
            }

            this.multipleCredential = this.parseValue(this.data.value);
            this.refreshTable();
            this.validateSecretLength();
        });
    }

    rotateSecret(): void {
        this.service.rotate(this.data.id).subscribe({
            next: () => {
                this.openInformationDialog("dialog.secret.rotate", false, 'information');
            },
            error: (err) => {
                this.openInformationDialog("dialog.save.error", false, 'warning', getErrorMessage(err), getErrorCode(err));
            }
        });
    }

    onKeystoreNameChanged(selectedId: number | undefined) {
        if (!selectedId) {
            return;
        }

        this.filteredKeystoreAliasOptions$ = this.keystoreService.getAllKeystoreAliases(selectedId);
    }

    addNewMultipleCredential(): void {
        this.multipleCredential.push({ key: "", value: "" });
        this.refreshTable();
        this.validateSecretLength();
    }

    deleteMultipleCredential(index: number) {
        this.multipleCredential.splice(index, 1);
        this.refreshTable();
        this.validateSecretLength();
    }

    selected(event: MatAutocompleteSelectedEvent): void {
        this.selectedApiKeys.push({ id: event.option.value, name: event.option.viewValue });
        this.roleInput.nativeElement.value = '';
        this.refreshSelectableRoles();
    }

    add(event: MatChipInputEvent): void {
        const value = event.value;
        const index = this.getIndex(parseInt(value));

        if (!index || index < 0) {
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            event.chipInput.clear();
            return;
        }

        if (value) {
            this.data.apiKeyRestrictions.push(parseInt(value));
        }

        this.refreshSelectableRoles();
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        event.chipInput.clear();
    }

    remove(apiKey: IdNamePair): void {
        const index = this.selectedApiKeys.indexOf(apiKey);

        if (index < 0) {
            return;
        } else if (index === 0) {
            this.selectedApiKeys.pop();
            this.refreshSelectableRoles();
            return;
        }

        this.selectedApiKeys.splice(index, 1);
        this.refreshSelectableRoles();
    }

    addNewIpRestriction() {
        this.ipRestrictions.push({ allow: true, ipPattern: '' });
        this.refreshIpRestrictions();
    }

    deleteIpRestriction(index : number) {
        this.ipRestrictions?.splice(index, 1);
        this.refreshIpRestrictions();
    }

    onKeyUp(_$event: any, timeout: number) {
        if (this.keyPressTimeout) {
            clearTimeout(this.keyPressTimeout);
        }

        this.keyPressTimeout = setTimeout(() => this.validateSecretLength(), timeout);
    }

    validateSecretLength() {
        if (this.valueDisplayed === false) {
            return;
        }

        this.transformMultipleCredentials();

        if (this.data.value.length === 0 || this.data.keystoreId === undefined || this.data.keystoreAliasId === undefined) {
            this.validationState = ValidationState.INVALID_INPUT;
            return;
        }

        this.validationState = ValidationState.IN_PROGRESS;

        this.service.validateLength({
            keystoreId: this.data.keystoreId,
            keystoreAliasId: this.data.keystoreAliasId,
            value: this.data.value
        }).subscribe({
            next: (result) => this.validationState = result.value ? ValidationState.VALID : ValidationState.INVALID,
            error: () => {
                this.validationState = ValidationState.INVALID;
            }
        });
    }

    private refreshTable() {
        this.datasource = new ArrayDataSource<KeyValuePair>(this.multipleCredential);
    }

    private refreshIpRestrictions() {
        this.ipRestrictionsDatasource = new ArrayDataSource<IpRestriction>(this.ipRestrictions);
    }

    private parseValue(data: string): KeyValuePair[] {
        if (!data) {
            return [];
        }

        return data.split(";").map(kvp => {
            const splitData = kvp.split(":");
            return { key: splitData[0], value: splitData[1] };
        });
    }

    private transformMultipleCredentials() {
        if (this.data.type !== 'MULTIPLE_CREDENTIAL') {
            return;
        }

        this.data.value = this.multipleCredential.map(item => item.key + ":" + item.value).join(";");
    }

    private validateForm() {
        if ((this.data.keystoreAliasId === undefined)) {
            throw Error("Please select a keystore alias!");
        }
    }

    private cleanOptionalFields() {
        this.data.creationDate = undefined;
        this.data.lastRotated = undefined;
        this.data.lastUpdated = undefined;
    }

    private refreshSelectableRoles(): void {
        this.selectableApiKeys = this.allApiKeys;
        this.selectableApiKeys = this.selectableApiKeys.filter((item) => {
            return !this.selectedApiKeys.map(apiKey => apiKey.id).includes(item.id);
        });
    }

    private getIndex(value: number) {
        return this.selectedApiKeys.map(apiKey => apiKey.id).indexOf(value);
    }
    
    private addMissingDataToIpRestrictions(): void {
        this.data.ipRestrictions = this.ipRestrictions;
        this.data.ipRestrictions?.forEach(item => item.secretId = this.data.id);
    }
}