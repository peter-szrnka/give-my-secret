import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, ElementRef, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { Observable, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { IEntitySaveResponseDto } from "../../common/model/entity-save-response.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { KeystoreDetailComponent } from "./keystore-detail.component";
import { KeystoreAlias } from "./model/keystore-alias.model";
import { KeystoreService } from "./service/keystore-service";

/**
 * @author Peter Szrnka
 */
describe('KeystoreDetailComponent', () => {
    let component : KeystoreDetailComponent;
    // Injected services
    let service : any;
    let dialog : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    let router : any;
    // Fixtures
    let fixture : ComponentFixture<KeystoreDetailComponent>;
    let mockElementRef : any;
    let mockAliases : KeystoreAlias[] = [];
    let splashScreenStateService: any = {};

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ FormsModule, BrowserAnimationsModule, AngularMaterialModule, MomentPipe, TranslatorModule ],
            declarations : [KeystoreDetailComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue: router },
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : KeystoreService, useValue : service },
                { provide : DialogService, useValue : dialog },
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide: ElementRef, useValue: mockElementRef },
                { provide : SplashScreenStateService, useValue : splashScreenStateService }
            ]
        });

        fixture = TestBed.createComponent(KeystoreDetailComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        mockElementRef = {
            nativeElement : {}
        };

        sharedDataService = {
            refreshCurrentUserInfo: jest.fn()
        };

        dialog = {
            openNewDialog : jest.fn().mockReturnValue({ afterClosed : jest.fn().mockReturnValue(of(true)) } as any)
        };
        
        activatedRoute = class {
            data : Data = of({
                entity : {
                    id : 1,
                    userId : 1,
                    name : "my-api-key",
                    value : "test",
                    description : "string",
                    status : "ACTIVE",
                    creationDate : new Date(),
                    aliases : mockAliases
                }
            })
        };

        service = {
            save : jest.fn().mockReturnValue(of({ entityId: 1 } as IEntitySaveResponseDto))
        };

        router = {
            navigate : jest.fn()
        };

        splashScreenStateService = {
            start: jest.fn(),
            stop: jest.fn()
        };
    });

    it('Should save keystore', () => {
        dialog = {
            openNewDialog : jest.fn().mockReturnValue({ afterClosed : () : Observable<any> => of(true) })
        };
        
        mockAliases = [
            { id: 1, alias: '...', aliasCredential: '...', operation : 'SAVE' }
        ];
        configureTestBed();
        component.addNewAlias();
        component.changeState({ alias: '...', aliasCredential: '...', operation : 'SAVE' }, 1, 'DELETE');
        component.changeState({ id: 1, alias: '...', aliasCredential: '...', operation : 'SAVE' }, 0, 'DELETE');

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.openNewDialog).toHaveBeenCalledWith({ text: "dialog.save.keystore", type: "information" });
    });

    it('Should save new generated keystore', () => {
        dialog = {
            openNewDialog : jest.fn().mockReturnValue({ afterClosed : () : Observable<any> => of(true) })
        };
        
        mockAliases = [
            { id: 1, alias: '...', aliasCredential: '...', operation : 'SAVE' }
        ];
        configureTestBed();
        component.addNewAlias();
        component.changeState({ alias: '...', aliasCredential: '...', operation : 'SAVE' }, 1, 'DELETE');
        component.changeState({ id: 1, alias: '...', aliasCredential: '...', operation : 'SAVE' }, 0, 'DELETE');
        component.data.generated = true;
        component.toggleCredentialDisplay();
        component.toggleAliasCredentialDisplay({ alias: '...', aliasCredential: '...', operation : 'SAVE', showCredential: true });

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.openNewDialog).toHaveBeenCalledWith({ text: "dialog.save.keystore", type: "information" });
    });

    it('Should fail on save keystore | HTTP error', () => {
        service = {
            save : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})))
        };
        dialog = {
            openNewDialog : jest.fn().mockReturnValue({ afterClosed : () : Observable<any> => of(true) })
        };
        configureTestBed();

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.openNewDialog).toHaveBeenCalledWith({ text: "dialog.save.error", type: "warning", arg: "OOPS!" });
        expect(splashScreenStateService.stop).toHaveBeenCalled();
    });

    it('Should fail on save keystore | Unknown error', () => {
        service = {
            save : jest.fn().mockReturnValue(throwError(() => new Error("OOPS!")))
        };
        dialog = {
            openNewDialog : jest.fn().mockReturnValue({ afterClosed : () : Observable<any> => of(false) })
        };
        configureTestBed();

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.openNewDialog).toHaveBeenCalledWith({ text: "dialog.save.error", type: "warning", arg: "OOPS!" });
    });

    it('should upload file', () => {
        configureTestBed();
        const blob1 : Blob = new Blob(["testing"], { type: "application/pdf", endings : "native" });
        component.fileInput = mockElementRef;

        // act
        component.uploadFileEvt({ target : {
            files : [
                blob1
            ]
        } });

         // assert
         expect(component).toBeTruthy();
         expect(component.fileInput.nativeElement.value).toEqual('');
    });

    it('should not upload file | No files available', () => {
        configureTestBed();

        component.fileInput = mockElementRef;

        // act
        component.uploadFileEvt({ target : { files : [ ]} });

         // assert
         expect(component).toBeTruthy();
         expect(component.fileInput.nativeElement.value).not.toEqual('');
    });

    it.each([
        ["test"], [undefined]
    ])('should download file', (fileName : string | undefined) => {
        configureTestBed();
        component.fileInput = mockElementRef;
        component.data.fileName = fileName;

        // act
        component.downloadKeystore();

         // assert
         expect(component).toBeTruthy();
    });
});
