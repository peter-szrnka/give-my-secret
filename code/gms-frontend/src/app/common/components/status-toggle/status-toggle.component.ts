import { Component, Input } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'status-toggle',
    templateUrl: './status-toggle.component.html',
    styleUrls : ['./status-toggle.component.scss']
})
export class StatusToggleComponent {

    @Input() entityId : number;
    @Input() status? : string;
    @Input() doNotToggle? : boolean = false;

    constructor(private snackbar : MatSnackBar) {}

    public toggle() {
        if (this.doNotToggle === true) {
            return;
        }

        this.snackbar.open("Entity status changed!");
    }
}