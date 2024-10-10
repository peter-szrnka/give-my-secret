import { Injectable } from "@angular/core";
import { Clipboard } from '@angular/cdk/clipboard';
import { MatSnackBar } from "@angular/material/snack-bar";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class ClipboardService {

    constructor(private readonly clipboard: Clipboard, private readonly snackbar : MatSnackBar) { }

    public copyValue(value: string, snackbarMessage : string) {
        const pending = this.clipboard.beginCopy(value);
        let remainingAttempts = 3;
        const attempt = () => {
            const result = pending.copy();
            if (!result && --remainingAttempts) {
                setTimeout(attempt);
            } else {
                pending.destroy();
                this.snackbar.open(snackbarMessage);
            }
        };
        attempt();
    }
}