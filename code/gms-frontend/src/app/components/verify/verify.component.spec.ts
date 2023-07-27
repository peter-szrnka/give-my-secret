import { HttpErrorResponse } from "@angular/common/http";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { ReplaySubject, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { AuthenticationPhase, LoginResponse, VerifyLogin } from "../../common/model/login.model";
import { AuthService } from "../../common/service/auth-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { User } from "../user/model/user.model";
import { VerifyComponent } from "./verify.component";

/**
 * @author Peter Szrnka
 */
describe('VerifyComponent', () => {
    let component : VerifyComponent;
    let fixture : ComponentFixture<VerifyComponent>;
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
            declarations : [VerifyComponent],
            providers: [
                { provide : Router, useValue: router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : SplashScreenStateService, useValue : splashScreenStateService },
                { provide : AuthService, useValue : authService },
                { provide : MatDialog, useValue : dialog }
            ]
        });

        fixture = TestBed.createComponent(VerifyComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        router = {
            navigate : jest.fn().mockReturnValue(of(true)),
            navigateByUrl : jest.fn().mockReturnValue(of(true)),
            getCurrentNavigation: jest.fn().mockReturnValue({
                extras: { state: { username: 'user-1' } }
            })
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

        const mockCurrentUser: User = {
            roles: []
        };

        const mockResponse: LoginResponse = {
            currentUser: mockCurrentUser,
            phase: AuthenticationPhase.COMPLETED
        };
        authService = {
            verifyLogin : jest.fn().mockImplementation(() => {
                return of(mockResponse);
            })
        };
    });

    it('Should MFA verification succeed', () => {
        // arrange
        const mockUser = { username: 'test', roles: [] };
        const mockResponse: LoginResponse = {
            currentUser: mockUser,
            phase: AuthenticationPhase.COMPLETED
        };
        authService = {
            verifyLogin : jest.fn().mockImplementation(() => {
                return of(mockResponse);
            })
        };
        configTestBed();
        component.formModel.verificationCode = "123456";

        // act
        component.verifyLogin();

        // assert
        expect(component).toBeTruthy();
        expect(component.formModel.username).toEqual('user-1');
        expect(authService.verifyLogin).toBeCalledWith({ username: "user-1", verificationCode : "123456" } as VerifyLogin);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(router.navigate).toHaveBeenCalledWith(['']);
        expect(sharedDataService.setCurrentUser).toHaveBeenCalledWith(mockUser);
    });

    it('Should MFA verification fail', () => {
        // arrange
        const mockResponse: LoginResponse = {
            currentUser: { username: 'test', roles: [] },
            phase: AuthenticationPhase.FAILED
        };
        authService = {
            verifyLogin : jest.fn().mockImplementation(() => {
                return of(mockResponse);
            })
        };
        configTestBed();
        component.formModel.verificationCode = "123456";

        // act
        component.verifyLogin();

        // assert
        expect(component).toBeTruthy();
        expect(authService.verifyLogin).toBeCalledWith({ username: "user-1", verificationCode : "123456" } as VerifyLogin);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
        expect(sharedDataService.setCurrentUser).toHaveBeenCalledTimes(0);
    });

    it('Should fail by unknown error', () => {
        authService = {
            verifyLogin : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})))
        };

        configTestBed();
        component.formModel.verificationCode = "123456";

        // act
        component.verifyLogin();

        // assert
        expect(component).toBeTruthy();
        expect(authService.verifyLogin).toBeCalledWith({ username: "user-1", verificationCode : "123456" } as VerifyLogin);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(sharedDataService.setCurrentUser).toBeCalledTimes(0);
    });
});