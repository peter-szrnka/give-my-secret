import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { DialogData } from "./dialog-data.model";
import { AngularMaterialModule } from "../../../angular-material-module";
import { TranslatorModule } from "../pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'info-dialog',
    templateUrl: './info-dialog.component.html',
    imports: [AngularMaterialModule, TranslatorModule]
})
export class InfoDialog {

  TITLE_MAP: any = {
    'information': 'dialog.information',
    'warning': 'dialog.warning'
  };
  title: string = '';
  noData: DialogData = { text: 'noText', type: 'information' };

  constructor(
    public dialogRef: MatDialogRef<InfoDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {
    this.title = data.title ?? this.TITLE_MAP[this.data.type];
  }
}