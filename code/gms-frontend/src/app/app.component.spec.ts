import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import { EMPTY, of, ReplaySubject } from "rxjs";
import { AppComponent } from "./app.component";
import { SystemReadyData } from "./common/model/system-ready.model";
import { User } from "./components/user/model/user.model";
import { SharedDataService } from "./common/service/shared-data-service";
import { SplashScreenStateService } from "./common/service/splash-screen-service";

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

    beforeEach(() => {
        router = {
            navigate : jest.fn().mockReturnValue(of(true))
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
            clearDataAndReturn : jest.fn().mockReturnValue(of(EMPTY))
        };

        splashScreenStateService = {
            start : jest.fn()
        };

        TestBed.configureTestingModule({
            //imports : [RouterTestingModule],
            declarations : [AppComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : SplashScreenStateService, useValue : splashScreenStateService },
                { provide : Router, useValue : router }
            ]
        });

        fixture = TestBed.createComponent(AppComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('Should create component', () => {
        fixture.detectChanges();
        expect(component).toBeTruthy();
    });

    it('User is admin', () => {
        currentUser = {
            roles : ["ROLE_ADMIN"],
            username : "test1",
            id : 1
        };
        mockSubject.next(currentUser);
        mockSystemReadySubject.next({ ready: true, status: 200, authMode : 'db' });
        
        // act & assert
        expect(component.isNormalUser()).toEqual(false);
        expect(component.isAdmin()).toEqual(true);
    });

    it('User is a normal user', () => {
        localStorage.setItem('showTextsInSidevNav', 'true');
        currentUser = {
            roles : ["ROLE_USER"],
            username : "test1",
            id : 1
        };
        mockSubject.next(currentUser);
        mockSystemReadySubject.next({ ready: true, status: 200, authMode : 'db' });
        component.toggleTextMenuVisibility();

        // act & assert
        expect(component.isNormalUser()).toEqual(true);
        expect(component.isAdmin()).toEqual(false);
        expect(component.showTexts).toBeFalsy();
        localStorage.removeItem('showTextsInSidevNav');
    });

    it('No available user', () => {
        sharedDataService.getUserInfo = jest.fn().mockReturnValue(undefined);
        mockSubject.next(undefined);
        router.url = '/api_key/list';
        mockSystemReadySubject.next({ ready: true, status: 200, authMode : 'db' });
        fixture.detectChanges();

        // act & assert
        expect(router.navigate).toHaveBeenCalled();
    });

    it('should log out', () => {
        sharedDataService.getUserInfo = jest.fn().mockReturnValue(undefined);
        mockSubject.next(undefined);
        mockSystemReadySubject.next({ ready: false, status: 403, authMode : 'db' });
        fixture.detectChanges();

        // act & assert
        expect(router.navigate).toHaveBeenCalled();
    });
});