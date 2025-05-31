import { HttpErrorResponse } from "@angular/common/http";
import { NO_ERRORS_SCHEMA } from "@angular/compiler";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { Observable, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { DialogService } from "../../common/service/dialog-service";
import { SecureStorageService } from "../../common/service/secure-storage.service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { User } from "../user/model/user.model";
import { ApiTestingComponent } from "./api-testing.component";
import { ApiTestingService } from "./service/api-testing-service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
describe('ApiTestingComponent', () => {

    let component : ApiTestingComponent;
    let sharedData: any;
    let service : any;
    let splashScreenService : any;
    let dialogService : any;
    let secureStorageService : any;

    // Fixtures
    let fixture : ComponentFixture<ApiTestingComponent>;

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ FormsModule, AngularMaterialModule, NoopAnimationsModule, TranslatorModule ],
            declarations : [ ApiTestingComponent ],
            providers : [
                { provide : SharedDataService, useValue: sharedData },
                { provide : ApiTestingService, useValue : service },
                { provide : DialogService, useValue : dialogService },
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
        sharedData = {
            getUserInfo : jest.fn().mockResolvedValue({ id: 1, role: 'USER', username: 'test' } as User)
        };

        service = {
            getSecretValue : jest.fn().mockImplementation(() : Observable<any> => {
                return of({ value : "my-secret-value" });
            })
        };
        dialogService = {
            openNewDialog : jest.fn().mockReturnValue({ afterClosed : () => of(true) })
        };
        splashScreenService = {
            start : jest.fn(),
            stop : jest.fn()
        };
        secureStorageService = {
            getItem : jest.fn().mockImplementation((_username, key) => "apiKey" === key ? "test" : "secret1"),
            setItem : jest.fn(),
            getItemWithoutEncryption: jest.fn().mockReturnValue('en')
        };
    });

    it('should return secret value with saved credentials', async() => {
        // arrange
        sharedData.getUserInfo = jest.fn().mockResolvedValue(undefined);
        configTestBed();

        component.apiKey = "test";
        component.secretId = "secret1";

        // act
        fixture.autoDetectChanges();
        component.callApi();

        // assert
        expect(component.apiKey).toEqual('test');
        expect(component.secretId).toEqual('secret1');
        expect(component).toBeTruthy();
        expect(component.apiResponse).toEqual("{\"value\":\"my-secret-value\"}");
        expect(splashScreenService.start).toHaveBeenCalled();
        expect(splashScreenService.stop).toHaveBeenCalled();
        expect(secureStorageService.setItem).toHaveBeenCalled();
    });

    it('should return secret value', async() => {
        // arrange
        configTestBed();

        component.apiKey = "api-key1";
        component.secretId = "secret-id1";

        // act
        //fixture.detectChanges();
        component.callApi();

        // assert
        expect(component).toBeTruthy();
        expect(component.apiResponse).toEqual("{\"value\":\"my-secret-value\"}");
        expect(splashScreenService.start).toHaveBeenCalled();
        expect(splashScreenService.stop).toHaveBeenCalled();
        expect(secureStorageService.setItem).toHaveBeenCalled();
        expect(dialogService.openNewDialog).not.toHaveBeenCalled();
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
        fixture.detectChanges();
        component.callApi();

        // assert
        expect(component).toBeTruthy();
        expect(dialogService.openNewDialog).toHaveBeenCalledTimes(1);
        expect(splashScreenService.start).toHaveBeenCalled();
    });
});