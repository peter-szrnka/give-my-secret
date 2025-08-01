import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { Observable, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { IEntitySaveResponseDto } from "../../common/model/entity-save-response.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { IprestrictionDetailComponent } from "./ip-restriction-detail.component";
import { IpRestriction } from "./model/ip-restriction.model";
import { IpRestrictionService } from "./service/ip-restriction.service";

/**
 * @author Peter Szrnka
 */
describe('IprestrictionDetailComponent', () => {
    let component : IprestrictionDetailComponent;
    let fixture : ComponentFixture<IprestrictionDetailComponent>;
    // Injected services
    let router : any;
    let service : any;
    let dialog : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    let splashScreenStateService: any = {};

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [IprestrictionDetailComponent, RouterTestingModule, BrowserAnimationsModule, FormsModule, AngularMaterialModule, MomentPipe, TranslatorModule ],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue : router},
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : DialogService, useValue : dialog },
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide : IpRestrictionService, useValue : service },
                { provide : SplashScreenStateService, useValue : splashScreenStateService }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(IprestrictionDetailComponent);
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
            openNewDialog : jest.fn().mockReturnValue({ afterClosed : () => of(true) })
        }
        
        activatedRoute = class {
            data : Data = of({
                entity : {
                    id : 1,
                } as IpRestriction
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
            openNewDialog : jest.fn().mockReturnValue({ afterClosed : () => of(false) })
        };
        configureTestBed();

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.openNewDialog).toHaveBeenCalledWith({"errorCode": "GMS-018", "text": "dialog.save.error", "type": "warning", "arg": "OOPS!"});
    });

    it('Should fail at form validation 2', () => {
        service = {
            save : jest.fn().mockReturnValue(throwError(() => { return { message: "OOPS!", errorCode: "GMS-018" } }))
        };
        dialog = {
            openNewDialog : jest.fn().mockReturnValue({ afterClosed : () => of(true) })
        };
        configureTestBed();

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
    });

    it('Should save IP restriction', () => {
        configureTestBed();

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.openNewDialog).toHaveBeenCalledWith({ text: "dialog.save.ip_restriction", type: "information" });
    });
});