import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatDialog } from "@angular/material/dialog";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { User } from "../../common/model/user.model";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SystemPropertyListComponent } from "./system-property-list.component";
import { SystemPropertyService } from "../../common/service/system-property.service";


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
            imports : [RouterTestingModule, AngularMaterialModule, BrowserAnimationsModule, PipesModule ],
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
            navigate : jest.fn()
        };

        sharedDataService = {
            getUserInfo : jest.fn().mockReturnValue(currentUser)
        };

        dialog = {
            open : jest.fn()
        }
        
        activatedRoute = class {
            data : Data = of({
                itemList : [
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
                ]
            })
        };

        service = {
            delete : jest.fn().mockReturnValue(of("OK"))
        };
    });

    it('Should create component', () => {
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should handle resolver error', () => {
        activatedRoute = class {
            data : Data = throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"}))
        };
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.datasource).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should return empty table | Invalid user', () => {
        spyOn(component.sharedData, 'getUserInfo').and.returnValue(undefined);
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should delete an item', () => {
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.datasource).toBeTruthy();

        spyOn(component.dialog, 'open').and.returnValue({afterClosed : jest.fn().mockReturnValue(of(true))});

        component.promptDelete('REFRESH_JWT_ALGORITHM');

        expect(component.dialog.open).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should cancel dialog', () => {
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.datasource).toBeTruthy();

        spyOn(component.dialog, 'open').and.returnValue({afterClosed : jest.fn().mockReturnValue(of(false))});

        component.promptDelete('REFRESH_JWT_ALGORITHM');

        expect(component.dialog.open).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });
});
