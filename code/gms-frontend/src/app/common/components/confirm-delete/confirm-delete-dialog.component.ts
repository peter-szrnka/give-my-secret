import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";

const DEFAULT_DELETE_MESSAGE = 'Do you really want to delete this entity?';

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

  constructor(
    public dialogRef: MatDialogRef<ConfirmDeleteDialog>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDeleteDialogData
  ) {
    this.message = data.confirmMessage ?? DEFAULT_DELETE_MESSAGE;
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}