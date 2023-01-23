import { HttpErrorResponse } from "@angular/common/http";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormBuilder, FormsModule } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { RouterTestingModule } from "@angular/router/testing";
import { Observable, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { FORM_GROUP_MOCK } from "../../common/form-helper.spec";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { UserService } from "../../common/service/user-service";
import { SettingsSummaryComponent } from "./settings-summary.component";

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

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ RouterTestingModule, FormsModule, AngularMaterialModule, NoopAnimationsModule ],
            declarations : [SettingsSummaryComponent],
            providers: [
                { provide : UserService, useValue : userService },
                { provide : MatDialog, useValue : dialog },
                { provide : FormBuilder, useValue : formBuilder },
                { provide : SplashScreenStateService, useValue : splashScreenService },
                { provide : SharedDataService, useValue : sharedData }
            ]
        });

        fixture = TestBed.createComponent(SettingsSummaryComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        dialog = {
            open : jest.fn()
        };
        userService = {
            changeCredentials : jest.fn().mockImplementation(() : Observable<void> => {
                return of(void 0);
            })
        };
        splashScreenService = {
            start : jest.fn(),
            stop : jest.fn()
        };

        sharedData = {
            authMode : 'db'
        };

        formBuilder = FORM_GROUP_MOCK;
    });

    it('Should fail on save', () => {
        userService = {
            changeCredentials : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})))
        };
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

});