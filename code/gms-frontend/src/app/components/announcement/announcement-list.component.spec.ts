import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatDialog } from "@angular/material/dialog";
import { MatTableModule } from "@angular/material/table";
import { ActivatedRoute, Data } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { of } from "rxjs";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { User } from "../../common/model/user.model";
import { AnnouncementService } from "../../common/service/announcement-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { AnnouncementListComponent } from "./announcement-list.component";


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
                        // TODO updte
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
        }).compileComponents();
    });

    it('Should create component', () => {
        fixture = TestBed.createComponent(AnnouncementListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should handle resolver error', () => {
        fixture = TestBed.createComponent(AnnouncementListComponent);
        component = fixture.componentInstance;
        spyOn(component.activatedRoute, 'data').and.throwError("Unexpected error!");
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.datasource).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should return empty table | Invalid user', () => {
        fixture = TestBed.createComponent(AnnouncementListComponent);
        component = fixture.componentInstance;
        spyOn(component.sharedData, 'getUserInfo').and.returnValue(undefined);
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should delete an item', () => {
        fixture = TestBed.createComponent(AnnouncementListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.datasource).toBeTruthy();

        spyOn(component.dialog, 'open').and.returnValue({afterClosed : jest.fn().mockReturnValue(of(true))});

        component.promptDelete(1);

        expect(component.dialog.open).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should cancel dialog', () => {
        fixture = TestBed.createComponent(AnnouncementListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.datasource).toBeTruthy();

        spyOn(component.dialog, 'open').and.returnValue({afterClosed : jest.fn().mockReturnValue(of(false))});

        component.promptDelete(1);

        expect(component.dialog.open).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });
});
