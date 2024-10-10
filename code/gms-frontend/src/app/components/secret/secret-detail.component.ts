import { ENTER, COMMA } from "@angular/cdk/keycodes";
import { Component, ElementRef, ViewChild } from "@angular/core";
import { MatAutocompleteSelectedEvent } from "@angular/material/autocomplete";
import { MatChipInputEvent } from "@angular/material/chips";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { Observable } from "rxjs";
import { BaseDetailComponent } from "../../common/components/abstractions/component/base-detail.component";
import { PageConfig } from "../../common/model/common.model";
import { IdNamePair } from "../../common/model/id-name-pair.model";
import { PAGE_CONFIG_SECRET, Secret } from "./model/secret.model";
import { ApiKeyService } from "../apikey/service/apikey-service";
import { KeystoreService } from "../keystore/service/keystore-service";
import { SecretService } from "./service/secret-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { ArrayDataSource } from "@angular/cdk/collections";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { IpRestriction } from "../ip_restriction/model/ip-restriction.model";
import { ButtonConfig } from "../../common/components/nav-back/button-config";

interface KeyValuePair {
    key: string,
    value: string;
}

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'secret-detail',
    templateUrl: './secret-detail.component.html',
    styleUrls: ['./secret-detail.component.scss']
})
export class SecretDetailComponent extends BaseDetailComponent<Secret, SecretService> {

    buttonConfig: ButtonConfig[] = [
        { primary: true, url: '/secret/list', label: 'Back to list' }
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

    readonly separatorKeysCodes = [ENTER, COMMA] as const;
    @ViewChild('roleInput') roleInput: ElementRef<HTMLInputElement>;

    constructor(
        protected override router: Router,
        protected override sharedData: SharedDataService,
        protected override service: SecretService,
        public override dialog: MatDialog,
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

        this.service.save(this.data).subscribe({
            next: () => {
                this.openInformationDialog(this.getPageConfig().label + " has been saved!", true, 'information');
            },
            error: (err) => {
                this.openInformationDialog("Error: " + getErrorMessage(err), false, 'warning');
            },
            complete: () => {
                this.splashScreenStateService.stop();
            }
        });
    }

    showValue() {
        this.service.getValue(this.data.id).subscribe(value => {
            this.data.value = value;

            if (this.data.type !== 'MULTIPLE_CREDENTIAL') {
                return;
            }

            this.multipleCredential = this.parseValue(this.data.value);
            this.refreshTable();
        });
    }

    rotateSecret(): void {
        this.service.rotate(this.data.id).subscribe({
            next: () => {
                this.openInformationDialog("Secret has been rotated successfully!", false, 'information');
            },
            error: (err) => {
                this.openInformationDialog("Unexpected error occurred: " + getErrorMessage(err), false, 'warning');
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
    }

    deleteMultipleCredential(index: number) {
        this.multipleCredential.splice(index, 1);
        this.refreshTable();
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