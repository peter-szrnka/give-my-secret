import { TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { EMPTY, Observable, of, Subject, throwError } from "rxjs";
import { User } from "../../components/user/model/user.model";
import { SetupService } from "../../components/setup/service/setup-service";
import { SharedDataService } from "./shared-data-service";
import { Router } from "@angular/router";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { SystemStatusDto } from "../model/system-status.model";
import { AuthService } from "./auth-service";
import { HttpErrorResponse } from "@angular/common/http";
import { SystemReadyData } from "../model/system-ready.model";
import { InformationService } from "./info-service";

/**
 * @author Peter Szrnka
 */
describe('SharedDataService', () => {
  let router: any;
  let currentUser: any;
  let service: SharedDataService;
  let setupService: any;
  let mockSubject: Subject<User>;
  let mockSystemReadySubject: Subject<SystemReadyData>;
  let authService: any;
  let infoService: any;

  const configureTestBed = () => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: Router, useValue: router },
        { provide: SetupService, useValue: setupService },
        { provide: AuthService, useValue: authService },
        { provide: InformationService, useValue: infoService },
        SharedDataService
      ]
    });
    service = TestBed.inject(SharedDataService);
  };

  beforeEach(() => {
    router = {
      navigate: jest.fn(),
      url : "/test"
    };

    setupService = {
      checkReady: (): Observable<SystemStatusDto> => { return of({ status: 'OK', authMode: 'db' }); }
    };

    authService = {
      logout: jest.fn().mockReturnValue(of(EMPTY))
    };

    mockSubject = new Subject<User>();
    mockSystemReadySubject = new Subject<SystemReadyData>();

    currentUser = {
      roles: ["ROLE_ADMIN"],
      userName: "test1",
      userId: 1
    };
    mockSubject.next(currentUser);
    mockSystemReadySubject.next({ ready: true, status: 200, authMode: 'db' });

    
    infoService = {
      getUserInfo: jest.fn().mockResolvedValue(currentUser)
    };
  });

  it('should return OK', () => {
    currentUser = {
      roles: ["ROLE_ADMIN"],
      userName: "test1",
      userId: 1
    };
    configureTestBed();

    mockSubject.next(currentUser);

    mockSubject.subscribe(res => expect(res).toEqual(currentUser));
    mockSystemReadySubject.subscribe(res => expect(res).toEqual(false));
  });

  it('should clear data', () => {
    // assert
    mockSubject.subscribe(res => expect(res).toBeUndefined());

    // act
    configureTestBed();
    service.clearData();
  });

  it.each([
    [
      {
        roles: ["ROLE_ADMIN"],
        userName: "test1",
        userId: 1
      }
    ],
    [null]
  ])('should refresh current user info', (currentUser: User | null) => {
    // arrange
    infoService.getUserInfo = jest.fn().mockResolvedValue(currentUser);
    configureTestBed();

    mockSubject.subscribe(res => expect(res).toEqual(currentUser));

    // act
    service.refreshCurrentUserInfo();

    // assert
    expect(infoService.getUserInfo).toHaveBeenCalled();
  });

  it('should clear data and return', () => {
    mockSubject.subscribe(res => expect(res).toEqual({
      userId: undefined,
      userName: undefined
    }));

    // act
    configureTestBed();
    service.clearDataAndReturn({ value: 'mock' }).subscribe(data => {
      expect(data).toEqual({ value: 'mock' });
    });

    // assert
    authService.logout().subscribe(() => {
      expect(localStorage.removeItem('currentUser')).toHaveBeenCalled();
    });
    expect(authService.logout).toHaveBeenCalled();
    //
  });

  it('should run check', () => {
    // arrange
    const jwtData = {
      userId: "test1",
      userName: "test1",
      exp: new Date().getTime() + 100000,
      roles: ["ROLE_USER"]
    };

    configureTestBed();

    mockSubject.subscribe(res => expect(res).toEqual(jwtData));
    mockSystemReadySubject.subscribe(res => expect(res.ready).toEqual(true));

    // act
    service.check();
  });

  it('should run check and handle error', () => {
    // arrange
    const jwtData = {
      userId: "test1",
      userName: "test1",
      exp: new Date().getTime() + 100000,
      roles: ["ROLE_USER"]
    };

    setupService = {
      checkReady: () => throwError(() => new HttpErrorResponse({
        error: new Error("Authentication failed"),
        status: 401
      }))
    };

    configureTestBed();

    mockSubject.subscribe(res => expect(res).toEqual(jwtData));
    mockSystemReadySubject.subscribe(res => {
      expect(res.ready).toEqual(false);
      expect(res.status).toEqual(0);
    });

    // act
    service.check();
  });

  it('should get user info', async() => {
    // act
    configureTestBed();
    const response: User | undefined = await service.getUserInfo();

    // assert
    expect(response).toBeUndefined();
  });

  it('should log out', () => {
    // act & assert
    configureTestBed();
    service.logout();

    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should not log out again', () => {
    // arrange
    router.url = "/login";

    // act & assert
    configureTestBed();
    service.logout();

    expect(authService.logout).toHaveBeenCalledTimes(0);
    expect(router.navigate).toHaveBeenCalledTimes(0);
  });
});