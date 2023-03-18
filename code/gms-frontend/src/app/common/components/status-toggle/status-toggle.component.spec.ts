import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { SplashScreenStateService } from "../../service/splash-screen-service";
import { SplashComponent } from "../splash/splash.component";
import { StatusToggleComponent } from "./status-toggle.component";
import { EventEmitter } from "stream";

/**
 * @author Peter Szrnka
 */
describe('SplashComponent', () => {
    let component : StatusToggleComponent;

    // Fixtures
    let fixture : ComponentFixture<StatusToggleComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports : [RouterTestingModule ],
            declarations : [SplashComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
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