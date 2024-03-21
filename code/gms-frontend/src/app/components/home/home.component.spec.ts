import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { AngularMaterialModule } from "../../angular-material-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { HomeComponent } from "./home.component";
import { HomeService } from "./service/home.service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { ReplaySubject, of, throwError } from "rxjs";
import { User } from "../user/model/user.model";
import { HomeData } from "./model/home-data.model";
import { HttpErrorResponse } from "@angular/common/http";

/**
 * @author Peter Szrnka
 */
describe('HomeComponent', () => {
    let component : HomeComponent;
    let fixture : ComponentFixture<HomeComponent>;
    // Injected services
    let sharedData: any;
    let homeService : any;
    let mockHomeData: HomeData;
    let mockSubject : ReplaySubject<User | undefined>;
    let authModeSubject: ReplaySubject<string>;

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ RouterTestingModule, AngularMaterialModule, PipesModule ],
            schemas : [CUSTOM_ELEMENTS_SCHEMA],
            declarations : [HomeComponent],
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
            init : jest.fn(),
            check : jest.fn(),
            userSubject$: mockSubject,
            getUserInfo : jest.fn(),
            clearData : jest.fn(),
            authModeSubject$: authModeSubject
        };

        homeService = {
            getData: jest.fn()
        };
    });

    it('should load component for admin', () => {
        const currentUser = {
            roles : ["ROLE_ADMIN"],
            username : "test1",
            id : 1
        };
        mockHomeData = {
           events: {
            resultList: [{id: 1, target: 'apikey', username: 'user-1', operation:'save', eventDate: new Date()}],
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
        homeService.getData = jest.fn().mockReturnValue(of(mockHomeData))
        configTestBed();
        mockSubject.next(currentUser);
        authModeSubject.next("db");

        // assert
        expect(component).toBeTruthy();
        expect(component.eventDataSource).toBeDefined();
    });

    it('should handle unknown error', () => {
        const currentUser = {
            roles : ["ROLE_ADMIN"],
            username : "test1",
            id : 1
        };
        homeService.getData = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})))
        configTestBed();
        mockSubject.next(currentUser);
        authModeSubject.next("db");

        // assert
        expect(component).toBeTruthy();
        expect(component.error).toEqual('OOPS!');
    });

    it('should not load component for unknown user', () => {
        configTestBed();
        mockSubject.next(undefined);
        authModeSubject.next("db");

        // assert
        expect(component).toBeTruthy();
        expect(component.eventDataSource).toBeDefined();

        component.ngOnDestroy();
    });
});