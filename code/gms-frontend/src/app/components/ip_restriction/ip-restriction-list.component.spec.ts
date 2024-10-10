import { HttpErrorResponse } from "@angular/common/http";
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
import { IpRestrictionListComponent } from "./ip-restriction-list.component";
import { IpRestrictionService } from "./service/ip-restriction.service";

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
            imports : [ AngularMaterialModule, BrowserAnimationsModule, MomentPipe ],
            declarations : [IpRestrictionListComponent],
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
            navigate : jest.fn(),
            navigateByUrl : jest.fn().mockResolvedValue(true)
        };

        sharedDataService = {
            getUserInfo : jest.fn().mockReturnValue(Promise.resolve(currentUser)),
            refreshCurrentUserInfo: jest.fn()
        };

        dialogService = {
            openConfirmDeleteDialog : jest.fn().mockReturnValue({ afterClosed : jest.fn().mockReturnValue(of(true)) })
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
            })
        };

        service = {
            delete : jest.fn().mockReturnValue(of("OK")),
            toggle : jest.fn().mockReturnValue(of("OK"))
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
            data : Data = throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"}))
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
});
