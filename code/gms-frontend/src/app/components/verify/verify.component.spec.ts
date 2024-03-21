import { HttpErrorResponse } from "@angular/common/http";
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
import { User } from "../user/model/user.model";
import { VerifyComponent } from "./verify.component";
import { WINDOW_TOKEN } from "../../window.provider";

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

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ RouterTestingModule, FormsModule, AngularMaterialModule, NoopAnimationsModule ],
            declarations : [VerifyComponent],
            providers: [
                { provide : WINDOW_TOKEN, useValue : mockWindow },
                { provide : Router, useValue: router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : SplashScreenStateService, useValue : splashScreenStateService },
                { provide : AuthService, useValue : authService },
                { provide : MatDialog, useValue : dialog },
                { provide : Location, useValue: mockLocation },
                { provide : ActivatedRoute, useValue : activatedRoute }
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
        expect(authService.verifyLogin).toBeCalledWith({ username: "user-1", verificationCode : "123456" } as VerifyLogin);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(sharedDataService.refreshCurrentUserInfo).toBeCalledTimes(0);
    });

    it('Should MFA verification succeed', async () => {
        // arrange
        const mockUser: User = { username: 'test', roles: [] };
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
        expect(authService.verifyLogin).toBeCalledWith({ username: "user-1", verificationCode : "123456" } as VerifyLogin);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(router.navigate).toHaveBeenCalled();
    });

    it('Should MFA verification fail', async () => {
        // arrange
        const mockResponse: LoginResponse = {
            currentUser: { username: 'test', roles: [] },
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
        expect(authService.verifyLogin).toBeCalledWith({ username: "user-1", verificationCode : "123456" } as VerifyLogin);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(sharedDataService.refreshCurrentUserInfo).toHaveBeenCalledTimes(0);
        expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
    });
});