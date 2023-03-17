import { Component, EventEmitter, Input, Output } from '@angular/core';

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
    @Output() callbackFunction: EventEmitter<any> = new EventEmitter();

    public toggle() {
        if (this.doNotToggle) {
            return;
        }

        this.callbackFunction.emit();
    }
}