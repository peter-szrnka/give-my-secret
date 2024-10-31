import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

const DEFAULT_DELETE_MESSAGE = 'dialog.defaultDeleteMessage';

export interface ConfirmDeleteDialogData {
  confirmMessage?: string;
  result: boolean;
}

/**
 * @author Peter Szrnka
 */
@Component({
  selector: 'confirm-delete-dialog',
  templateUrl: './confirm-delete-dialog.component.html',
})
export class ConfirmDeleteDialog {

  message: string;
  noData: ConfirmDeleteDialogData = { result: false };

  constructor(
    public dialogRef: MatDialogRef<ConfirmDeleteDialog>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDeleteDialogData
  ) {
    this.message = data.confirmMessage ?? DEFAULT_DELETE_MESSAGE;
  }
}