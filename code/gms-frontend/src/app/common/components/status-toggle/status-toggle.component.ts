import { NgIf } from '@angular/common';
import { Component, CUSTOM_ELEMENTS_SCHEMA, Input, NO_ERRORS_SCHEMA } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AngularMaterialModule } from '../../../angular-material-module';

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [
        AngularMaterialModule,
        NgIf
    ],
    providers: [
        MatSnackBar
    ],
    schemas: [ CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA ], 
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