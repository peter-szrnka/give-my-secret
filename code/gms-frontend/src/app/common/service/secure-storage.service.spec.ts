import { TestBed } from "@angular/core/testing";
import { SecureStorageService } from "./secure-storage.service";

/**
 * @author Peter Szrnka
 */
describe('SecureStorageService', () => {
    let service: SecureStorageService;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            providers: [
                SecureStorageService
            ]
        });
        service = TestBed.inject(SecureStorageService);
    };

    it('Should save key', () => {
        // arrange
        configureTestBed();

        // act
        service.setItem('userId', 'testKey', 'value');

        // assert
        expect(localStorage.getItem('testKey')).toBeDefined();

        localStorage.clear();
    });

    it('Should get empty result', () => {
        // arrange
        configureTestBed();

        // act
        const response : string = service.getItem('userId', 'testKey');

        // assert
        expect(response).toEqual('');

        localStorage.clear();
    });

    it('Should get key', () => {
        // arrange
        localStorage.setItem('userIdtestKey', 'njjsrXS//GsOKcSRak5kRQ==');
        configureTestBed();

        // act
        const response : string = service.getItem('userId', 'testKey');

        // assert
        expect(response).toEqual('value');

        localStorage.clear();
    });

    it('Should save without encryption', () => {
        // arrange
        configureTestBed();

        // act
        service.setItemWithoutEncryption('testKey', 'value');

        // assert
        expect(localStorage.getItem('testKey')).toEqual('value');

        localStorage.clear();
    });

    it('Should get without encryption', () => {
        // arrange
        localStorage.setItem('testKey', 'value');
        configureTestBed();

        // act
        const response : string = service.getItemWithoutEncryption('testKey', 'defaultValue');

        // assert
        expect(response).toEqual('value');

        localStorage.clear();
    });
});