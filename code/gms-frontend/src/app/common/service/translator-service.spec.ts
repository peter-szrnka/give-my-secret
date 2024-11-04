import { TestBed } from "@angular/core/testing";
import { TranslatorService } from "./translator-service";
import { SecureStorageService } from "./secure-storage.service";

/**
 * @author Peter Szrnka
 */
describe("TranslatorService", () => {
    let service: TranslatorService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                TranslatorService,
                { provide: SecureStorageService, useValue: { getItemWithoutEncryption: jest.fn().mockReturnValue("en") } }
            ]
        });
        service = TestBed.inject(TranslatorService);
    });

    it("should be created", () => {
        expect(service).toBeTruthy();
    });

    it("should return the translation", () => {
        expect(service.translate("sidemenu.home")).toBe('Home');
    });

    it("should return the translation with argument", () => {
        expect(service.translate("error.unable.to.reload", "5")).toBe("Unable to load the page, retry in 5 seconds...");
    });

    it("should return the translation missing text", () => {
        expect(service.translate("notExistingKey")).toBe("Translation not found: notExistingKey");
    });
});