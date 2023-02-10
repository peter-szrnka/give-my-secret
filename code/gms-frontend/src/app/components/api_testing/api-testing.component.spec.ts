import { HttpErrorResponse } from "@angular/common/http";
import { NO_ERRORS_SCHEMA } from "@angular/compiler";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { Observable, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { DialogData } from "../../common/components/info-dialog/dialog-data.model";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { ApiTestingService } from "./service/api-testing-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { ApiTestingComponent } from "./api-testing.component";
import { SecureStorageService } from "../../common/service/secure-storage.service";

/**
 * @author Peter Szrnka
 */
describe('ApiTestingComponent', () => {

    let component : ApiTestingComponent;
    let service : any;
    let splashScreenService : any;
    let dialog : any;
    let secureStorageService : any;

    // Fixtures
    let fixture : ComponentFixture<ApiTestingComponent>;

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ FormsModule, AngularMaterialModule, NoopAnimationsModule ],
            declarations : [ ApiTestingComponent ],
            providers : [
                { provide : ApiTestingService, useValue : service },
                { provide : MatDialog, useValue : dialog },
                { provide : SplashScreenStateService, useValue : splashScreenService },
                { provide : SecureStorageService, useValue : secureStorageService }
            ],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
        });

        fixture = TestBed.createComponent(ApiTestingComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        service = {
            getSecretValue : jest.fn().mockImplementation(() : Observable<any> => {
                return of({ value : "my-secret-value" });
            })
        };
        dialog = {
            open : jest.fn().mockReturnValue({ afterClosed : () => of(true) })
        }
        splashScreenService = {
            start : jest.fn(),
            stop : jest.fn()
        };
        //formBuilder = FORM_GROUP_MOCK;
        secureStorageService = {
            getItem : jest.fn().mockImplementation((key) => "apiKey" === key ? "test" : "secret1"),
            setItem : jest.fn()
        };
    });

    it('should return secret value with saved credentials', () => {
        // arrange
        configTestBed();

        // act
        component.callApi();

        // assert
        expect(component.apiKey).toEqual('test');
        expect(component.secretId).toEqual('secret1');
        expect(component).toBeTruthy();
        expect(component.apiResponse).toEqual("{\"value\":\"my-secret-value\"}");
        expect(splashScreenService.start).toHaveBeenCalled();
        expect(splashScreenService.stop).toHaveBeenCalled();
        expect(secureStorageService.getItem).toHaveBeenCalled();
        expect(secureStorageService.setItem).toHaveBeenCalled();
    });

    it('should return secret value', () => {
        // arrange
        configTestBed();

        component.apiKey = "api-key1";
        component.secretId = "secret-id1";

        // act
        component.callApi();

        // assert
        expect(component).toBeTruthy();
        expect(component.apiResponse).toEqual("{\"value\":\"my-secret-value\"}");
        expect(splashScreenService.start).toHaveBeenCalled();
        expect(splashScreenService.stop).toHaveBeenCalled();
        expect(secureStorageService.setItem).toHaveBeenCalled();
    });

    it('should throw error', () => {
        // arrange
        service = {
            getSecretValue : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})))
        };
        configTestBed();

        component.apiKey = "api-key2";
        component.secretId = "secret-id2";

        // act
        component.callApi();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledTimes(1);
        expect(dialog.open).toBeCalledWith(InfoDialog, { data: { text: "Unexpected error occurred: OOPS!", type: "warning" } as DialogData });
        expect(splashScreenService.start).toHaveBeenCalled();
    });
});