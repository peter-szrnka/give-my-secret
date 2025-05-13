import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { Observable, ReplaySubject, of } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { IEntitySaveResponseDto } from "../../common/model/entity-save-response.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { Event } from "../event/model/event.model";
import { EventService } from "../event/service/event-service";
import { UserData } from "./model/user-data.model";
import { UserService } from "./service/user-service";
import { UserDetailComponent } from "./user-detail.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
describe('UserDetailComponent', () => {
    let component : UserDetailComponent;
    let fixture : ComponentFixture<UserDetailComponent>;
    // Injected services
    let router : any;
    let eventServiceMock : any;
    let serviceMock : any;
    let dialogService : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    let authModeSubject: ReplaySubject<string>;
    let splashScreenStateService: any = {};

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [RouterTestingModule, AngularMaterialModule, NoopAnimationsModule, FormsModule, TranslatorModule ],
            declarations : [UserDetailComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue : router},
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : UserService, useValue : serviceMock },
                { provide : DialogService, useValue : dialogService },
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide : EventService, useValue : eventServiceMock },
                { provide : SplashScreenStateService, useValue : splashScreenStateService }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(UserDetailComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        authModeSubject = new ReplaySubject<string>();
        router = {

        };
        sharedDataService = {
            refreshCurrentUserInfo: jest.fn(),
            authModeSubject$: authModeSubject
        };

        dialogService = {
            openNewDialog : jest.fn().mockReturnValue({ afterClosed: jest.fn().mockReturnValue(of()) })
        }
        
        activatedRoute = class {
            data : Data = of({
                entity : {
                    id : 1,
                    name : "Test User",
                    role : "ROLE_USER",
                    email : "test.email@mail.com",
                    status : "ACTIVE",
                    username : "user.name"
                } as UserData
            })
        };

        eventServiceMock = {
            listByUserId : jest.fn().mockReturnValue(of([ { id: 1, username: 'user1', eventDate: new Date(), operation: 'SAVE', target: 'KEYSTORE' } as Event ]))
        };

        serviceMock = {
            save : jest.fn().mockReturnValue(of({ entityId : 1, success : true }) as Observable<IEntitySaveResponseDto>)
        };

        splashScreenStateService = {
            start: jest.fn(),
            stop: jest.fn()
        };
    });

    it('should save new entity', () => {
        // arrange
        eventServiceMock = {
            listByUserId : jest.fn().mockReturnValue(of(undefined))
        };

        activatedRoute = class {
            data : Data = of({
                entity : {
                    name : "Test User",
                    role : "ROLE_USER" ,
                    email : "test.email@mail.com",
                    status : "ACTIVE",
                    username : "user.name"
                } as UserData
            })
        };

        configureTestBed();

        // act
        authModeSubject.next("db");
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialogService.openNewDialog).toHaveBeenCalled();
    });

    it('should save details', () => {
        // arrange
        configureTestBed();

        // act
        authModeSubject.next("db");
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialogService.openNewDialog).toHaveBeenCalled();
    });

    it('should toggle password display', () => {
        // arrange
        configureTestBed();
        // act
        component.togglePasswordDisplay();

        // assert
        expect(component).toBeTruthy();
        expect(component.showPassword).toBe(true);
    });
});