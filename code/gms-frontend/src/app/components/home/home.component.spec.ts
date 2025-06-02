import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { ReplaySubject, Subscription, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { SharedDataService } from "../../common/service/shared-data-service";
import { User } from "../user/model/user.model";
import { HomeComponent, PageStatus } from "./home.component";
import { HomeData } from "./model/home-data.model";
import { HomeService } from "./service/home.service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
describe('HomeComponent', () => {
    let component: HomeComponent;
    let fixture: ComponentFixture<HomeComponent>;
    // Injected services
    let sharedData: any;
    let homeService: any;
    let mockHomeData: HomeData;
    let mockSubject: ReplaySubject<User | undefined>;
    let authModeSubject: ReplaySubject<string>;

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports: [RouterTestingModule, AngularMaterialModule, MomentPipe, TranslatorModule],
            schemas: [CUSTOM_ELEMENTS_SCHEMA],
            declarations: [HomeComponent],
            providers: [
                { provide: SharedDataService, useValue: sharedData },
                { provide: HomeService, useValue: homeService }
            ]
        });

        fixture = TestBed.createComponent(HomeComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        mockSubject = new ReplaySubject<User | undefined>();
        authModeSubject = new ReplaySubject<string>();

        sharedData = {
            init: jest.fn(),
            check: jest.fn(),
            userSubject$: mockSubject,
            getUserInfo: jest.fn(),
            clearData: jest.fn(),
            authModeSubject$: authModeSubject
        };

        homeService = {
            getData: jest.fn()
        };
    });

    it('should handle errors', async() => {
        homeService.getData = jest.fn().mockReturnValue(throwError(() => new Error("error")));
        configTestBed();

        // assert
        expect(component).toBeTruthy();
        expect(component.data).toBeUndefined();
    });

    it('should load component for admin', () => {
        const currentUser = {
            role: "ROLE_ADMIN",
            username: "test1",
            id: 1
        };
        mockHomeData = {
            events: {
                resultList: [{ id: 1, entityId:1, source: "UI", target: 'apikey', username: 'user-1', operation: 'save', eventDate: new Date() }],
                totalElements: 0
            },
            announcements: {
                resultList: [],
                totalElements: 0
            },
            userCount: 1,
            role: 'ROLE_ADMIN',
            apiKeyCount: 0,
            keystoreCount: 0,
            announcementCount: 0,
            secretCount: 0
        };
        homeService.getData = jest.fn().mockReturnValue(of(mockHomeData));
        configTestBed();
        mockSubject.next(currentUser);
        authModeSubject.next("db");

        // assert
        expect(component).toBeTruthy();
        expect(component.data).toBeDefined();
    });

    it('should not load component for unknown user', () => {
        mockHomeData = {
            events: {
                resultList: [{ id: 1, entityId:1, source: "UI", target: 'apikey', username: 'user-1', operation: 'save', eventDate: new Date() }],
                totalElements: 0
            },
            announcements: {
                resultList: [],
                totalElements: 0
            },
            userCount: 1,
            apiKeyCount: 0,
            keystoreCount: 0,
            announcementCount: 0,
            secretCount: 0
        };
        homeService.getData = jest.fn().mockReturnValue(of(mockHomeData));
        configTestBed();
        mockSubject.next(undefined);
        authModeSubject.next("db");

        // assert
        expect(component).toBeTruthy();
        expect(component.data).toBeDefined();
    });
});