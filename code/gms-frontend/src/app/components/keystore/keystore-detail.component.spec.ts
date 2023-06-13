import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, ElementRef, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { Observable, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { DialogData } from "../../common/components/info-dialog/dialog-data.model";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { IEntitySaveResponseDto } from "../../common/model/entity-save-response.model";
import { KeystoreService } from "./service/keystore-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { KeystoreDetailComponent } from "./keystore-detail.component";
import { KeystoreAlias } from "./model/keystore-alias.model";

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
    // Fixtures
    let fixture : ComponentFixture<KeystoreDetailComponent>;
    let mockElementRef : any;
    let mockAliases : KeystoreAlias[] = [];

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [RouterTestingModule, FormsModule, BrowserAnimationsModule, AngularMaterialModule, PipesModule ],
            declarations : [KeystoreDetailComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : KeystoreService, useValue : service },
                { provide : MatDialog, useValue : dialog },
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide: ElementRef, useValue: mockElementRef }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(KeystoreDetailComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        mockElementRef = {
            nativeElement : {}
        };

        sharedDataService = {};

        dialog = {
            open : jest.fn().mockReturnValue({ afterClosed : () : Observable<any> => of() })
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
    });

    it('Should save keystore', () => {
        dialog = {
            open : jest.fn().mockReturnValue({ afterClosed : () : Observable<any> => of(true) })
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
        expect(dialog.open).toBeCalledWith(InfoDialog, { data: { text: "Keystore has been saved!", type: "information" } as DialogData });
    });

    it('Should save new generated keystore', () => {
        dialog = {
            open : jest.fn().mockReturnValue({ afterClosed : () : Observable<any> => of(true) })
        };
        
        mockAliases = [
            { id: 1, alias: '...', aliasCredential: '...', operation : 'SAVE' }
        ];
        configureTestBed();
        component.addNewAlias();
        component.changeState({ alias: '...', aliasCredential: '...', operation : 'SAVE' }, 1, 'DELETE');
        component.changeState({ id: 1, alias: '...', aliasCredential: '...', operation : 'SAVE' }, 0, 'DELETE');
        component.data.generated = true;

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toBeCalledWith(InfoDialog, { data: { text: "Keystore has been saved!", type: "information" } as DialogData });
    });

    it('Should fail on save keystore | HTTP error', () => {
        service = {
            save : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})))
        };
        dialog = {
            open : jest.fn().mockReturnValue({ afterClosed : () : Observable<any> => of(true) })
        };
        configureTestBed();

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toBeCalledWith(InfoDialog, { data: { text: "Error: OOPS!", type: "warning" } as DialogData });
    });

    it('Should fail on save keystore | Unknown error', () => {
        service = {
            save : jest.fn().mockReturnValue(throwError(() => new Error("OOPS!")))
        };
        dialog = {
            open : jest.fn().mockReturnValue({ afterClosed : () : Observable<any> => of(false) })
        };
        configureTestBed();

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toBeCalledWith(InfoDialog, { data: { text: "Error: OOPS!", type: "warning" } as DialogData });
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
