import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { User } from "../user/model/user.model";
import { KeystoreListComponent } from "./keystore-list.component";
import { KeystoreService } from "./service/keystore-service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
describe('KeystoreListComponent', () => {
    let component : KeystoreListComponent;
    const currentUser : User = {
        role: 'ROLE_USER'
    };
    // Injected services
    let service : any;
    let dialogService : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    // Fixtures
    let fixture : ComponentFixture<KeystoreListComponent>;
    let router : any;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ AngularMaterialModule, BrowserAnimationsModule, MomentPipe, TranslatorModule ],
            declarations : [KeystoreListComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue: router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : KeystoreService, useValue : service },
                { provide : DialogService, useValue : dialogService },
                { provide : ActivatedRoute, useClass : activatedRoute }
            ]
        });
    };

    beforeEach(() => {
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
            delete : jest.fn().mockReturnValue(of("OK"))
        };

        router = {
            navigate : jest.fn()
        };
    });

    it('Should create component', () => {
        configureTestBed();
        fixture = TestBed.createComponent(KeystoreListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should handle resolver error', () => {
        activatedRoute.data = throwError(() => new Error("Unexpected error!"));
        configureTestBed();
        fixture = TestBed.createComponent(KeystoreListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should return empty table | Invalid user', () => {
        configureTestBed();
        fixture = TestBed.createComponent(KeystoreListComponent);
        component = fixture.componentInstance;
        jest.spyOn(component.sharedData, 'getUserInfo').mockResolvedValue(undefined);
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should delete an item', () => {
        configureTestBed();
        fixture = TestBed.createComponent(KeystoreListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        jest.spyOn(dialogService, 'openConfirmDeleteDialog').mockReturnValue({ afterClosed : jest.fn().mockReturnValue(of(true)) } as any);

        component.promptDelete(1);

        expect(dialogService.openConfirmDeleteDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should cancel dialog', () => {
        configureTestBed();
        fixture = TestBed.createComponent(KeystoreListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        jest.spyOn(dialogService, 'openConfirmDeleteDialog').mockReturnValue({ afterClosed : jest.fn().mockReturnValue(of(false)) } as any);

        component.promptDelete(1);

        expect(component.dialogService.openConfirmDeleteDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });
});
