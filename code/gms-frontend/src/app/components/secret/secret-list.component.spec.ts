import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatDialog } from "@angular/material/dialog";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { User } from "../user/model/user.model";
import { SecretService } from "./service/secret-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { COPY_SECRET_ID_MESSAGE, SecretListComponent } from "./secret-list.component";
import { ClipboardService } from "../../common/service/clipboard-service";

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
    let dialog : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    let clipboardService : any;
    let router : any;
    // Fixtures
    let fixture : ComponentFixture<SecretListComponent>;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [RouterTestingModule, AngularMaterialModule, BrowserAnimationsModule, PipesModule ],
            declarations : [SecretListComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue: router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : SecretService, useValue : service },
                { provide : MatDialog, useValue : dialog },
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide : ClipboardService, useValue : clipboardService}
            ]
        });

        fixture = TestBed.createComponent(SecretListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        sharedDataService = {
            getUserInfo : jest.fn().mockReturnValue(Promise.resolve(currentUser)),
            refreshCurrentUserInfo: jest.fn()
        };

        dialog = {
            open : jest.fn()
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
            })
        };

        service = {
            delete : jest.fn().mockReturnValue(of("OK"))
        };

        clipboardService = {
            copyValue : jest.fn()
        };

        router = {
            navigate : jest.fn()
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
        jest.spyOn(component.sharedData, 'getUserInfo').mockResolvedValue(undefined);
        fixture.detectChanges();

        expect(component).toBeTruthy();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should delete an item', () => {
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.datasource).toBeTruthy();

        const mockDialogRef : any = { afterClosed : jest.fn().mockReturnValue(of(true)) };
        jest.spyOn(component.dialog, 'open').mockReturnValue(mockDialogRef);

        component.promptDelete(1);

        expect(component.dialog.open).toHaveBeenCalled();
        expect(component.sharedData.getUserInfo).toHaveBeenCalled();
    });

    it('Should cancel dialog', () => {
        configureTestBed();

        expect(component).toBeTruthy();
        expect(component.datasource).toBeTruthy();

        const mockDialogRef : any = { afterClosed : jest.fn().mockReturnValue(of(false)) };
        jest.spyOn(component.dialog, 'open').mockReturnValue(mockDialogRef);

        component.promptDelete(1);

        expect(component.dialog.open).toHaveBeenCalled();
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
