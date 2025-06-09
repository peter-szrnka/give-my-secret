import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { ClipboardService } from "../../common/service/clipboard-service";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { User } from "../user/model/user.model";
import { ApiKeyListComponent } from "./apikey-list.component";
import { ApiKeyService } from "./service/apikey-service";

/**
 * @author Peter Szrnka
 */
describe('ApiKeyListComponent', () => {
    let component : ApiKeyListComponent;
    const currentUser : User | any = {
        roles :  ["ROLE_USER" ]
    };
    // Injected services
    let router : any;
    let service : any;
    let dialogService : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    let clipboardService : any;
    // Fixtures
    let fixture : ComponentFixture<ApiKeyListComponent>;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ ApiKeyListComponent, AngularMaterialModule, BrowserAnimationsModule, MomentPipe, TranslatorModule ],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue : router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : ApiKeyService, useValue : service },
                { provide : DialogService, useValue : dialogService },
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide : ClipboardService, useValue : clipboardService}
            ]
        });

        fixture = TestBed.createComponent(ApiKeyListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        router = {
            navigate : jest.fn(),
            navigateByUrl : jest.fn().mockResolvedValue(true)
        };

        sharedDataService = {
            getUserInfo : jest.fn().mockReturnValue(Promise.resolve(currentUser)),
            refreshCurrentUserInfo: jest.fn()
        };

        dialogService = {
            openConfirmDeleteDialog : jest.fn()
        }
        
        activatedRoute = class {
            data : Data = of({
                data : {
                    resultList : [
                        {
                            id : 1,
                            userId : 1,
                            name : "my-api-key",
                            value : "test",
                            description : "string",
                            status : "ACTIVE",
                            creationDate : new Date()
                        }
                    ],
                    totalElements : 1
                }
            });
            snapshot = {
                queryParams : {
                    page : 0
                }
            }
        };

        service = {
            delete : jest.fn().mockReturnValue(of("OK")),
            toggle : jest.fn().mockReturnValue(of("OK"))
        };

        clipboardService = {
            copyValue : jest.fn()
        };
    });

    it('Should create component', () => {
        configureTestBed();

        component.onFetch({ pageSize : 0 });
        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should handle resolver error', () => {
        activatedRoute = class {
            data : Data = throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"}));
            snapshot = { queryParams : { } };
        };
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should return empty table | Invalid user', () => {
        jest.spyOn(component.sharedData, 'getUserInfo').mockResolvedValue(undefined);
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should delete an item', () => {
        configureTestBed();

        expect(component).toBeTruthy();

        const mockDialogRef : any = { afterClosed : jest.fn().mockReturnValue(of(true)) };
        jest.spyOn(dialogService, 'openConfirmDeleteDialog').mockReturnValue(mockDialogRef);

        component.promptDelete(1);

        expect(dialogService.openConfirmDeleteDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should cancel dialog', () => {
        configureTestBed();

        expect(component).toBeTruthy();

        const mockDialogRef : any = { afterClosed : jest.fn().mockReturnValue(of(false)) };
        jest.spyOn(dialogService, 'openConfirmDeleteDialog').mockReturnValue(mockDialogRef);

        component.promptDelete(1);

        expect(dialogService.openConfirmDeleteDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should toggle(enable) an item', () => {
        configureTestBed();

        // act & assert
        expect(component).toBeTruthy();
        component.toggle(1, 'ACTIVE');

        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should copy value', () => {
        configureTestBed();

        // act
        component.copyApiKeyValue('value');

        // assert
        expect(clipboardService.copyValue).toHaveBeenCalled();
    });
});
