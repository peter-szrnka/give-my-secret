import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { Data, ActivatedRoute, Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { Observable, of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { IdNamePair } from "../../common/model/id-name-pair.model";
import { IEntitySaveResponseDto } from "../../common/model/entity-save-response.model";
import { ApiKeyService } from "../apikey/service/apikey-service";
import { KeystoreService } from "../keystore/service/keystore-service";
import { SecretService } from "./service/secret-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SecretDetailComponent } from "./secret-detail.component";
import { DialogData } from "../../common/components/info-dialog/dialog-data.model";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";

/**
 * @author Peter Szrnka
 */
describe('SecretDetailComponent', () => {
    let component : SecretDetailComponent;
    let fixture : ComponentFixture<SecretDetailComponent>;
    // Injected services
    let router : any;
    let serviceMock : any;
    let dialog : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    let keystoreService = {};
    let apiKeyService = {};
    let splashScreenStateService: any = {};

    const idPair1 : IdNamePair =  { id: 1, name : "name-1"};
    const idPair2 : IdNamePair =  { id: 2, name : "name-2"};

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [RouterTestingModule, FormsModule, BrowserAnimationsModule, AngularMaterialModule, PipesModule ],
            declarations : [SecretDetailComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue : router},
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : SecretService, useValue : serviceMock },
                { provide : MatDialog, useValue : dialog },
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide : KeystoreService, useValue : keystoreService },
                { provide : ApiKeyService, useValue : apiKeyService },
                { provide : SplashScreenStateService, useValue : splashScreenStateService }
            ],
        }).compileComponents();

        fixture = TestBed.createComponent(SecretDetailComponent);
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
                    secretId : "my-secret",
                    keystoreId : 1,
                    keystoreAliasId : 1,
                    status : "ACTIVE",
                    value : "my-value",
                    creationDate : new Date(),
                    lastUpdated: new Date(),
                    lastRotated: new Date(),
                    rotationPeriod: "MONTHLY",
                    rotationEnabled : true,
                    returnDecrypted : false,
                    apiKeyRestrictions : [1,2,3],
                    type : 'CREDENTIAL'
                }
            })
        };

        serviceMock = {
            getValue : jest.fn().mockReturnValue(of("value")),
            rotate : jest.fn().mockReturnValue(of("OK")),
            save : jest.fn().mockReturnValue(of({ entityId : 1, success : true }) as Observable<IEntitySaveResponseDto>)
        };

        const mockNames : IdNamePair[] = [ idPair1, idPair2 ];

        keystoreService = {
            getAllKeystoreNames : jest.fn().mockReturnValue(of(mockNames)),
            getAllKeystoreAliases : jest.fn().mockReturnValue(of(['test','test2']))
        };

        apiKeyService = {
            getAllApiKeyNames : jest.fn().mockReturnValue(of(mockNames))
        };

        splashScreenStateService = {
            start: jest.fn(),
            stop: jest.fn()
        };
    });

    it('Should fail at form validation', () => {
        configureTestBed();
        component.data.keystoreAliasId = undefined;

        // act
        try {
            component.save();
        } catch(err : any) {
            expect(err.message).toEqual("Please select a keystore alias!");
        }

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledTimes(0);
    });

    it('Should not save secret | HTTP error', () => {
        serviceMock.save = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})));
        configureTestBed();

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toBeCalledWith(InfoDialog, { data: { text: "Error: OOPS!", type: "warning" } as DialogData });
    });

    it('Should not save secret | unkown error', () => {
        serviceMock.save = jest.fn().mockReturnValue(throwError(() => new Error("OOPS!")));
        dialog = {
            open : jest.fn().mockReturnValue({ afterClosed : () => of(false) })
        };
        configureTestBed();

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toBeCalledWith(InfoDialog, { data: { text: "Error: OOPS!", type: "warning" } as DialogData });
    });

    it('Should save secret', () => {
        configureTestBed();

        // act
        component.add({ value : idPair2.id, chipInput : { clear : jest.fn() } } as any);
        component.add({ value : '3', chipInput : { clear : jest.fn() } } as any);
        component.selected({
            option : {
                value : 1,
                viewValue : 'name-1'
            }
        } as any);
        component.selected({
            option : {
                value : 3,
                viewValue : 'no-name-3'
            }
        } as any);
        component.onKeystoreNameChanged(undefined);
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toBeCalledWith(InfoDialog, { data: { text: "Secret has been saved!", type: "information" } as DialogData });
    });

    it('Should save secret with username password pair', () => {
        activatedRoute = class {
            data : Data = of({
                entity : {
                    id : 1,
                    secretId : "my-secret",
                    keystoreId : 1,
                    keystoreAliasId : 1,
                    status : "ACTIVE",
                    value : "username:user;password:pw12345678",
                    type : 'MULTIPLE_CREDENTIAL',
                    creationDate : new Date(),
                    lastUpdated: new Date(),
                    lastRotated: new Date(),
                    rotationPeriod: "MONTHLY",
                    rotationEnabled : true,
                    returnDecrypted : false,
                    apiKeyRestrictions : [1,2,3]
                }
            })
        };
        configureTestBed();

        // act
        component.add({ value : idPair2.id, chipInput : { clear : jest.fn() } } as any);
        component.add({ value : '3', chipInput : { clear : jest.fn() } } as any);
        component.selected({
            option : {
                value : 1,
                viewValue : 'name-1'
            }
        } as any);
        component.selected({
            option : {
                value : 3,
                viewValue : 'no-name-3'
            }
        } as any);
        component.onKeystoreNameChanged(undefined);
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toBeCalledWith(InfoDialog, { data: { text: "Secret has been saved!", type: "information" } as DialogData });
    });

    it('Should save secret when all api keys allowed', () => {
        configureTestBed();

        // act
        component.remove({ id: 3, name : "test" } as IdNamePair);
        component.remove(idPair2);
        component.remove(idPair1);
        component.formData.allApiKeysAllowed = true;
        component.addNewIpRestriction();
        component.deleteIpRestriction(0);
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: { text: "Secret has been saved!", type: "information" } as DialogData });
    });

    it('Should show secret value for username and password', () => {
        serviceMock.getValue = jest.fn().mockReturnValue(of("username:user;password:pw12345678"));
        activatedRoute = class {
            data : Data = of({
                entity : {
                    id : 1,
                    secretId : "my-secret",
                    keystoreId : 1,
                    keystoreAliasId : 1,
                    status : "ACTIVE",
                    type : 'MULTIPLE_CREDENTIAL',
                    creationDate : new Date(),
                    lastUpdated: new Date(),
                    lastRotated: new Date(),
                    rotationPeriod: "MONTHLY",
                    rotationEnabled : true,
                    returnDecrypted : false,
                    apiKeyRestrictions : [1,2,3]
                }
            })
        };
        configureTestBed();

        // act
        component.showValue();
        component.addNewMultipleCredential();
        component.multipleCredential[2] = { key: "u", value : "p" };
        component.deleteMultipleCredential(2);

        // assert
        expect(component).toBeTruthy();
        expect(component.multipleCredential.length).toEqual(2);
        expect(component.multipleCredential[0].key).toEqual('username');
        expect(component.multipleCredential[0].value).toEqual('user');
        expect(component.multipleCredential[1].key).toEqual('password');
        expect(component.multipleCredential[1].value).toEqual('pw12345678');
        expect(component.data.type).toEqual('MULTIPLE_CREDENTIAL');
        expect(component.data.value).toEqual("username:user;password:pw12345678");
    });

    it('Should show secret value', () => {
        configureTestBed();

        // act
        component.showValue();

        // assert
        expect(component).toBeTruthy();
        expect(component.data.value).toEqual("value");
    });

    it('Should not rotate secret | HTTP error', () => {
        serviceMock.rotate = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})));
        configureTestBed();

        // act
        component.rotateSecret();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: { text: "Unexpected error occurred: OOPS!", type: "warning" } as DialogData });
    });

    it('Should not rotate secret | unknown error', () => {
        serviceMock.rotate = jest.fn().mockReturnValue(throwError(() => new Error("OOPS!")));
        configureTestBed();

        // act
        component.rotateSecret();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: { text: "Unexpected error occurred: OOPS!", type: "warning" } as DialogData });
    });
    
    it('Should rotate secret', () => {
        configureTestBed();

        // act
        component.rotateSecret();

        // assert
        expect(component).toBeTruthy();
        expect(dialog.open).toHaveBeenCalled();
    });
});