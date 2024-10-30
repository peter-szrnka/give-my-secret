import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { DialogData } from "./dialog-data.model";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'info-dialog',
    styleUrls: ['./info-dialog.component.scss'],
    templateUrl: './info-dialog.component.html'
  })
  export class InfoDialog {

    TITLE_MAP : any = {
      'information' : 'dialog.information',
      'warning' : 'dialog.warning'
    };

    constructor(
      public dialogRef: MatDialogRef<InfoDialog>,
      @Inject(MAT_DIALOG_DATA) public data: DialogData
    ) {}
  
    closeDialog(): void {
      this.dialogRef.close();
    }
}