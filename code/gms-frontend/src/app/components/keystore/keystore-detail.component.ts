/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, ElementRef, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseDetailComponent } from "../../common/components/abstractions/component/base-detail.component";
import { PageConfig } from "../../common/model/common.model";
import { Keystore, PAGE_CONFIG_KEYSTORE } from "./model/keystore.model";
import { KeystoreService } from "./service/keystore-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { KeystoreAlias } from "./model/keystore-alias.model";
import { ArrayDataSource } from "@angular/cdk/collections";

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

  displayedColumns: string[] = ['alias','aliasCredential', 'operations'];

  public datasource : ArrayDataSource<KeystoreAlias>;
  aliasList : KeystoreAlias[] = [];
  allAliasesAreValid : boolean;

  constructor(
    protected override router: Router,
    protected override sharedData: SharedDataService,
    protected override service: KeystoreService,
    public override dialog: MatDialog,
    protected override activatedRoute: ActivatedRoute) {
    super(router, sharedData, service, dialog, activatedRoute);
  }

  override getPageConfig(): PageConfig {
    return PAGE_CONFIG_KEYSTORE;
  }

  override dataLoadingCallback(data: Keystore): void {
    this.aliasList = data.aliases;
    this.datasource = new ArrayDataSource<KeystoreAlias>(this.aliasList.map(alias => {alias.operation = 'SAVE'; return alias;}));
  }

  save() {
    this.addAliasDataToRequest();
    this.service.save(this.data, this.file)
      .subscribe({
        next: () => {
          this.openInformationDialog(this.getPageConfig().label + " has been saved!", true, 'information');
        },
        error: (err) => {
          this.openInformationDialog("Error: " + getErrorMessage(err), false, 'warning');
        }
      });
  }

  uploadFileEvt(imgFile: any) {
    if (imgFile.target.files && imgFile.target.files[0]) {
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

  addAliasDataToRequest() : void {
    this.data.aliases = this.aliasList;
  }

  private validateAliases() : void {
    this.allAliasesAreValid = this.data.aliases && this.data.aliases.filter(alias=>alias.operation!=='DELETE').length > 0;
  }
}