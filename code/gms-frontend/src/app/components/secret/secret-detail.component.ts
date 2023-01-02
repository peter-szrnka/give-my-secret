import { ENTER, COMMA } from "@angular/cdk/keycodes";
import { Component, ElementRef, ViewChild } from "@angular/core";
import { MatAutocompleteSelectedEvent } from "@angular/material/autocomplete";
import { MatChipInputEvent } from "@angular/material/chips";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { Observable } from "rxjs";
import { BaseDetailComponent } from "../../common/components/abstractions/base-detail.component";
import { PageConfig } from "../../common/model/common.model";
import { IdNamePair } from "../../common/model/id-name-pair.model";
import { PAGE_CONFIG_SECRET, Secret } from "../../common/model/secret.model";
import { ApiKeyService } from "../../common/service/apikey-service";
import { KeystoreService } from "../../common/service/keystore-service";
import { SecretService } from "../../common/service/secret-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { getErrorMessage } from "../../common/utils/error-utils";

@Component({
    selector: 'secret-detail',
    templateUrl: './secret-detail.component.html',
    styleUrls: ['./secret-detail.component.scss']
})
export class SecretDetailComponent extends BaseDetailComponent<Secret, SecretService> {

    rotationPeriods: string[] = [
        'THIRTY_SECONDS', 'MINUTES', 'HOURLY', 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'
    ];

    filteredKeystoreOptions$: Observable<IdNamePair[]>;
    filteredApiKeyOptions$: Observable<IdNamePair[]>;
    selectableApiKeys: IdNamePair[] = [];
    selectedApiKeys: IdNamePair[] = [];
    allApiKeys: IdNamePair[] = [];

    formData = {
        keystoreName: undefined,
        keystoreList: [],
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
        private keystoreService: KeystoreService,
        private apiKeyService: ApiKeyService) {
        super(router, sharedData, service, dialog, activatedRoute);
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
    }

    save() {
        this.data.apiKeyRestrictions = this.formData.allApiKeysAllowed ? [] : this.selectedApiKeys.map(apiKey => apiKey.id);
        this.validateForm();

        this.loading = true;
        this.service.save(this.data).subscribe({
            next: () => {
                this.openInformationDialog(this.getPageConfig().label + " has been saved!", true, 'information');
            },
            error: (err) => {
                this.loading = false;
                this.openInformationDialog("Error: " + getErrorMessage(err), false, 'warning');
            },
            complete: () => {
                this.loading = false;
            }
        });
    }

    showValue() {
        this.service.getValue(this.data.id).subscribe(value => this.data.value = value);
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

    onKeystoreNameChanged(selectedId?: number) {
        this.data.keystoreId = selectedId;
    }

    private validateForm() {
        if ((this.data.keystoreId === undefined)) {
            throw Error("Please select a keystore!");
        }
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
            event.chipInput!.clear();
            return;
        }

        if (value) {
            this.data.apiKeyRestrictions.push(parseInt(value));
        }

        this.refreshSelectableRoles();
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        event.chipInput!.clear();
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
}