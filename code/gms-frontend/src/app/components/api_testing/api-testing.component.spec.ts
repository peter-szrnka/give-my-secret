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
import { vi } from "vitest";
import { TranslatorPipe } from "../../common/components/pipes/translator/translator.pipe";

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
            imports : [ ApiTestingComponent, FormsModule, AngularMaterialModule, NoopAnimationsModule, TranslatorPipe ],
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
            getUserInfo : vi.fn().mockResolvedValue({ id: 1, role: 'USER', username: 'test' } as User)
        };

        service = {
            getSecretValue : vi.fn().mockImplementation(() : Observable<any> => {
                return of({ value : "my-secret-value" });
            })
        };
        dialogService = {
            openNewDialog : vi.fn().mockReturnValue({ afterClosed : () => of(true) })
        };
        splashScreenService = {
            start : vi.fn(),
            stop : vi.fn()
        };
        secureStorageService = {
            getItem : vi.fn().mockImplementation((_username, key) => "apiKey" === key ? "test" : "secret1"),
            setItem : vi.fn(),
            getItemWithoutEncryption: vi.fn().mockReturnValue('en')
        };
    });

    it('should return secret value with saved credentials', async() => {
        // arrange
        sharedData.getUserInfo = vi.fn().mockResolvedValue(undefined);
        configTestBed();
        fixture.autoDetectChanges();

        component.apiKey = "test";
        component.secretId = "secret1";

        // act
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
            getSecretValue : vi.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})))
        };
        configTestBed();
        fixture.detectChanges();

        component.apiKey = "api-key2";
        component.secretId = "secret-id2";

        // act
        component.callApi();

        // assert
        expect(component).toBeTruthy();
        expect(dialogService.openNewDialog).toHaveBeenCalledTimes(1);
        expect(splashScreenService.start).toHaveBeenCalled();
    });
});