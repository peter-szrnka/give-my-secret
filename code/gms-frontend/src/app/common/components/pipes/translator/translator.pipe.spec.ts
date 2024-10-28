import { TestBed } from "@angular/core/testing";
import { TranslatorService } from "../../../service/translator-service";
import { TranslatorPipe } from "./translator.pipe";

/**
 * @author Peter Szrnka
 */
describe('TranslatorPipe', () => {
    let pipe: TranslatorPipe;
    let translatorService: any;

    beforeEach(() => {
        translatorService = {
            translate: jest.fn().mockReturnValue('Home')
        };
        TestBed.configureTestingModule({
            providers: [
                TranslatorPipe,
                { provide: TranslatorService, useValue: translatorService }
            ]
        });
        pipe = TestBed.inject(TranslatorPipe);
    });

    it("Should translate by key", () => {
        const result = pipe.transform('sidemenu.home');

        // assert
        expect(result).toBe('Home');
    });
});