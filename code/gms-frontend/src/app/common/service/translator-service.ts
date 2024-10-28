import { Injectable } from "@angular/core";

import translations from '../../../assets/i18n/translations.json';
import { SecureStorageService } from "./secure-storage.service";

export const TRANSLATION_NOT_FOUND = 'Translation not found: $s';

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class TranslatorService {

    constructor(private readonly storageService: SecureStorageService) {}

    public translate(key: string, arg?: any) : string {
        return this.getResolvedValue(key).replace("$s", arg);
    }

    private getResolvedValue(key: string): string {
        return this.getLanguageMap()[key] ?? TRANSLATION_NOT_FOUND.replace("$s", key);
    }

    private getLanguageMap(): any  {
        return translations[this.getLanguage() as keyof typeof translations];
    }

    private getLanguage(): string {
        return this.storageService.getItemWithoutEncryption('language', 'en');
    }
}