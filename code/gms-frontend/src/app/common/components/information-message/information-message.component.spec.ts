import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatDialogModule } from "@angular/material/dialog";
import { TranslatorModule } from "../pipes/translator/translator.module";
import { InformationMessageComponent } from "./information-message.component";

/**
 * @author Peter Szrnka
 */
describe('InformationMessageComponent', () => {
    let component : InformationMessageComponent;

    // Fixtures
    let fixture : ComponentFixture<InformationMessageComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports : [MatDialogModule, TranslatorModule, InformationMessageComponent ],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
            ]
        });
    });

    it('onInit when component loaded then set parameters properly', () => {
        fixture = TestBed.createComponent(InformationMessageComponent);
        component = fixture.componentInstance;
        component.severity = 'information';
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.severity).toBe('information');
        expect(component.icon).toBe('information');
        expect(component.iconColor).toBe('blue');
    });
});