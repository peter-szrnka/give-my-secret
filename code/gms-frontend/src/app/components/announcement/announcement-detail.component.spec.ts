import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { Observable, of } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { IEntitySaveResponseDto } from "../../common/model/entity-save-response.model";
import { User } from "../user/model/user.model";
import { AnnouncementService } from "./service/announcement-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { AnnouncementDetailComponent } from "./announcement-detail.component";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";

/**
 * @author Peter Szrnka
 */
describe('AnnouncementDetailComponent', () => {
    let component : AnnouncementDetailComponent;
    const currentUser : User | any = {
        roles :  ["ROLE_USER" ]
    };
    // Injected services
    let router : any;
    let service : any;
    let dialog : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    let splashScreenStateService: any = {};
    // Fixtures
    let fixture : ComponentFixture<AnnouncementDetailComponent>;

    beforeEach(() => {
        router = {

        };

        dialog = {
            open : jest.fn()
        }
        sharedDataService = {
            getUserInfo : jest.fn().mockReturnValue(currentUser),
            refreshCurrentUserInfo: jest.fn()
        };

        service = {
            save : function() : Observable<IEntitySaveResponseDto> {
                return of({success:true, entityId:1 });
            }
        };

        activatedRoute = class {
            data : Data = of({
                "entity" : {
                        id : 1,
                        userId : 1,
                        name : "my-api-key",
                        value : "test",
                        description : "string",
                        status : "ACTIVE",
                        creationDate : new Date()
                    }
            })
        };

        splashScreenStateService = {
            start: jest.fn(),
            stop: jest.fn()
        };

        TestBed.configureTestingModule({
            imports : [RouterTestingModule, BrowserAnimationsModule, FormsModule, AngularMaterialModule, PipesModule ],
            declarations : [AnnouncementDetailComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue : router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : AnnouncementService, useValue : service },
                { provide : MatDialog, useValue : dialog },
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide : SplashScreenStateService, useValue : splashScreenStateService }
            ]
        }).compileComponents();
    });

    it('Should create component', () => {
        fixture = TestBed.createComponent(AnnouncementDetailComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
    });

    it('Should save entity', () => {
        fixture = TestBed.createComponent(AnnouncementDetailComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();

        jest.spyOn(component.dialog, 'open').mockReturnValue({ afterClosed : jest.fn().mockReturnValue(of(true)) } as any);

        component.save();
        expect(component.dialog.open).toHaveBeenCalled();
    });
});