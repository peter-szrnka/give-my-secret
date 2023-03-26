import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { StatusToggleComponent } from "./status-toggle.component";
import { MatSnackBar } from "@angular/material/snack-bar";

/**
 * @author Peter Szrnka
 */
describe('StatusToggleComponent', () => {
    let component : StatusToggleComponent;
    let snackbar : any;

    // Fixtures
    let fixture : ComponentFixture<StatusToggleComponent>;

    beforeEach(() => {
        snackbar = {
            open : jest.fn()
        };
        TestBed.configureTestingModule({
            imports : [RouterTestingModule ],
            declarations : [StatusToggleComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers : [
                {
                    provide: MatSnackBar, useValue : snackbar
                }
            ]
        });
    });

    it('Should toggle entity', () => {
        fixture = TestBed.createComponent(StatusToggleComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        component.doNotToggle = false;
        component.entityId = 1;
        component.status = 'ACTIVE';
        component.callbackFunction.subscribe((response) => {
            expect(response).toBeDefined();
        });

        // act
        component.toggle();

        expect(component).toBeTruthy();
    });

    it('Should not toggle entity', () => {
        fixture = TestBed.createComponent(StatusToggleComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        component.doNotToggle = true;
        component.entityId = 1;
        component.status = 'DISABLED';

        // act
        component.toggle();

        expect(component).toBeTruthy();
    });
});