import { HttpErrorResponse } from "@angular/common/http";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormBuilder, FormsModule } from "@angular/forms";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { Observable, ReplaySubject, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { FORM_GROUP_MOCK } from "../../common/form-helper.spec";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { UserService } from "../user/service/user-service";
import { SettingsSummaryComponent } from "./settings-summary.component";
import { TranslatorService } from "../../common/service/translator-service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
describe('SettingsSummaryComponent', () => {
    let component : SettingsSummaryComponent;
    let fixture : ComponentFixture<SettingsSummaryComponent>;

    // Injected services
    let userService : any;
    let dialog : any = {};
    let formBuilder : any;
    let splashScreenService : any;
    let sharedData : any;
    let mockSubject : ReplaySubject<string>;
    let translatorService : any;

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ FormsModule, AngularMaterialModule, NoopAnimationsModule, TranslatorModule ],
            declarations : [SettingsSummaryComponent],
            providers: [
                { provide : UserService, useValue : userService },
                { provide : DialogService, useValue : dialog },
                { provide : FormBuilder, useValue : formBuilder },
                { provide : SplashScreenStateService, useValue : splashScreenService },
                { provide : SharedDataService, useValue : sharedData },
                { provide : TranslatorService, useValue : translatorService }
            ]
        });

        fixture = TestBed.createComponent(SettingsSummaryComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        dialog = {
            openInfoDialog : jest.fn(),
            openWarningDialog : jest.fn()
        };
        userService = {
            changeCredentials : jest.fn().mockImplementation(() : Observable<void> => {
                return of(void 0);
            }),
            isMfaActive: jest.fn().mockImplementation(() : Observable<boolean> => {
                return of(true);
            }),
            toggleMfa: jest.fn().mockReturnValue(of(''))
        };
        splashScreenService = {
            start : jest.fn(),
            stop : jest.fn()
        };

        translatorService = {
            translate : jest.fn().mockReturnValue('translated')
        };

        mockSubject = new ReplaySubject<string>();
        sharedData = {
            authMode : 'db',
            authModeSubject$ : mockSubject
        };

        mockSubject.next('db');

        formBuilder = FORM_GROUP_MOCK;
    });

    it('Should fail on save', () => {
        userService.changeCredentials  =jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})));
        configTestBed();

        component.credentialData.oldCredential = "oldPassword";
        component.credentialData.newCredential1 = "newPassword";
        component.credentialData.newCredential2 = "newPassword";

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.openWarningDialog).toHaveBeenCalled();
        expect(splashScreenService.start).toHaveBeenCalled();
        expect(splashScreenService.stop).toHaveBeenCalled();
    });

    it('Should create component and save', () => {
        configTestBed();

        component.credentialData.oldCredential = "oldPassword";
        component.credentialData.newCredential1 = "newPassword";
        component.credentialData.newCredential2 = "newPassword";

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(splashScreenService.start).toHaveBeenCalled();
        expect(splashScreenService.stop).toHaveBeenCalled();
    });

    it('Should toggle MFA fail', () => {
        // arrange
        userService.toggleMfa =jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})));
        configTestBed();
        component.mfaEnabled = true;

        // act
        component.toggleMfa();

        // assert
        expect(dialog.openWarningDialog).toHaveBeenCalled();
        expect(userService.toggleMfa).toHaveBeenCalledWith(true);
        expect(splashScreenService.start).toHaveBeenCalled();
        expect(splashScreenService.stop).toHaveBeenCalled();
    });

    it('Should toggle MFA', () => {
        // arrange
        configTestBed();
        component.mfaEnabled = true;

        // act
        component.toggleMfa();

        // assert
        expect(userService.toggleMfa).toHaveBeenCalledWith(true);
    });
});