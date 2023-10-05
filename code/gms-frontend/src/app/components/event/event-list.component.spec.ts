import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatDialog } from "@angular/material/dialog";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { User } from "../user/model/user.model";
import { EventService } from "./service/event-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { EventListComponent } from "./event-list.component";

/**
 * @author Peter Szrnka
 */
describe('EventListComponent', () => {
    let component : EventListComponent;
    const currentUser : User | any = {
        roles :  ["ROLE_USER" ]
    };
    // Injected services
    let service : any;
    let dialog : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    let router : any;
    // Fixtures
    let fixture : ComponentFixture<EventListComponent>;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ AngularMaterialModule, BrowserAnimationsModule, PipesModule ],
            declarations : [EventListComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue: router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : EventService, useValue : service },
                { provide : MatDialog, useValue : dialog },
                { provide : ActivatedRoute, useClass : activatedRoute }
            ]
        });
    };

    beforeEach(() => {
        sharedDataService = {
            getUserInfo : jest.fn().mockReturnValue(currentUser)
        };

        dialog = {
            open : jest.fn()
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
            })
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
        fixture = TestBed.createComponent(EventListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should handle resolver error', () => {
        activatedRoute.data = throwError(() => new Error("Unexpected error!"));
        configureTestBed();
        fixture = TestBed.createComponent(EventListComponent);
        component = fixture.componentInstance;

        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.datasource).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should return empty table | Invalid user', () => {
        configureTestBed();
        fixture = TestBed.createComponent(EventListComponent);
        component = fixture.componentInstance;
        jest.spyOn(component.sharedData, 'getUserInfo').mockResolvedValue(undefined);
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should delete an item', () => {
        configureTestBed();
        fixture = TestBed.createComponent(EventListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.datasource).toBeTruthy();

        jest.spyOn(component.dialog, 'open').mockReturnValue({ afterClosed : jest.fn().mockReturnValue(of(true)) } as any);

        component.promptDelete(1);

        expect(component.dialog.open).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should cancel dialog', () => {
        configureTestBed();
        fixture = TestBed.createComponent(EventListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.datasource).toBeTruthy();

        jest.spyOn(component.dialog, 'open').mockReturnValue({ afterClosed : jest.fn().mockReturnValue(of(false)) } as any);

        component.promptDelete(1);

        expect(component.dialog.open).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });
});
