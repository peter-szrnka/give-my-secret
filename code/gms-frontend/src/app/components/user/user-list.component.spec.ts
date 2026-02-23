import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatTableModule } from "@angular/material/table";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { ReplaySubject, of, throwError } from "rxjs";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { User } from "./model/user.model";
import { UserService } from "./service/user-service";
import { UserListComponent } from "./user-list.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { vi } from "vitest";

/**
 * @author Peter Szrnka
 */
describe('UserListComponent', () => {
    let component : UserListComponent;
    const currentUser : User | any = {
        roles :  ["ROLE_USER" ]
    };
    // Injected services
    let service : any;
    let dialogService : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    let router : any;
    let splashScreenService: any;
    let authModeSubject: ReplaySubject<string>;
    // Fixtures
    let fixture : ComponentFixture<UserListComponent>;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ UserListComponent, MatTableModule, MomentPipe, TranslatorModule ],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue: router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : UserService, useValue : service },
                { provide : DialogService, useValue : dialogService },
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide : SplashScreenStateService, useValue: splashScreenService }
            ]
        });
    };

    beforeEach(async () => {
        authModeSubject = new ReplaySubject<string>();
        sharedDataService = {
            getUserInfo : vi.fn().mockReturnValue(Promise.resolve(currentUser)),
            refreshCurrentUserInfo: vi.fn(),
            authModeSubject$: authModeSubject
        };

        splashScreenService = {
            start: vi.fn(),
            stop: vi.fn()
        };

        dialogService = {
            openConfirmDeleteDialog : vi.fn(),
            openNewDialog : vi.fn()
        }
        
        activatedRoute = class {
            data : Data = of({
                data : {
                    resultList : [
                        {
                            id : 1,
                            userId : 1,
                            name : "my-api-key",
                            value : "test",
                            description : "string",
                            status : "ACTIVE",
                            creationDate : new Date()
                        }
                    ],
                    totalElements : 1
                }
            });
            snapshot = {
                queryParams : {
                    page : 0
                }
            }
        };

        service = {
            delete : vi.fn().mockReturnValue(of("OK")),
            manualLdapUserSync: vi.fn().mockReturnValue(of("OK"))
        };

        router = {
            navigate : vi.fn(),
            navigateByUrl : vi.fn().mockReturnValue(of({ then : vi.fn().mockReturnValue(of(true)) }))
        };
    });

    it('Should create component', () => {
        configureTestBed();
        fixture = TestBed.createComponent(UserListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        authModeSubject.next("db");

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should handle resolver error', async () => {
        activatedRoute.data = throwError(() => new Error("Unexpected error!"));
        configureTestBed();
        fixture = TestBed.createComponent(UserListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        authModeSubject.next("db");

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should return empty table | Invalid user', async () => {
        configureTestBed();
        fixture = TestBed.createComponent(UserListComponent);
        component = fixture.componentInstance;
        vi.spyOn(component.sharedData, 'getUserInfo').mockResolvedValue(undefined);
        fixture.detectChanges();
        authModeSubject.next("db");

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should delete an item', async () => {
        configureTestBed();
        fixture = TestBed.createComponent(UserListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        authModeSubject.next("db");

        expect(component).toBeTruthy();

        vi.spyOn(dialogService, 'openConfirmDeleteDialog').mockReturnValue({ afterClosed : vi.fn().mockReturnValue(of(true)) } as any);

        component.promptDelete(1);

        expect(dialogService.openConfirmDeleteDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should cancel dialog', async () => {
        configureTestBed();
        fixture = TestBed.createComponent(UserListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        authModeSubject.next("db");

        expect(component).toBeTruthy();

        vi.spyOn(dialogService, 'openConfirmDeleteDialog').mockReturnValue({ afterClosed : vi.fn().mockReturnValue(of(false)) } as any);
        vi.spyOn(dialogService, 'openNewDialog').mockReturnValue({ afterClosed : vi.fn().mockReturnValue(of(false)) } as any);

        component.promptDelete(1);
        component.manualLdapUserSync();

        expect(dialogService.openConfirmDeleteDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
        expect(service.manualLdapUserSync).toHaveBeenCalledTimes(0);
    });

    it('Should sync LDAP users manually', async () => {
        configureTestBed();
        fixture = TestBed.createComponent(UserListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        authModeSubject.next("ldap");
        expect(component).toBeTruthy();

        vi.spyOn(dialogService, 'openNewDialog').mockReturnValue({ afterClosed : vi.fn().mockReturnValue(of(false)) } as any);

        component.manualLdapUserSync();

        expect(dialogService.openNewDialog).toHaveBeenCalled();
        expect(component.router.navigateByUrl).toHaveBeenCalled();
        expect(splashScreenService.start).toHaveBeenCalled();
        expect(splashScreenService.stop).toHaveBeenCalled();
    });
});
