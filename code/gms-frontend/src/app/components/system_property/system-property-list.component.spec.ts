import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { User } from "../user/model/user.model";
import { SystemProperty } from "./model/system-property.model";
import { SystemPropertyService } from "./service/system-property.service";
import { SystemPropertyListComponent } from "./system-property-list.component";
import { InformationService } from "../../common/service/info-service";
import { VmOption } from "../../common/model/common.model";

/**
 * @author Peter Szrnka
 */
describe('SystemPropertyListComponent', () => {
    let component: SystemPropertyListComponent;
    const currentUser: User | any = {
        roles: ["ROLE_ADMIN"]
    };
    // Injected services
    let router: any;
    let service: any;
    let dialogService: any = {};
    let sharedDataService: any;
    let activatedRoute: any = {};
    let splashScreenService: any = {};
    let infoService : any;
    // Fixtures
    let fixture: ComponentFixture<SystemPropertyListComponent>;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [SystemPropertyListComponent, FormsModule, AngularMaterialModule, MomentPipe, TranslatorModule],
            schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide: Router, useValue: router },
                { provide: SharedDataService, useValue: sharedDataService },
                { provide: SystemPropertyService, useValue: service },
                { provide: DialogService, useValue: dialogService },
                { provide: ActivatedRoute, useClass: activatedRoute },
                { provide: SplashScreenStateService, useValue: splashScreenService },
                { provide : InformationService, useValue: infoService }
            ]
        });

        fixture = TestBed.createComponent(SystemPropertyListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        router = {
            navigate: jest.fn().mockReturnValue(of(true))
        };

        sharedDataService = {
            getUserInfo: jest.fn().mockReturnValue(Promise.resolve(currentUser)),
            refreshCurrentUserInfo: jest.fn()
        };

        dialogService = {
            openNewDialog: jest.fn().mockReturnValue({ afterClosed: jest.fn().mockReturnValue(of({ result: true })) }),
            openConfirmDeleteDialog: jest.fn().mockReturnValue({ afterClosed: jest.fn().mockReturnValue(of({ result: true })) }),
        }

        activatedRoute = class {
            data: Data = of({
                data: {
                    resultList: [
                        {
                            key: "ENABLE_GLOBAL_MFA",
                            category: "JWT",
                            value: "HS512",
                            factoryValue: false,
                            lastModified: new Date(),
                            type: "BOOLEAN"
                        },
                        {
                            key: "ENABLE_AUTOMATIC_LOGOUT",
                            category: "GENERAL",
                            factoryValue: true,
                            lastModified: new Date(),
                            type: "BOOLEAN"
                        },
                        {
                            key: "JOB_OLD_EVENT_LIMIT",
                            category: "JOB",
                            value: "1;d",
                            factoryValue: true,
                            lastModified: new Date(),
                            type: "STRING"
                        },
                        {
                            key: "AUTOMATIC_LOGOUT_TIME_IN_MINUTES",
                            category: "GENERAL",
                            value: "15;m",
                            factoryValue: true,
                            lastModified: new Date(),
                            type: "STRING"
                        },
                        {
                            key: "UNKNOWN_PROPERTY",
                            category: "GENERAL",
                            value: "15;m",
                            factoryValue: true,
                            lastModified: new Date(),
                            type: "STRING"
                        }
                    ],
                    totalElements: 3
                }
            });
            snapshot = {
                queryParams: {
                    page: 0
                }
            };
        };

        service = {
            save: jest.fn().mockReturnValue(of("")),
            delete: jest.fn().mockReturnValue(of("OK"))
        };

        splashScreenService = {
            start: jest.fn()
        };

        infoService = {
            getVmOptions: jest.fn().mockReturnValue(of([{ key: 'testKey', value: 'value1' } as VmOption]))
        };
    });

    it('Should handle resolver error', () => {
        activatedRoute = class {
            data: Data = throwError(() => new HttpErrorResponse({ error: new Error("OOPS!"), status: 500, statusText: "OOPS!" }))
        };
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should return empty table | Invalid user', () => {
        // arrange
        sharedDataService = {
            getUserInfo: jest.fn().mockReturnValue(undefined)
        };
        configureTestBed();

        // act &assert
        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
        //expect(informationService.getVmOptions).toHaveBeenCalled();
    });

    it('Should save fail', () => {
        // arrange
        service.save = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error: new Error("OOPS!"), status: 500, statusText: "OOPS!" })));
        configureTestBed();

        expect(component).toBeTruthy();

        // act
        component.save({ key: 'X', value: 'value', type: 'string' } as SystemProperty);

        // assert
        expect(dialogService.openNewDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should filter table', async () => {
        // arrange
        configureTestBed();

        expect(component).toBeTruthy();

        // act
        fixture.detectChanges();
        component.applyFilter({ target: { value: 'JWT_TOKEN' } });
    });

    it('Should save succeed', () => {
        // arrange
        configureTestBed();

        expect(component).toBeTruthy();

        // act
        component.onFetch({ pageSize: 10 });
        component.save({ key: 'X', value: 'value', type: 'string' } as SystemProperty);

        // assert
        expect(dialogService.openNewDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
        expect(router.navigate).toHaveBeenCalled();
    });

    it('Should save succeed with callback', () => {
        // arrange
        configureTestBed();

        expect(component).toBeTruthy();

        // act
        component.onFetch({ pageSize: 10 });
        component.save({ key: 'X', value: 'value', type: 'STRING', callbackMethod: 'checkSystemReady' } as SystemProperty);
        component.save({ key: 'ENABLE_GLOBAL_MFA', value: 'true', type: 'BOOLEAN' } as SystemProperty);
        component.save({ key: 'ENABLE_AUTOMATIC_LOGOUT', value: 'false', type: 'BOOLEAN' } as SystemProperty);

        // assert
        expect(dialogService.openNewDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
        expect(router.navigate).toHaveBeenCalled();
        expect(splashScreenService.start).toHaveBeenCalled();
    });

    it('Should delete an item', () => {
        // arrange
        configureTestBed();
        expect(component).toBeTruthy();
        jest.spyOn(dialogService, 'openConfirmDeleteDialog').mockReturnValue({ afterClosed: jest.fn().mockReturnValue(of({ result: true })) });

        // act
        component.promptDelete({ key: 'X', value: 'value', type: 'string' } as SystemProperty);

        expect(service.delete).toHaveBeenCalled();
        expect(dialogService.openConfirmDeleteDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
        expect(router.navigate).toHaveBeenCalled();
    });

    it('Should cancel dialog after delete', () => {
        // arrange
        dialogService.openConfirmDeleteDialog = jest.fn().mockReturnValue({ afterClosed: jest.fn().mockReturnValue(of({ result: false })) });
        configureTestBed();

        expect(component).toBeTruthy();
        jest.spyOn(dialogService, 'openConfirmDeleteDialog').mockReturnValue({ afterClosed: jest.fn().mockReturnValue(of({ result: false })) });

        // act
        component.promptDelete({ key: 'REFRESH_JWT_ALGORITHM', value: 'value', type: 'string', callbackMethod: 'checkSystemReady' } as SystemProperty);

        // assert
        expect(service.delete).toHaveBeenCalledTimes(0);
        expect(dialogService.openConfirmDeleteDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });
});
