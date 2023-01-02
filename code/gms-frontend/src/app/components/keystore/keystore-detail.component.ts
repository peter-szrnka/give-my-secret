/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, ElementRef, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseDetailComponent } from "../../common/components/abstractions/base-detail.component";
import { PageConfig } from "../../common/model/common.model";
import { Keystore, PAGE_CONFIG_KEYSTORE } from "../../common/model/keystore.model";
import { KeystoreService } from "../../common/service/keystore-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { getErrorMessage } from "../../common/utils/error-utils";

@Component({
  selector: 'keystore-detail',
  templateUrl: './keystore-detail.component.html',
  styleUrls: ['./keystore-detail.component.scss']
})
export class KeystoreDetailComponent extends BaseDetailComponent<Keystore, KeystoreService> {

  @ViewChild('fileInput') fileInput: ElementRef;
  fileAttr = 'Choose File';
  file: File;

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

  save() {
    this.loading = true;
    this.service.save(this.data, this.file)
      .subscribe({
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

  uploadFileEvt(imgFile: any) {
    if (imgFile.target.files && imgFile.target.files[0]) {
      this.fileAttr = '';
      Array.from(imgFile.target.files).forEach((file: any) => {
        this.fileAttr += file.name + ' - ';
        this.file = file;
      });
      // HTML5 FileReader API
      const reader = new FileReader();
      //?reader.onload = (e: any) => {};
      reader.readAsDataURL(imgFile.target.files[0]);
      // Reset if duplicate image uploaded again
      this.fileInput.nativeElement.value = '';
    } else {
      this.fileAttr = 'Choose File';
    }
  }
}