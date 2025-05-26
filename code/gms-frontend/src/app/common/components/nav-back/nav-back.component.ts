import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AngularMaterialModule } from '../../../angular-material-module';
import { NavButtonVisibilityPipe } from '../pipes/nav-button-visibility.pipe';
import { TranslatorModule } from '../pipes/translator/translator.module';
import { ButtonConfig } from './button-config';

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [
        AngularMaterialModule,
        NavButtonVisibilityPipe,
        RouterLink,
        TranslatorModule
    ],
    selector: 'nav-back',
    templateUrl: './nav-back.component.html',
    styleUrls: ['./nav-back.component.scss']
})
export class NavBackComponent {

    @Input() backToHome? : boolean = true;
    @Input() buttonConfig? : ButtonConfig[] = [];
    @Input() reload? : boolean = true;
    @Input() customSuffixText?: string;

    reloadPage() {
        window.location.reload();
    }
}