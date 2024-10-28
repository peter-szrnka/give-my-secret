import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { Observable, of } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { NavButtonVisibilityPipe } from "../../common/components/pipes/nav-button-visibility.pipe";
import { IEntitySaveResponseDto } from "../../common/model/entity-save-response.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { User } from "../user/model/user.model";
import { AnnouncementDetailComponent } from "./announcement-detail.component";
import { AnnouncementService } from "./service/announcement-service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

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
    let dialogService : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    let splashScreenStateService: any = {};
    // Fixtures
    let fixture : ComponentFixture<AnnouncementDetailComponent>;

    beforeEach(() => {
        router = {

        };
        dialogService = {
            openCustomDialogWithErrorCode : jest.fn().mockReturnValue({ afterClosed : jest.fn().mockReturnValue(of(true)) } as any)
        };
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
            imports : [ BrowserAnimationsModule, FormsModule, AngularMaterialModule, MomentPipe, NavButtonVisibilityPipe, TranslatorModule ],
            declarations : [AnnouncementDetailComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue : router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : AnnouncementService, useValue : service },
                { provide : DialogService, useValue : dialogService },
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

        jest.spyOn(dialogService, 'openCustomDialogWithErrorCode').mockReturnValue({ afterClosed : jest.fn().mockReturnValue(of(true)) } as any);

        component.save();
        expect(dialogService.openCustomDialogWithErrorCode).toHaveBeenCalled();
    });
});