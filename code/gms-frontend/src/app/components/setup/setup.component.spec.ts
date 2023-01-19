import { HttpErrorResponse } from "@angular/common/http";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatDialog } from "@angular/material/dialog";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { Observable, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { IEntitySaveResponseDto } from "../../common/model/entity-save-response.model";
import { SetupService } from "../../common/service/setup-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { SetupComponent } from "./setup.component";
import { WINDOW_TOKEN } from "../../app.module";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { AppRoutingModule } from "../../app-routing.module";
import { FormsModule } from "@angular/forms";
import { GmsComponentsModule } from "../../common/components/gms-components-module";

describe('SetupComponent', () => {
    let component : SetupComponent;
    let fixture : ComponentFixture<SetupComponent>;

    // Injected services
    let router : any;
    let splashScreenStateService : any;
    let dialog : any;
    let setupService : any;
    let mockWindow : any;

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ RouterTestingModule, FormsModule, BrowserModule, AngularMaterialModule, NoopAnimationsModule, AppRoutingModule, GmsComponentsModule ],
            declarations : [SetupComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : WINDOW_TOKEN, useValue : mockWindow },
                { provide : Router, useValue: router },
                { provide : SplashScreenStateService, useValue : splashScreenStateService },
                { provide : MatDialog, useValue : dialog },
                { provide : SetupService, useValue : setupService }
            ]
        });

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
            navigate : jest.fn()
        };

        splashScreenStateService = {
            start : jest.fn(),
            stop : jest.fn()
        };

        setupService = {
            saveAdminUser : jest.fn().mockImplementation(() : Observable<IEntitySaveResponseDto> => {
                return of({ entityId : 1, success : true } as IEntitySaveResponseDto);
            })
        };
    });

    it('should throw error 404', () => {
        // arrange
        setupService = {
            saveAdminUser : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 404, statusText: "Not exists"})))
        }
        configTestBed();

        component.userData = {
            username : "admin",
            credential : "testPassword",
            roles : []
        };

        // act
        component.saveAdminUser();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.saveAdminUser).toHaveBeenCalledWith(component.userData);
        expect(router.navigate).toBeCalledWith(['']);
    });

    it('should throw error 500', () => {
        // arrange
        setupService = {
            saveAdminUser : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "OOPS!"})))
        }
        configTestBed();

        component.userData = {
            username : "admin",
            credential : "testPassword",
            roles : []
        };

        // act
        component.saveAdminUser();

        // assert
        expect(component).toBeTruthy();
        expect(setupService.saveAdminUser).toHaveBeenCalledWith(component.userData);
        expect(router.navigate).toBeCalledTimes(0);
    });

    it('should save setup settings', () => {
        // arrange
        configTestBed();

        component.userData = {
            username : "admin",
            credential : "testPassword",
            roles : []
        };

        // act
        component.saveAdminUser();

        // assert
        expect(component).toBeTruthy();
        expect(component.errorMessage).toEqual('');
        expect(setupService.saveAdminUser).toHaveBeenCalledWith(component.userData);
    });

    it('should retry setup', () => {
        // arrange
        configTestBed();

        component.errorMessage = 'Test error';

        // act
        component.retrySetup();

        // arrange
        expect(component).toBeTruthy();
        expect(component.errorMessage).toBeUndefined();
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
});