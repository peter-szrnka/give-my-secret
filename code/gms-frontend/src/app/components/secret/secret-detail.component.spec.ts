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
import { IEntitySaveResponseDto } from "../../common/model/entity-save-response.model";
import { IdNamePair } from "../../common/model/id-name-pair.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { ApiKeyService } from "../apikey/service/apikey-service";
import { KeystoreService } from "../keystore/service/keystore-service";
import { SecretDetailComponent, ValidationState } from "./secret-detail.component";
import { SecretService } from "./service/secret-service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { Secret } from "./model/secret.model";

/**
 * @author Peter Szrnka
 */
describe('SecretDetailComponent', () => {
    let component : SecretDetailComponent;
    let fixture : ComponentFixture<SecretDetailComponent>;
    // Injected services
    let router : any;
    let serviceMock : any;
    let dialogService : any = {};
    let sharedDataService : any;
    let activatedRoute : any = {};
    let keystoreService = {};
    let apiKeyService = {};
    let splashScreenStateService: any = {};

    const idPair1 : IdNamePair =  { id: 1, name : "name-1"};
    const idPair2 : IdNamePair =  { id: 2, name : "name-2"};

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [RouterTestingModule, FormsModule, BrowserAnimationsModule, AngularMaterialModule, MomentPipe, TranslatorModule ],
            declarations : [SecretDetailComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : Router, useValue : router},
                { provide : SharedDataService, useValue : sharedDataService },
                { provide : SecretService, useValue : serviceMock },
                { provide : DialogService, useValue : dialogService },
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

        dialogService = {
            openNewDialog : jest.fn().mockReturnValue({ afterClosed : () => of(true) })
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
            save : jest.fn().mockReturnValue(of({ entityId : 1, success : true }) as Observable<IEntitySaveResponseDto>),
            validateLength: jest.fn().mockReturnValue(of({ valid : true })),
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
        expect(dialogService.openNewDialog).toHaveBeenCalledTimes(0);
    });

    it('Should not save secret | HTTP error', () => {
        serviceMock.save = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})));
        configureTestBed();

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialogService.openNewDialog).toHaveBeenCalledWith({"arg": "OOPS!", "text": "dialog.save.error", "type": "warning"});
    });

    it('Should not save secret | unkown error', () => {
        serviceMock.save = jest.fn().mockReturnValue(throwError(() => new Error("OOPS!")));
        configureTestBed();

        jest.spyOn(dialogService, 'openNewDialog').mockReturnValue({ afterClosed : () => of(false) });

        // act
        component.save();

        // assert
        expect(component).toBeTruthy();
        expect(dialogService.openNewDialog).toHaveBeenCalledWith({"arg": "OOPS!", "text": "dialog.save.error", "type": "warning"});
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
        expect(dialogService.openNewDialog).toHaveBeenCalledWith({"text": "dialog.save.secret", "type": "information"});
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
        component.ipRestrictions = [
            { allow: true, ipPattern: '(192.168.0.)[0-9]{1,3}' }
        ];
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
        expect(dialogService.openNewDialog).toHaveBeenCalledWith({"text": "dialog.save.secret", "type": "information"});
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
        expect(dialogService.openNewDialog).toHaveBeenCalledWith({"text": "dialog.save.secret", "type": "information"});
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
        expect(dialogService.openNewDialog).toHaveBeenCalledWith({"text": "dialog.save.error", "type": "warning", arg: "OOPS!"});
    });

    it('Should not rotate secret | unknown error', () => {
        serviceMock.rotate = jest.fn().mockReturnValue(throwError(() => new Error("OOPS!")));
        configureTestBed();

        // act
        component.rotateSecret();

        // assert
        expect(component).toBeTruthy();
        expect(dialogService.openNewDialog).toHaveBeenCalledWith({"text": "dialog.save.error", "type": "warning", arg: "OOPS!"});
    });
    
    it('Should rotate secret', () => {
        configureTestBed();

        // act
        component.rotateSecret();

        // assert
        expect(component).toBeTruthy();
        expect(dialogService.openNewDialog).toHaveBeenCalledWith({"text": "dialog.secret.rotate", "type": "information"});
    });

    it('onKeyUp when validation required immediately then validate', () => {
        // arrange
        configureTestBed();

        component.onKeyUp({ key: 'Enter' } as any, 200);
        component.onKeyUp({ key: 'Enter' } as any, 0);

        // assert
        expect(component).toBeTruthy();
    });

    it.each([
        { value: ''} as Secret,
        { value: '12345678901234', keystoreId: undefined, keystoreAliasId: undefined } as Secret,
        { value: '12345678901234', keystoreId: 1, keystoreAliasId: undefined } as Secret
    ])('validateSecretLength when input is invalid then show warning message', (secretInput: Secret) => {
        // arrange
        configureTestBed();

        // act
        component.data = secretInput;
        component.validateSecretLength();

        // assert
        expect(component).toBeTruthy();
        expect(component.validationState).toEqual(ValidationState.INVALID_INPUT);
    });

    it.each([
        [ true, ValidationState.VALID ],
        [ false, ValidationState.INVALID ]
    ])('validateSecretLength when service returns an answer then show result', (input: boolean, expectedValidationState: ValidationState) => {
        // arrange
        serviceMock.validateLength = jest.fn().mockReturnValue(of({ value: input }));
        configureTestBed();

        // act
        component.validateSecretLength();

        // assert
        expect(component).toBeTruthy();
        expect(component.validationState).toEqual(expectedValidationState);
    });

    it('validateSecretLength when service returns an error then show error message', () => {
        // arrange
        serviceMock.validateLength = jest.fn().mockReturnValue(throwError(() => new Error("OOPS!")));
        configureTestBed();

        // act
        component.validateSecretLength();

        // assert
        expect(component).toBeTruthy();
        expect(component.validationState).toEqual(ValidationState.INVALID);
    });
});