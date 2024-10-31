import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Router } from "@angular/router";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { AuthenticationPhase, Login, LoginResponse } from "../../common/model/login.model";
import { AuthService } from "../../common/service/auth-service";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { EMPTY_USER } from "../user/model/user.model";
import { LoginComponent } from "./login.component";

/**
 * @author Peter Szrnka
 */
describe('LoginComponent', () => {
    let component : LoginComponent;
    let fixture : ComponentFixture<LoginComponent>;
    // Injected services
    let router : any;
    let authService : any;
    let dialogService : any = {};
    let sharedDataService : any;
    let splashScreenStateService : any;
    let mockLocation: any;
    let activatedRoute : any = {};

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ FormsModule, AngularMaterialModule, NoopAnimationsModule, TranslatorModule ],
            declarations : [LoginComponent],
            providers: [
                { provide : Router, useValue: router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : SplashScreenStateService, useValue : splashScreenStateService },
                { provide : AuthService, useValue : authService },
                { provide : DialogService, useValue : dialogService },
                { provide : Location, useValue: mockLocation },
                { provide : ActivatedRoute, useValue : activatedRoute }
            ],
            schemas: [ CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA ]
        });

        fixture = TestBed.createComponent(LoginComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        router = {
            navigate : jest.fn().mockReturnValue(of(true))
        };

        sharedDataService = {
            refreshCurrentUserInfo : jest.fn(),
            systemReady: true
        };

        dialogService = {
            openNewDialog : jest.fn()
        };

        splashScreenStateService = {
            start : jest.fn(),
            stop : jest.fn()
        };

        authService = {
            login : jest.fn().mockImplementation(() => {
                return of({
                    currentUser:  {
                        role: "ROLE_USER"
                    },
                    phase: AuthenticationPhase.COMPLETED
                });
            })
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

    it.each([
        [undefined, "ROLE_USER"],
        [{
            previousUrl: '/'
        }, 'ROLE_USER'],
        [{
            previousUrl: '/secret/list'
        }, 'ROLE_ADMIN'],
        [{
            previousUrl: '/users/list'
        }, 'ROLE_ADMIN'],
        [{
            previousUrl: '/secret/list'
        }, 'ROLE_USER'],
        [{
            previousUrl: '/users/list'
        }, 'ROLE_USER']
    ])('Should create component and login with redirect', (inputQueryParam: any, inputRole: string) => {
        // arrange
        activatedRoute = {
            snapshot : {
                queryParams: inputQueryParam
            }
        };
        authService = {
            login : jest.fn().mockImplementation(() => {
                return of({
                    currentUser:  {
                        role: inputRole
                    },
                    phase: AuthenticationPhase.COMPLETED
                });
            })
        };

        configTestBed();
        component.formModel = { username: "user-1", credential : "myPassword1" };

        // act
        component.togglePasswordDisplay();
        component.login();

        // assert
        expect(component).toBeTruthy();
        expect(authService.login).toHaveBeenCalledWith({ username: "user-1", credential : "myPassword1" } as Login);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(component.showPassword).toBeTruthy();
    });

    it('Should create component and login', () => {
        // arrange
        activatedRoute = {
            snapshot : {
                queryParams: {
                }
            }
        };
        configTestBed();
        component.formModel = { username: "user-1", credential : "myPassword1" };

        // act
        component.togglePasswordDisplay();
        component.login();

        // assert
        expect(component).toBeTruthy();
        expect(authService.login).toHaveBeenCalledWith({ username: "user-1", credential : "myPassword1" } as Login);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(component.showPassword).toBeTruthy();
    });

    it('Should not redirect to main page when system is not ready', () => {
        // arrange
        activatedRoute = {
            snapshot : {
                queryParams: {
                }
            }
        };
        configTestBed();

        // act
        sharedDataService.systemReady = false;

        // assert
        expect(component).toBeTruthy();
        expect(router.navigate).toHaveBeenCalledTimes(0);
    });

    it('Should require MFA with redirect', () => {
        // arrange
        activatedRoute = {
            snapshot : {
                queryParams: {
                    previousUrl: '/users/list'
                }
            }
        };
        const mockResponse: LoginResponse = {
            currentUser: { username: 'test', role: 'ROLE_USER' },
            phase: AuthenticationPhase.MFA_REQUIRED
        };
        authService = {
            login : jest.fn().mockImplementation(() => {
                return of(mockResponse);
            })
        };
        configTestBed();
        component.formModel = { username: "user-1", credential : "myPassword1" };

        // act
        component.login();

        // assert
        expect(component).toBeTruthy();
        expect(authService.login).toHaveBeenCalledWith({ username: "user-1", credential : "myPassword1" } as Login);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(router.navigate).toHaveBeenCalledWith(['/verify'], { state: { username: 'test' }, queryParams: { previousUrl: '/users/list' } });
        expect(sharedDataService.refreshCurrentUserInfo).toHaveBeenCalledTimes(0);
    });

    it('Should require MFA with redirect', () => {
        // arrange
        activatedRoute = {
            snapshot : {
                queryParams: {
                    previousUrl: '/users/list'
                }
            }
        };
        const mockResponse: LoginResponse = {
            currentUser: EMPTY_USER,
            phase: AuthenticationPhase.BLOCKED
        };
        authService = {
            login : jest.fn().mockImplementation(() => {
                return of(mockResponse);
            })
        };
        configTestBed();
        component.formModel = { username: "user-1", credential : "myPassword1" };

        // act
        component.login();

        // assert
        expect(component).toBeTruthy();
        expect(authService.login).toHaveBeenCalledWith({ username: "user-1", credential : "myPassword1" } as Login);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it('Should require MFA', () => {
        // arrange
        const mockResponse: LoginResponse = {
            currentUser: { username: 'test', role: 'ROLE_USER' },
            phase: AuthenticationPhase.MFA_REQUIRED
        };
        authService = {
            login : jest.fn().mockImplementation(() => {
                return of(mockResponse);
            })
        };
        configTestBed();
        component.formModel = { username: "user-1", credential : "myPassword1" };

        // act
        component.login();

        // assert
        expect(component).toBeTruthy();
        expect(authService.login).toHaveBeenCalledWith({ username: "user-1", credential : "myPassword1" } as Login);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(router.navigate).toHaveBeenCalledWith(['/verify'], { state: { username: 'test' }, queryParams: { previousUrl: '' } });
        expect(sharedDataService.refreshCurrentUserInfo).toHaveBeenCalledTimes(0);
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
        expect(authService.login).toHaveBeenCalledWith({ username: "user-1", credential : "myPassword1" } as Login);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(sharedDataService.refreshCurrentUserInfo).toHaveBeenCalledTimes(0);
    });
});