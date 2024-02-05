import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { Router } from "@angular/router";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { RequestPasswordResetComponent } from "./request-password-reset.component";
import { ResetPasswordRequestService } from "./service/request-password-reset.service";
import { HttpErrorResponse } from "@angular/common/http";

/**
 * @author Peter Szrnka
 */
describe('RequestPasswordResetComponent', () => {
    let component : RequestPasswordResetComponent;
    let fixture : ComponentFixture<RequestPasswordResetComponent>;

    // Injected services
    let router : any;
    let service : any;
    let dialog : any = {};
    let splashScreenStateService : any;

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ FormsModule, AngularMaterialModule, NoopAnimationsModule ],
            declarations : [RequestPasswordResetComponent],
            providers: [
                { provide : Router, useValue: router },
                { provide : SplashScreenStateService, useValue : splashScreenStateService },
                { provide : MatDialog, useValue : dialog },
                { provide : ResetPasswordRequestService, useValue : service }
            ],
            schemas: [ CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA ]
        });

        fixture = TestBed.createComponent(RequestPasswordResetComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        router = {
            navigate : jest.fn().mockReturnValue(of(true))
        };

        dialog = {
            open : jest.fn()
        };

        service = {
            requestPasswordReset : jest.fn().mockImplementation(() => {
                return of({});
            })
        };

        splashScreenStateService = {
            start : jest.fn(),
            stop : jest.fn()
        };
    });

    it('Should fail', () => {
        // arrange
        service = {
            requestPasswordReset : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})))
        };
        configTestBed();

        // act
        component.requestReset();

        // assert
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(service.requestPasswordReset).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(dialog.open).toHaveBeenCalled();
    });

    it('Should request reset password', () => {
        // arrange
        configTestBed();

        // act
        component.requestReset();

        // assert
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(service.requestPasswordReset).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(dialog.open).toHaveBeenCalled();
    });
});