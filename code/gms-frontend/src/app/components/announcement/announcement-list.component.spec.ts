import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatDialog } from "@angular/material/dialog";
import { MatTableModule } from "@angular/material/table";
import { ActivatedRoute, Data } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { of, throwError } from "rxjs";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { User } from "../user/model/user.model";
import { AnnouncementService } from "./service/announcement-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { AnnouncementListComponent } from "./announcement-list.component";

/**
 * @author Peter Szrnka
 */
describe('AnnouncementListComponent', () => {
    let component : AnnouncementListComponent;
    const currentUser : User | any = {
        roles :  ["ROLE_USER" ]
    };
    // Injected services
    let service : any;
    let dialog : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    // Fixtures
    let fixture : ComponentFixture<AnnouncementListComponent>;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [RouterTestingModule, MatTableModule, PipesModule ],
            declarations : [AnnouncementListComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : AnnouncementService, useValue : service },
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
                itemList : [
                    {
                        id : 1,
                        userId : 1,
                        name : "my-api-key",
                        value : "test",
                        description : "string",
                        status : "ACTIVE",
                        creationDate : new Date()
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
        fixture = TestBed.createComponent(AnnouncementListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should handle resolver error', () => {
        activatedRoute.data = throwError(() => new Error("Unexpected error!"));
        configureTestBed();
        fixture = TestBed.createComponent(AnnouncementListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.datasource).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should return empty table | Invalid user', () => {
        configureTestBed();
        fixture = TestBed.createComponent(AnnouncementListComponent);
        component = fixture.componentInstance;

        jest.spyOn(component.sharedData, 'getUserInfo').mockReturnValue(undefined);
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should delete an item', () => {
        configureTestBed();
        fixture = TestBed.createComponent(AnnouncementListComponent);
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
        fixture = TestBed.createComponent(AnnouncementListComponent);
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
