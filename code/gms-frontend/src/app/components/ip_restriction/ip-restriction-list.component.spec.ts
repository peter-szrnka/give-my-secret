import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorPipe } from "../../common/components/pipes/translator/translator.pipe";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { User } from "../user/model/user.model";
import { IpRestrictionListComponent } from "./ip-restriction-list.component";
import { IpRestrictionService } from "./service/ip-restriction.service";
import { vi } from "vitest";

/**
 * @author Peter Szrnka
 */
describe('IpRestrictionListComponent', () => {
    let component : IpRestrictionListComponent;
    const currentUser : User | any = {
        roles :  ["ROLE_USER" ]
    };
    // Injected services
    let router : any;
    let service : any;
    let dialogService : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    // Fixtures
    let fixture : ComponentFixture<IpRestrictionListComponent>;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ IpRestrictionListComponent, AngularMaterialModule, BrowserAnimationsModule, MomentPipe, TranslatorPipe ],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue : router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : IpRestrictionService, useValue : service },
                { provide : DialogService, useValue : dialogService },
                { provide : ActivatedRoute, useClass : activatedRoute }
            ]
        });

        fixture = TestBed.createComponent(IpRestrictionListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        router = {
            navigate : vi.fn(),
            navigateByUrl : vi.fn().mockResolvedValue(true)
        };

        sharedDataService = {
            getUserInfo : vi.fn().mockReturnValue(Promise.resolve(currentUser)),
            refreshCurrentUserInfo: vi.fn()
        };

        dialogService = {
            openConfirmDeleteDialog : vi.fn().mockReturnValue({ afterClosed : vi.fn().mockReturnValue(of(true)) })
        };
        
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
            delete : vi.fn().mockReturnValue(of("OK")),
            toggle : vi.fn().mockReturnValue(of("OK"))
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
            snapshot = { queryParams : { page : 0 } };
        };
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should return empty table | Invalid user', () => {
        vi.spyOn(component.sharedData, 'getUserInfo').mockResolvedValue(undefined);
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should delete an item', () => {
        configureTestBed();

        expect(component).toBeTruthy();

        const mockDialogRef : any = { afterClosed : vi.fn().mockReturnValue(of(true)) };
        vi.spyOn(dialogService, 'openConfirmDeleteDialog').mockReturnValue(mockDialogRef);

        component.promptDelete(1);

        expect(dialogService.openConfirmDeleteDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should cancel dialog', () => {
        configureTestBed();

        expect(component).toBeTruthy();

        const mockDialogRef : any = { afterClosed : vi.fn().mockReturnValue(of(false)) };
        vi.spyOn(dialogService, 'openConfirmDeleteDialog').mockReturnValue(mockDialogRef);

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
});
