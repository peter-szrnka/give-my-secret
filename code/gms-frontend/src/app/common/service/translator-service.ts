import { Injectable } from "@angular/core";

import translations from '../../../assets/i18n/translations.json';

export const TRANSLATION_NOT_FOUND = 'Translation not found';

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class TranslatorService {

    public translate(key: string, arg?: any) : any {
        return this.getResolvedValue(key).replace("$s", arg);
    }

    private getResolvedValue(key: string): string {
        return this.getLanguageMap()[key] ?? TRANSLATION_NOT_FOUND;
    }

    private getLanguageMap(): any {
        // TODO: get language from user settings
        return translations['en'];
    }
}