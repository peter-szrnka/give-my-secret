import { Injectable } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ConfirmDeleteDialog } from "../components/confirm-delete/confirm-delete-dialog.component";
import { InfoDialog } from "../components/info-dialog/info-dialog.component";
import { TranslatorService } from "./translator-service";

export interface ConfirmDeleteDialogData {
    confirmMessageKey: string;
    arg?: string;
}

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class DialogService {

    constructor(private readonly dialog: MatDialog, private readonly translatorService : TranslatorService) { }

    openCustomDialog(text: string, type: string) : MatDialogRef<InfoDialog, any> {
        // TODO CHECK
        return this.dialog.open(InfoDialog, {
            data: { text: text, type: type }
        });
    }

    openCustomDialogWithErrorCode(text: string, type: string, errorCode?: string) : MatDialogRef<InfoDialog, any> {
        return this.dialog.open(InfoDialog, {
            // TODO CHECK
            data: { text: this.translatorService.translate(text), type: type, errorCode: errorCode }
        });
    }

    openInfoDialogWithoutTitle(text: string): MatDialogRef<InfoDialog, any> {
        return this.dialog.open(InfoDialog, {
            // TODO CHECK
            data: { text: this.translatorService.translate(text), type: 'information' }
        });
    }

    openInfoDialog(title: string, text: string): MatDialogRef<InfoDialog, any> {
        return this.dialog.open(InfoDialog, {
            // TODO CHECK
            data: { title: title, text: this.translatorService.translate(text), type: 'information' }
        });
    }

    openWarningDialog(text: string): MatDialogRef<InfoDialog, any> {
        return this.openCustomDialog(text, 'warning');
    }

    openConfirmDeleteDialog(data: ConfirmDeleteDialogData): MatDialogRef<ConfirmDeleteDialog, any> {
        return this.dialog.open(ConfirmDeleteDialog, {
            data: {
                result: true,
                confirmMessage: this.translatorService.translate(data.confirmMessageKey, data.arg)
            }
        });
    }
}