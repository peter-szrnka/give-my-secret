import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'confirm-delete-dialog',
    templateUrl: './confirm-delete-dialog.component.html',
  })
  export class ConfirmDeleteDialog {
    constructor(
      public dialogRef: MatDialogRef<ConfirmDeleteDialog>,
      @Inject(MAT_DIALOG_DATA) public data: boolean,
    ) {}
  
    onNoClick(): void {
      this.dialogRef.close();
    }
}