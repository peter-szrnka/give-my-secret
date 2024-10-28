import { NgFor, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AngularMaterialModule } from '../../../angular-material-module';
import { NavButtonVisibilityPipe } from '../pipes/nav-button-visibility.pipe';
import { ButtonConfig } from './button-config';
import { TranslatorModule } from '../pipes/translator/translator.module';

/**
 * @author Peter Szrnka
 */
@Component({ 
    standalone: true,
    imports: [
        AngularMaterialModule,
        NavButtonVisibilityPipe,
        NgIf, NgFor,
        RouterLink,
        TranslatorModule
    ],
    selector: 'nav-back',
    templateUrl: './nav-back.component.html',
    styleUrls : ['./nav-back.component.scss'],
})
export class NavBackComponent {

    @Input() backToHome? : boolean = true;
    @Input() buttonConfig? : ButtonConfig[] = [];
}