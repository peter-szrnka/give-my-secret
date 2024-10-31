import { Injectable } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ConfirmDeleteDialog, ConfirmDeleteDialogData } from "../components/confirm-delete/confirm-delete-dialog.component";
import { DialogData } from "../components/info-dialog/dialog-data.model";
import { InfoDialog } from "../components/info-dialog/info-dialog.component";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class DialogService {

    constructor(private readonly dialog: MatDialog) { }

    openNewDialog(data: DialogData): MatDialogRef<InfoDialog, any> {
        return this.dialog.open(InfoDialog, { data: data });
    }

    openConfirmDeleteDialog(data: ConfirmDeleteDialogData): MatDialogRef<ConfirmDeleteDialog, any> {
        return this.dialog.open(ConfirmDeleteDialog, { data: data });
    }
}