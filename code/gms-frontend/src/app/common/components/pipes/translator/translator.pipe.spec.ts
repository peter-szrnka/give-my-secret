import { TranslatorService } from "../../../service/translator-service";
import { TranslatorPipe } from "./translator.pipe";

/**
 * @author Peter Szrnka
 */
describe('TranslatorPipe', () => {

    it.each([
        [ 'sidemenu.home', 'Home', undefined ],
        [ 'error.unable.to.reload', 'Unable to load the page, retry in 4 seconds...', '4' ],
    ])('Should translate by key', (key: string, expected: string, arg?: string ) => {
        const pipe: TranslatorPipe = new TranslatorPipe(new TranslatorService());

        const result = pipe.transform(key, arg);

        // assert
        expect(result).toBe(expected);
    });
});