import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { Observable, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { DialogData } from "../../common/components/info-dialog/dialog-data.model";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { IEntitySaveResponseDto } from "../../common/model/entity-save-response.model";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { ApiKeyDetailComponent } from "./apikey-detail.component";
import { ApiKey } from "./model/apikey.model";
import { ApiKeyService } from "./service/apikey-service";

/**
 * @author Peter Szrnka
 */
describe('ApiKeyDetailComponent', () => {
    let component : ApiKeyDetailComponent;
    let fixture : ComponentFixture<ApiKeyDetailComponent>;
    // Injected services
    let router : any;
    let service : any;
    let dialog : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    let splashScreenStateService: any = {};

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [RouterTestingModule, BrowserAnimationsModule, FormsModule, AngularMaterialModule, PipesModule ],
            declarations : [ApiKeyDetailComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue : router},
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : MatDialog, useValue : dialog },
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide : ApiKeyService, useValue : service },
                { provide : SplashScreenStateService, useValue : splashScreenStateService }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(ApiKeyDetailComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        router = {

        };
        sharedDataService = {
            refreshCurrentUserInfo: jest.fn()
        };

        dialog = {
            open : jest.fn().mockReturnValue({ afterClosed : () => of(true) })
        }
        
        activatedRoute = class {
            data : Data = of({
                entity : {
                    id : 1,
                    description : "test",
                    status : "ACTIVE",
                    value : "my-value",
                    creationDate : new Date(),
                    lastUpdated: new Date()
                } as ApiKey
            })
        };

        service = {
            save : jest.fn().mockReturnValue(of({ entityId : 1, success : true }) as Observable<IEntitySaveResponseDto>)
        };

        splashScreenStateService = {
            start: jest.fn(),
            stop: jest.fn()
        };
    });

    it('Should fail at form validation', () => {
        service = {
            save : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : { message: "OOPS!", errorCode: "GMS-018" }, status : 500, statusText: "OOPS!"})))
        };
        dialog = {
            open : jest.fn().mockReturnValue({ afterClosed : () => of(false) })
        };
        configureTestBed();

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: { text: "Error: OOPS!", type: "warning", errorCode: "GMS-018" } as DialogData });
    });

    it('Should fail at form validation 2', () => {
        service = {
            save : jest.fn().mockReturnValue(throwError(() => { return { message: "OOPS!", errorCode: "GMS-018" }; }))
        };
        dialog = {
            open : jest.fn().mockReturnValue({ afterClosed : () => of(true) })
        };
        configureTestBed();

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();

    });

    it('Should save api key', () => {
        configureTestBed();

        // act
        component.generateRandomValue();
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toBeCalledWith(InfoDialog, { data: { text: "API key has been saved!", type: "information" } as DialogData });
    });
});