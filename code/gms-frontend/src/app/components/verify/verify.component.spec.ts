import { HttpErrorResponse } from "@angular/common/http";
import { EventEmitter } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { ReplaySubject, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { AuthenticationPhase, LoginResponse, VerifyLogin } from "../../common/model/login.model";
import { AuthService } from "../../common/service/auth-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { WINDOW_TOKEN } from "../../window.provider";
import { User } from "../user/model/user.model";
import { VerifyComponent } from "./verify.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { LoggerService } from "../../common/service/logger-service";

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
    let mockWindow : any;
    let mockLocation: any;
    let activatedRoute : any = {};
    let loggerService : any = {};

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ VerifyComponent, RouterTestingModule, FormsModule, AngularMaterialModule, NoopAnimationsModule, TranslatorModule ],
            providers: [
                { provide : WINDOW_TOKEN, useValue : mockWindow },
                { provide : Router, useValue: router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : SplashScreenStateService, useValue : splashScreenStateService },
                { provide : AuthService, useValue : authService },
                { provide : MatDialog, useValue : dialog },
                { provide : Location, useValue: mockLocation },
                { provide : ActivatedRoute, useValue : activatedRoute },
                { provide : LoggerService, useValue: loggerService }
            ]
        });
        fixture = TestBed.createComponent(VerifyComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        mockWindow = {
            history : {
                state: {
                    username: 'user-1'
                }
            }
        };

        router = {
            navigate : jest.fn().mockResolvedValue(true),
            navigateByUrl : jest.fn().mockResolvedValue(true),
            getCurrentNavigation: jest.fn().mockReturnValue({
                extras: { state: { username: 'user-1' } }
            })
        };

        mockSubject = new ReplaySubject<any>();
        sharedDataService = {
            refreshCurrentUserInfo : jest.fn(),
            userSubject$ : mockSubject,
            navigationChangeEvent: jest.fn().mockReturnValue(new EventEmitter<string>())
        };

        dialog = {
            open : jest.fn()
        };

        splashScreenStateService = {
            start : jest.fn(),
            stop : jest.fn()
        };

        const mockCurrentUser: User = {
            role: 'ROLE_USER'
        };

        const mockResponse: LoginResponse = {
            currentUser: mockCurrentUser,
            phase: AuthenticationPhase.COMPLETED
        };
        authService = {
            verifyLogin : jest.fn().mockReturnValue(of(mockResponse))
        };

        mockLocation = {
            path: jest.fn().mockReturnValue("/")
        };

        activatedRoute = {
            snapshot : {
                queryParams: {
                }
            }
        };

        loggerService = {
            error: jest.fn()
        };
    });

    afterEach(() => {
        jest.resetAllMocks();
    });

    it('Should forced call redirected', () => {
        mockWindow.history.state = {};
        configTestBed();

        // assert
        expect(component).toBeTruthy();
        expect(router.navigateByUrl).toHaveBeenCalledWith('/');
    });

    it('Should fail by unknown error', async () => {
        authService = {
            verifyLogin : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})))
        };

        configTestBed();
        component.formModel.verificationCode = "123456";

        // act
        await component.verifyLogin();

        // assert
        expect(component).toBeTruthy();
        expect(authService.verifyLogin).toHaveBeenCalledWith({ username: "user-1", verificationCode : "123456" } as VerifyLogin);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(sharedDataService.refreshCurrentUserInfo).toHaveBeenCalledTimes(0);
        expect(loggerService.error).toHaveBeenCalled();
    });

    it('Should MFA verification succeed', async () => {
        // arrange
        const mockUser: User = { username: 'test', role: 'ROLE_USER' };
        const mockResponse: LoginResponse = {
            currentUser: mockUser,
            phase: AuthenticationPhase.COMPLETED
        };
        authService = {
            verifyLogin : jest.fn().mockReturnValue(of(mockResponse))
        };
        configTestBed();
        component.formModel.verificationCode = "123456";

        // act
        await component.verifyLogin();

        // assert
        expect(component).toBeTruthy();
        expect(component.formModel.username).toEqual('user-1');
        expect(authService.verifyLogin).toHaveBeenCalledWith({ username: "user-1", verificationCode : "123456" } as VerifyLogin);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it('Should MFA verification succeed and navigate to custom url', async () => {
        // arrange
        const mockUser: User = { username: 'test', role: 'ROLE_USER' };
        const mockResponse: LoginResponse = {
            currentUser: mockUser,
            phase: AuthenticationPhase.COMPLETED
        };
        authService = {
            verifyLogin : jest.fn().mockReturnValue(of(mockResponse))
        };
        activatedRoute.snapshot.queryParams['previousUrl'] = '/secret/list';
        configTestBed();
        component.formModel.verificationCode = "123456";

        // act
        await component.verifyLogin();

        // assert
        expect(component).toBeTruthy();
        expect(component.formModel.username).toEqual('user-1');
        expect(authService.verifyLogin).toHaveBeenCalledWith({ username: "user-1", verificationCode : "123456" } as VerifyLogin);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it('Should MFA verification fail', async () => {
        // arrange
        const mockResponse: LoginResponse = {
            currentUser: { username: 'test', role: 'ROLE_USER' },
            phase: AuthenticationPhase.FAILED
        };
        authService = {
            verifyLogin : jest.fn().mockReturnValue(of(mockResponse))
        };
        configTestBed();
        component.formModel.verificationCode = "123456";

        // act
        await component.verifyLogin();

        // assert
        expect(component).toBeTruthy();
        expect(authService.verifyLogin).toHaveBeenCalledWith({ username: "user-1", verificationCode : "123456" } as VerifyLogin);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(sharedDataService.refreshCurrentUserInfo).toHaveBeenCalledTimes(0);
        expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
    });
});