import { HttpErrorResponse } from "@angular/common/http";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { of, ReplaySubject, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { Login } from "../../common/model/login.model";
import { AuthService } from "../../common/service/auth-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { LoginComponent } from "./login.component";

/**
 * @author Peter Szrnka
 */
describe('LoginComponent', () => {
    let component : LoginComponent;
    let fixture : ComponentFixture<LoginComponent>;
    let mockSubject : ReplaySubject<any>;
    // Injected services
    let router : any;
    let authService : any;
    let dialog : any = {};
    let sharedDataService : any;
    let splashScreenStateService : any;

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ RouterTestingModule, FormsModule, AngularMaterialModule, NoopAnimationsModule ],
            declarations : [LoginComponent],
            providers: [
                { provide : Router, useValue: router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : SplashScreenStateService, useValue : splashScreenStateService },
                { provide : AuthService, useValue : authService },
                { provide : MatDialog, useValue : dialog }
            ]
        });

        fixture = TestBed.createComponent(LoginComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        router = {
            navigate : jest.fn()
        };

        mockSubject = new ReplaySubject<any>();
        sharedDataService = {
            setCurrentUser : jest.fn(),
            userSubject$ : mockSubject
        };

        dialog = {
            open : jest.fn()
        };

        splashScreenStateService = {
            start : jest.fn(),
            stop : jest.fn()
        };

        authService = {
            login : jest.fn().mockImplementation(() => {
                return of("mock_jwt");
            })
        };
    });

    it('Should create component and login', () => {
        configTestBed();
        component.formModel = { username: "user-1", credential : "myPassword1" };

        // act
        component.login();

        // assert
        expect(component).toBeTruthy();
        expect(authService.login).toBeCalledWith({ username: "user-1", credential : "myPassword1" } as Login);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(sharedDataService.setCurrentUser).toBeCalledWith('mock_jwt');
    });

    it('Should fail after login', () => {
        authService = {
            login : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})))
        };

        configTestBed();
        component.formModel = { username: "user-1", credential : "myPassword1" };

        // act
        component.login();

        // assert
        expect(component).toBeTruthy();
        expect(authService.login).toBeCalledWith({ username: "user-1", credential : "myPassword1" } as Login);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(sharedDataService.setCurrentUser).toBeCalledTimes(0);
    });
});