import * as translations from '../assets/i18n/translations.json';

describe('Translations', () => {
    it('should create an instance', () => {
        const enKeys = new Set(Object.keys(translations.en));
        const huKeys = new Set(Object.keys(translations.hu));

        const difference = [...enKeys].filter(key => !huKeys.has(key));
      
        // Log the difference
        expect(difference).toEqual([]);
    });
});