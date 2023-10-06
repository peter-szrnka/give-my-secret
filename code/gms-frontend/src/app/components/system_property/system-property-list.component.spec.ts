import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { SharedDataService } from "../../common/service/shared-data-service";
import { User } from "../user/model/user.model";
import { SystemProperty } from "./model/system-property.model";
import { SystemPropertyService } from "./service/system-property.service";
import { PROPERTY_TEXT_MAP, SystemPropertyListComponent } from "./system-property-list.component";

/**
 * @author Peter Szrnka
 */
describe('SystemPropertyListComponent', () => {
    let component : SystemPropertyListComponent;
    const currentUser : User | any = {
        roles :  [ "ROLE_ADMIN" ]
    };
    // Injected services
    let router : any;
    let service : any;
    let dialog : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    // Fixtures
    let fixture : ComponentFixture<SystemPropertyListComponent>;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [RouterTestingModule, ReactiveFormsModule, FormsModule, AngularMaterialModule, BrowserAnimationsModule, PipesModule ],
            declarations : [SystemPropertyListComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue : router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : SystemPropertyService, useValue : service },
                { provide : MatDialog, useValue : dialog },
                { provide : ActivatedRoute, useClass : activatedRoute }
            ]
        });

        fixture = TestBed.createComponent(SystemPropertyListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        router = {
            navigate : jest.fn().mockReturnValue(of(true))
        };

        sharedDataService = {
            getUserInfo : jest.fn().mockReturnValue(Promise.resolve(currentUser)),
            refreshCurrentUserInfo: jest.fn()
        };

        dialog = {
            open : jest.fn()
        }
        
        activatedRoute = class {
            data : Data = of({
                data : {
                    resultList : [
                        {
                            key : "REFRESH_JWT_ALGORITHM",
                            value : "HS512",
                            factoryValue : true,
                            lastModified : new Date()
                        },
                        {
                            key : "OLD_EVENT_TIME_LIMIT_DAYS",
                            value : "1",
                            factoryValue : true,
                            lastModified : new Date()
                        }
                    ],
                    totalElements : 2
                }
            })
        };

        service = {
            save : jest.fn().mockReturnValue(of("")),
            delete : jest.fn().mockReturnValue(of("OK"))
        };
    });

    it.each([
        ['LONG', 'number'],
        ['STRING', 'text'],
      ])('Should return input type for %i', (input, expected) => {
        configureTestBed();

        component.onFetch({ pageSize : 1});
        // act
        const response = component.getInputType(input);

        // assert
        expect(response).toEqual(expected);
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
        // arrange
        sharedDataService = {
            getUserInfo : jest.fn().mockReturnValue(undefined)
        };
        configureTestBed();

        // act &assert
        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should save fail', () => {
        // arrange
        service.save = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})));
        configureTestBed();

        expect(component).toBeTruthy();

        const mockDialogRef : any = { afterClosed : jest.fn().mockReturnValue(of(true)) };
        jest.spyOn(component.dialog, 'open').mockReturnValue(mockDialogRef);

        // act
        component.save({ key : 'X', value : 'value', type : 'string' } as SystemProperty);

        // assert
        expect(component.dialog.open).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should save succeed', () => {
        // arrange
        configureTestBed();

        expect(component).toBeTruthy();

        const mockDialogRef : any = { afterClosed : jest.fn().mockReturnValue(of(true)) };
        jest.spyOn(component.dialog, 'open').mockReturnValue(mockDialogRef);

        // act
        const valueSet : string[] = component.getValueSet('REFRESH_JWT_ALGORITHM');
        const valueSetUnknown : string[] = component.getValueSet('UNKNOWN');
        component.save({ key : 'X', value : 'value', type : 'string' } as SystemProperty);

        // assert
        expect(valueSet).toEqual(PROPERTY_TEXT_MAP['REFRESH_JWT_ALGORITHM'].valueSet);
        expect(valueSetUnknown).toEqual([]);
        expect(component.dialog.open).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
        expect(router.navigate).toBeCalledWith(['/system_property/list']);
    });

    it('Should delete an item', () => {
        // arrange
        configureTestBed();

        expect(component).toBeTruthy();

        const mockDialogRef : any = { afterClosed : jest.fn().mockReturnValue(of(true)) };
        jest.spyOn(component.dialog, 'open').mockReturnValue(mockDialogRef);

        // act
        component.promptDelete('REFRESH_JWT_ALGORITHM');

        expect(service.delete).toHaveBeenCalled();
        expect(component.dialog.open).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
        expect(router.navigate).toBeCalledWith(['/system_property/list']);
    });

    it('Should cancel dialog after delete', () => {
        // arrange
        configureTestBed();

        expect(component).toBeTruthy();

        const mockDialogRef : any = { afterClosed : jest.fn().mockReturnValue(of(false)) };
        jest.spyOn(component.dialog, 'open').mockReturnValue(mockDialogRef);

        // act
        component.promptDelete('REFRESH_JWT_ALGORITHM');

        // assert
        expect(service.delete).toHaveBeenCalledTimes(0);
        expect(component.dialog.open).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });
});
