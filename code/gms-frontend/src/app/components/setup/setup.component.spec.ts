import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Router } from "@angular/router";
import { Observable, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { IEntitySaveResponseDto } from "../../common/model/entity-save-response.model";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { WINDOW_TOKEN } from "../../window.provider";
import { SetupService } from "./service/setup-service";
import { EMPTY_ADMIN_DATA, SetupComponent } from "./setup.component";

/**
 * @author Peter Szrnka
 */
describe('SetupComponent', () => {
    let component : SetupComponent;
    let fixture : ComponentFixture<SetupComponent>;

    // Injected services
    let router : any;
    let route: any;
    let splashScreenStateService : any;
    let dialog : any;
    let setupService : any;
    let mockWindow : any;

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ SetupComponent, AngularMaterialModule, FormsModule, BrowserAnimationsModule, TranslatorModule ],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : WINDOW_TOKEN, useValue : mockWindow },
                { provide : Router, useValue: router },
                { provide : ActivatedRoute, useValue: route },
                { provide : SplashScreenStateService, useValue : splashScreenStateService },
                { provide : MatDialog, useValue : dialog },
                { provide : SetupService, useValue : setupService }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(SetupComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        mockWindow = {
            location : {
                reload: jest.fn()
            }
        };

        router = {
            navigate : jest.fn().mockReturnValue(of(true)),
            navigateByUrl : jest.fn().mockReturnValue(of(true))
        };

        route = {
            queryParams : of({ systemStatus : 'NEED_ADMIN_USER' })
        };

        splashScreenStateService = {
            start : jest.fn(),
            stop : jest.fn()
        };

        setupService = {
            saveAdminUser : jest.fn().mockImplementation(() : Observable<IEntitySaveResponseDto> => {
                return of({ entityId : 1, success : true } as IEntitySaveResponseDto);
            }),
            stepBack : jest.fn().mockReturnValue(of('NEED_SETUP')),
            getAdminUserData : jest.fn().mockReturnValue(of({ username : 'admin', credential : 'testPassword', role : 'ROLE_ADMIN' })),
            saveInitialStep : jest.fn().mockReturnValue(of('NEED_ADMIN_USER')),
            saveSystemProperties: jest.fn().mockReturnValue(of({ success : true })),
            saveOrganizationData : jest.fn().mockReturnValue(of({ success : true })),
            completeSetup : jest.fn().mockReturnValue(of({ success : true }))
        };
    });

    it('should saveAdminUser throw error 404', async () => {
        // arrange
        setupService.saveAdminUser = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 404, statusText: "Not exists"})));

        configTestBed();

        component.userData = {
            username : "admin",
            credential : "testPassword",
            role: 'ROLE_ADMIN'
        };
        const navigateSpy = jest.spyOn(router,'navigate');

        // act
        component.saveAdminUser();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.saveAdminUser).toHaveBeenCalledWith(component.userData);
        expect(navigateSpy).toHaveBeenCalledWith(['']);
    });

    it('should saveAdminUser throw error 500', () => {
        // arrange
        setupService.saveAdminUser = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "OOPS!"})));
        configTestBed();

        component.userData = {
            username : "admin",
            credential : "testPassword",
            role: 'ROLE_ADMIN'
        };

        // act
        component.saveAdminUser();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.saveAdminUser).toHaveBeenCalledWith(component.userData);
        expect(router.navigate).toHaveBeenCalledTimes(0);
    });

    it('should save admin user', () => {
        // arrange
        route.queryParams = of({ systemStatus : 'NEED_ADMIN_USER' });
        configTestBed();

        component.userData = {
            username : "admin",
            credential : "testPassword",
            role: 'ROLE_ADMIN'
        };

        // act
        component.saveAdminUser();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.saveAdminUser).toHaveBeenCalledWith(component.userData);
    });

    it('should step back throw error 500', () => {
        // arrange
        setupService.stepBack = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "OOPS!"})));
        configTestBed();

        // act
        component.stepBack();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.stepBack).toHaveBeenCalled();
        expect(router.navigateByUrl).toHaveBeenCalledTimes(0);
    });

    it('should getCurrentAdminUserData throw error 500', () => {
        // arrange
        setupService.getAdminUserData = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "OOPS!"})));
        configTestBed();

        // act
        component.getCurrentAdminUserData();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.getAdminUserData).toHaveBeenCalled();
        expect(router.navigateByUrl).toHaveBeenCalledTimes(0);
    });

    it('should not found existing admin user', () => {
        // arrange
        setupService.getAdminUserData = jest.fn().mockReturnValue(of(null));
        configTestBed();

        // act
        component.getCurrentAdminUserData();

        // assert
        expect(component).toBeTruthy();
        expect(component.userData).toEqual(EMPTY_ADMIN_DATA);
        expect(setupService.getAdminUserData).toHaveBeenCalled();
        expect(router.navigateByUrl).toHaveBeenCalledTimes(0);
    });

    it('should step back', () => {
        // arrange
        route.queryParams = of();
        setupService.stepBack = jest.fn().mockReturnValue(of('NEED_SETUP'));
        configTestBed();

        // act
        component.stepBack();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.stepBack).toHaveBeenCalled();
        expect(router.navigateByUrl).toHaveBeenCalledWith('/setup?systemStatus=NEED_SETUP');
    });

    it('should load vm options', () => {
        // arrange
        route.queryParams = of({ systemStatus : 'NEED_SETUP' })
        configTestBed();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.saveInitialStep).toHaveBeenCalledTimes(0);
    });

    it('should save initial step', () => {
        // arrange
        setupService.saveInitialStep = jest.fn().mockReturnValue(of('NEED_ADMIN_USER'));
        configTestBed();

        // act
        component.saveInitialStep();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.saveInitialStep).toHaveBeenCalled();
        expect(router.navigateByUrl).toHaveBeenCalledWith('/setup?systemStatus=NEED_ADMIN_USER');
    });

    it('should save initial step throw error 500', () => {
        // arrange
        setupService.saveInitialStep = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "OOPS!"})));
        configTestBed();

        // act
        component.saveInitialStep();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.saveInitialStep).toHaveBeenCalled();
        expect(router.navigateByUrl).toHaveBeenCalledTimes(0);
    });

    it('should save system properties', () => {
        // arrange
        setupService.saveSystemProperties = jest.fn().mockReturnValue(of({ success : true }));
        configTestBed();

        // act
        component.saveSystemProperties();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.saveSystemProperties).toHaveBeenCalled();
    });

    it('should save system properties throw error 500', () => {
        // arrange
        setupService.saveSystemProperties = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "OOPS!"})));
        configTestBed();

        // act
        component.saveSystemProperties();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.saveSystemProperties).toHaveBeenCalled();
        expect(router.navigateByUrl).toHaveBeenCalledTimes(0);
    });

    it('should save organization data'  , () => {
        // arrange
        setupService.saveOrganizationData = jest.fn().mockReturnValue(of({ success : true }));
        configTestBed();

        // act
        component.saveOrganizationData();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.saveOrganizationData).toHaveBeenCalled();
    });

    it('should save organization data throw error 500', () => {
        // arrange
        setupService.saveOrganizationData = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "OOPS!"})));
        configTestBed();

        // act
        component.saveOrganizationData();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.saveOrganizationData).toHaveBeenCalled();
        expect(router.navigateByUrl).toHaveBeenCalledTimes(0);
    });

    it('should navigate to home', () => {
        // arrange
        configTestBed();

        component.errorMessage = 'Test error';

        // act
        component.navigateToHome();
        // arrange
        expect(component).toBeTruthy();
        expect(mockWindow.location.reload).toHaveBeenCalled();
    });

    it('should navigate to home throw error 404', () => {
        // arrange
        setupService.completeSetup = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 404, statusText: "Not exists"})));
        configTestBed();

        // act
        component.navigateToHome();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.completeSetup).toHaveBeenCalled();
        expect(router.navigate).toHaveBeenCalledWith(['']);
    });
});