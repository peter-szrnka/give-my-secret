import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonConfig } from './button-config';

/**
 * @author Peter Szrnka
 */
@Component({ 
    selector: 'nav-back',
    templateUrl: './nav-back.component.html',
    styleUrls : ['./nav-back.component.scss']
})
export class NavBackComponent {

    @Input() backToHome? : boolean = true;
    @Input() buttonConfig? : ButtonConfig[] = [];

    constructor(public router : Router) {}
}