import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { AngularMaterialModule } from "../../../angular-material-module";
import { TranslatorModule } from "../pipes/translator/translator.module";

const DEFAULT_DELETE_MESSAGE = 'dialog.defaultDeleteMessage';

export interface ConfirmDeleteDialogData {
  result: boolean;
  key?: string;
  arg?: string;
}

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'confirm-delete-dialog',
    templateUrl: './confirm-delete-dialog.component.html',
    imports: [AngularMaterialModule, TranslatorModule]
})
export class ConfirmDeleteDialog {

  key: string;
  noData: ConfirmDeleteDialogData = { key: 'noKey', result: false };

  constructor(
    public dialogRef: MatDialogRef<ConfirmDeleteDialog>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDeleteDialogData
  ) {
    this.key = data.key ?? DEFAULT_DELETE_MESSAGE;
  }
}