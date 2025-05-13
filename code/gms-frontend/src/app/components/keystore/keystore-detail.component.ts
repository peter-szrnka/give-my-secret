/* eslint-disable @typescript-eslint/no-explicit-any */
import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, ElementRef, ViewChild } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { environment } from "../../../environments/environment";
import { BaseDetailComponent } from "../../common/components/abstractions/component/base-detail.component";
import { PageConfig } from "../../common/model/common.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { KeystoreAlias } from "./model/keystore-alias.model";
import { Keystore, PAGE_CONFIG_KEYSTORE } from "./model/keystore.model";
import { KeystoreService } from "./service/keystore-service";

const ENABLED_ALGORITHMS : string[] = [
  "MD2WITHRSA",
  "MD5WITHRSA",
  "SHA1WITHRSA",
  "SHA224WITHRSA",
  "SHA256WITHRSA",
  "SHA384WITHRSA",
  "SHA512WITHRSA",
  "SHA512_224_WITHRSA",
  "SHA512_256_WITHRSA",
  "SHA1WITHRSAANDMGF1",
  "SHA224WITHRSAANDMGF1",
  "SHA256WITHRSAANDMGF1",
  "SHA384WITHRSAANDMGF1",
  "SHA512WITHRSAANDMGF1",
  "SHA3_224WITHRSAANDMGF1",
  "SHA3_256WITHRSAANDMGF1",
  "SHA3_384WITHRSAANDMGF1",
  "SHA3_512WITHRSAANDMGF1"
];

/**
 * @author Peter Szrnka
 */
@Component({
  selector: 'keystore-detail',
  templateUrl: './keystore-detail.component.html',
  styleUrls: ['./keystore-detail.component.scss']
})
export class KeystoreDetailComponent extends BaseDetailComponent<Keystore, KeystoreService> {

  @ViewChild('fileInput') fileInput: ElementRef;
  fileAttr = 'Choose File';
  file: File;

  displayedColumns: string[] = ['alias','aliasCredential', 'algorithm', 'operations'];

  public datasource : ArrayDataSource<KeystoreAlias>;
  aliasList : KeystoreAlias[] = [];
  allAliasesAreValid : boolean;
  enabledAlgorithms : string[] = ENABLED_ALGORITHMS;
  showCredential : boolean = false;

  constructor(
    protected override router: Router,
    protected override sharedData: SharedDataService,
    protected override service: KeystoreService,
    public override dialog: DialogService,
    protected override activatedRoute: ActivatedRoute,
    protected override splashScreenStateService: SplashScreenStateService) {
    super(router, sharedData, service, dialog, activatedRoute, splashScreenStateService);
  }

  override getPageConfig(): PageConfig {
    return PAGE_CONFIG_KEYSTORE;
  }

  override dataLoadingCallback(data: Keystore): void {
    this.aliasList = data.aliases;
    this.datasource = new ArrayDataSource<KeystoreAlias>(this.aliasList.map(alias => {
      alias.operation = 'SAVE'; 
      alias.showCredential = false;
      return alias;
    }));
  }

  save() {
    this.addAliasDataToRequest();
    this.splashScreenStateService.start();
    this.service.save(this.data, (this.data.generated === true) ? undefined : this.file)
      .subscribe({
        next: () => {
          this.openInformationDialog("dialog.save." + this.getPageConfig().scope, true, 'information');
        },
        error: (err) => {
          this.splashScreenStateService.stop();
          this.openInformationDialog("dialog.save.error", false, 'warning', getErrorMessage(err));
        },
        complete: () => {
            this.splashScreenStateService.stop();
        }
      });
  }

  uploadFileEvt(imgFile: any) {
    if (imgFile.target.files[0]) {
      this.fileAttr = '';
      Array.from(imgFile.target.files).forEach((file: any) => {
        this.fileAttr += file.name + ' - ';
        this.file = file;
      });
      // HTML5 FileReader API
      const reader = new FileReader();
      reader.readAsDataURL(imgFile.target.files[0]);
      // Reset if duplicate image uploaded again
      this.fileInput.nativeElement.value = '';
    } else {
      this.fileAttr = 'Choose File';
    }
  }

 public downloadKeystore() : void {
    const link = document.createElement('a');
    document.body.appendChild(link);
    link.setAttribute('style', 'display: none');
    link.href = environment.baseUrl + "secure/keystore/download/" + this.data.id;
    link.download = this.data.fileName ?? "keystore.jks";
    link.click();

    document.body.removeChild(link);
  }

  addNewAlias() {
    this.aliasList.push({ alias: '', aliasCredential: '', operation : 'SAVE' });
    this.refreshTable();
  }

  refreshTable() {
    this.datasource = new ArrayDataSource<KeystoreAlias>(this.aliasList);
    this.validateAliases();
  }

  changeState(element : KeystoreAlias, index : number, newState : string) {
    if (element.id === undefined) {
      this.aliasList.splice(index, 1);
    } else {
      element.operation = newState;
    }

    this.refreshTable();
  }

  toggleCredentialDisplay() {
    this.showCredential = !this.showCredential;
  }

  toggleAliasCredentialDisplay(alias : KeystoreAlias) {
    alias.showCredential = !alias.showCredential;
  }

  private addAliasDataToRequest() : void {
    this.data.aliases = this.aliasList;
    this.data.fileName = undefined;
  }

  private validateAliases() : void {
    this.allAliasesAreValid = this.data.aliases && this.data.aliases.filter(alias=>alias.operation!=='DELETE').length > 0;
  }
}