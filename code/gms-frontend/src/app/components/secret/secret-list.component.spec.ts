import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute, Data, provideRouter, Router } from "@angular/router";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { NavButtonVisibilityPipe } from "../../common/components/pipes/nav-button-visibility.pipe";
import { TranslatorPipe } from "../../common/components/pipes/translator/translator.pipe";
import { SplashComponent } from "../../common/components/splash/splash.component";
import { StatusToggleComponent } from "../../common/components/status-toggle/status-toggle.component";
import { ClipboardService } from "../../common/service/clipboard-service";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { User } from "../user/model/user.model";
import { COPY_SECRET_ID_MESSAGE, SecretListComponent } from "./secret-list.component";
import { SecretService } from "./service/secret-service";
import { vi } from "vitest";
import { routes } from "../../app.config";

/**
 * @author Peter Szrnka
 */
describe('SecretListComponent', () => {
    let component : SecretListComponent;
    const currentUser : User | any = {
        roles :  ["ROLE_USER" ]
    };
    // Injected services
    let service : any;
    let dialogService : any;
    let sharedDataService : any;
    let activatedRoute : any = {};
    let clipboardService : any;
    let router : any;
    // Fixtures
    let fixture : ComponentFixture<SecretListComponent>;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [
                SecretListComponent,
                AngularMaterialModule,
                FormsModule,
                SplashComponent,
                NavBackComponent,
                MomentPipe,
                NavButtonVisibilityPipe,
                StatusToggleComponent,
                TranslatorPipe
            ],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                provideRouter(routes),
                { provide : Router, useValue: router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : SecretService, useValue : service },
                { provide : DialogService, useValue : dialogService },
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide : ClipboardService, useValue : clipboardService}
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(SecretListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        sharedDataService = {
            getUserInfo : vi.fn().mockReturnValue(Promise.resolve(currentUser)),
            refreshCurrentUserInfo: vi.fn()
        };

        dialogService = {
            openConfirmDeleteDialog : vi.fn().mockReturnValue({ afterClosed : vi.fn().mockReturnValue(of({ result: true })) })
        };
        
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
            delete : vi.fn().mockReturnValue(of("OK"))
        };

        clipboardService = {
            copyValue : vi.fn()
        };

        router = {
            navigate : vi.fn(),
            navigateByUrl: vi.fn().mockResolvedValue(true)
        };
    });

    it('Should create component', () => {
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should handle resolver error', () => {
        activatedRoute.data = throwError(() => new Error("Unexpected error!"));
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should return empty table | Invalid user', () => {
        configureTestBed();
        fixture = TestBed.createComponent(SecretListComponent);
        component = fixture.componentInstance;
        vi.spyOn(component.sharedData, 'getUserInfo').mockResolvedValue(undefined);
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should delete an item', () => {
        configureTestBed();

        expect(component).toBeTruthy();

        vi.spyOn(dialogService, 'openConfirmDeleteDialog').mockReturnValue({ afterClosed : vi.fn().mockReturnValue(of({ result: true }) )});

        component.promptDelete(1);

        expect(dialogService.openConfirmDeleteDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should cancel dialog', () => {
        dialogService.openConfirmDeleteDialog = vi.fn().mockReturnValue({ afterClosed : vi.fn().mockReturnValue(of({ result: false })) });
        configureTestBed();

        expect(component).toBeTruthy();
        vi.spyOn(dialogService, 'openConfirmDeleteDialog').mockReturnValue({ afterClosed : vi.fn().mockReturnValue(of({ result: false }) )});

        component.promptDelete(1);

        expect(dialogService.openConfirmDeleteDialog).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should copy secretId', () => {
        configureTestBed();

        // act
        component.copySecretIdValue('value');

        // assert
        expect(clipboardService.copyValue).toHaveBeenCalledWith('value', COPY_SECRET_ID_MESSAGE);
    });
});
