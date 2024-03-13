import { CUSTOM_ELEMENTS_SCHEMA, Component, Input, NO_ERRORS_SCHEMA } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonConfig } from './button-config';
import { PipesModule } from '../pipes/pipes.module';

/**
 * @author Peter Szrnka
 */
@Component({ 
    selector: 'nav-back',
    templateUrl: './nav-back.component.html',
    styleUrls : ['./nav-back.component.scss'],
    imports: [ PipesModule ],
    schemas: [ CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA ],
    standalone: true
})
export class NavBackComponent {

    @Input() backToHome? : boolean = true;
    @Input() buttonConfig? : ButtonConfig[] = [];

    constructor(public router : Router) {}
}