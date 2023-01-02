import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { Subject } from "rxjs";
import { SplashScreenStateService } from "../../service/splash-screen-service";
import { SplashComponent } from "./splash.component";

describe('SplashComponent', () => {
    let component : SplashComponent;
    let splashScreenStateService : any;
    const returnSubject : Subject<boolean> = new Subject<boolean>();

    // Fixtures
    let fixture : ComponentFixture<SplashComponent>;

    beforeEach(() => {
        splashScreenStateService = {
            splashScreenSubject$ : returnSubject
        };

        TestBed.configureTestingModule({
            imports : [RouterTestingModule ],
            declarations : [SplashComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : SplashScreenStateService, useValue : splashScreenStateService }
            ]
        });

        fixture = TestBed.createComponent(SplashComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('Should load splash screen', () => {
        expect(component).toBeTruthy();
        
        returnSubject.next(true);

        // assert
        expect(component.splashTransition).toEqual("opacity 0s");
        expect(component.opacityChange).toEqual(1);
    });

    it('Should hide splash screen', () => {
        expect(component).toBeTruthy();
        
        returnSubject.next(false);

        // assert
        expect(component.splashTransition).toEqual("opacity 0.25s");
        expect(component.opacityChange).toEqual(0);
    });
});