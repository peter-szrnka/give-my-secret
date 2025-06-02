import { CUSTOM_ELEMENTS_SCHEMA, EventEmitter, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router } from "@angular/router";
import { Observable, of, ReplaySubject } from "rxjs";
import { AppComponent } from "./app.component";
import { SystemReadyData } from "./common/model/system-ready.model";
import { User } from "./components/user/model/user.model";
import { SharedDataService } from "./common/service/shared-data-service";
import { SplashScreenStateService } from "./common/service/splash-screen-service";
import { Location } from "@angular/common";

/**
 * @author Peter Szrnka
 */
describe('AppComponent', () => {
    let component : AppComponent;
    let currentUser : User | any;
    let router : any;
    let sharedDataService : any;
    let splashScreenStateService : any;
    let fixture : ComponentFixture<AppComponent>;
    let mockSubject : ReplaySubject<User | undefined>;
    let mockSystemReadySubject : ReplaySubject<SystemReadyData>;
    let mockEvents: any[] = [];
    let mockLocation: any;
    let mockNavigationEmitter: EventEmitter<string> = new EventEmitter<string>();

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            declarations : [AppComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : SplashScreenStateService, useValue : splashScreenStateService },
                { provide : Router, useValue : router },
                { provide : Location, useValue: mockLocation }
            ]
        });

        fixture = TestBed.createComponent(AppComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        mockEvents = [
            new NavigationStart(0, 'http://localhost:4200/login'),
            new NavigationEnd(0, 'http://localhost:4200/login', 'http://localhost:4200/login'),
            new NavigationCancel(0, 'http://localhost:4200/login', 'user cancelled'),
            new NavigationError(0, 'http://localhost:4200/login', new Error())
        ];
        router = {
            navigate : jest.fn().mockReturnValue(of(true)),
            url: '',
            events: new Observable(observer => {
                mockEvents.forEach(event => observer.next(event));
                observer.complete();
            })
        };

        mockSubject = new ReplaySubject<User | undefined>();
        mockSystemReadySubject = new ReplaySubject<SystemReadyData>();

        sharedDataService = {
            init : jest.fn(),
            check : jest.fn(),
            userSubject$ : mockSubject,
            authMode : 'db',
            systemReadySubject$ : mockSystemReadySubject,
            getUserInfo : jest.fn(),
            clearData : jest.fn(),
            navigationChangeEvent: mockNavigationEmitter,
            resetAutomaticLogoutTimer: jest.fn()
        };

        splashScreenStateService = {
            start: jest.fn(),
            stop: jest.fn()
        };

        mockLocation = {
            path: jest.fn().mockReturnValue("/")
        };

        localStorage.clear();
    });

    it('Should create component', () => {
        configureTestBed();
        expect(component).toBeTruthy();
        component.toggleTextMenuVisibility();
    });

    it('User is admin', () => {
        currentUser = {
            role : "ROLE_ADMIN",
            username : "test1",
            id : 1
        };
        router.url = '/user/list';
        mockSubject.next(currentUser);
        mockSystemReadySubject.next({ ready: true, status: 200, authMode : 'db', systemStatus: 'OK' });
        configureTestBed();
        
        // act & assert
        expect(component.isAdmin).toEqual(true);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it.each([
        [true], [false]
    ])('User is a normal user with nav setting=%s', (showTexts : boolean) => {
        localStorage.setItem('showTextsInSidevNav', showTexts.toString());
        currentUser = {
            role : "ROLE_USER",
            username : "test1",
            id : 1
        };
        configureTestBed();
        mockSubject.next(currentUser);
        mockSystemReadySubject.next({ ready: true, status: 200, authMode: 'db', systemStatus: 'OK' });
        component.toggleTextMenuVisibility();

        // act & assert
        expect(component.isAdmin).toEqual(false);
        expect(component.showTexts).toEqual(!showTexts);
        localStorage.removeItem('showTextsInSidevNav');
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it('System is offline', () => {
        configureTestBed();
        mockSystemReadySubject.next({ ready: false, status: 0, authMode : '', systemStatus: 'NEED_SETUP' });
        fixture.autoDetectChanges();

        // act & assert
        expect(router.navigate).toHaveBeenCalledTimes(1);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it('System is not ready', () => {
        configureTestBed();
        mockSystemReadySubject.next({ ready: false, status: 200, authMode : 'db', automaticLogoutTimeInMinutes: 1, systemStatus: 'NEED_SETUP' });
        fixture.autoDetectChanges();

        // act & assert
        expect(router.navigate).toHaveBeenCalledTimes(1);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it('Unexpected error during ready data query', () => {
        configureTestBed();
        mockSystemReadySubject.next({ ready: true, status: 500, authMode : 'db', systemStatus: 'OK' });
        fixture.autoDetectChanges();

        // act & assert
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it('No available user', () => {
        sharedDataService.getUserInfo = jest.fn().mockReturnValue(undefined);
        router.url = '/api_key/list';
        configureTestBed();
        mockSubject.next(undefined); 
        mockSystemReadySubject.next({ ready: true, status: 200, authMode : 'db', systemStatus: 'OK' });
        fixture.detectChanges();

        // act & assert
        expect(router.navigate).toHaveBeenCalled();
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it('No available user and set previousUrl parameter', () => {
        sharedDataService.getUserInfo = jest.fn().mockReturnValue(undefined);
        router.url = '/api_key/list';
        mockLocation.path = jest.fn().mockReturnValue("");
        configureTestBed();
        mockSubject.next(undefined); 
        mockSystemReadySubject.next({ ready: true, status: 200, authMode : 'db', systemStatus: 'OK' });
        fixture.detectChanges();

        // act & assert
        expect(router.navigate).toHaveBeenCalled();
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it('should not log out', () => {
        sharedDataService.getUserInfo = jest.fn().mockReturnValue(undefined);
        configureTestBed();
        mockSubject.next(undefined);
        mockSystemReadySubject.next({ ready: false, status: 500, authMode : 'db', systemStatus: 'NEED_SETUP' });
        fixture.detectChanges();

        // act & assert
        expect(router.navigate).toHaveBeenCalledTimes(0);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it('Should navigate to main page', () => {
        currentUser = {
            role : "ROLE_ADMIN",
            username : "test1",
            id : 1
        };
        router.url = '/login';
        mockSubject.next(currentUser);
        mockSystemReadySubject.next({ ready: true, status: 200, authMode : 'db', systemStatus: 'OK' });
        configureTestBed();
        
        // act & assert
        expect(component.isAdmin).toEqual(true);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it('Should navigate to custom url after successful login', () => {
        currentUser = {
            role : "ROLE_ADMIN",
            username : "test1",
            id : 1
        };
        router.url = '/login';
        mockSubject.next(currentUser);
        mockSystemReadySubject.next({ ready: true, status: 200, authMode : 'db', systemStatus: 'OK' });
        mockNavigationEmitter.emit('/apikey/list');
        configureTestBed();
        
        // act & assert
        expect(component.isAdmin).toEqual(true);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });
});