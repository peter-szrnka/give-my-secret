import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot } from "@angular/router";
import { Observable, catchError, map, of } from "rxjs";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ErrorCodeList } from "../model/error-code-list.model";
import { ErrorCodeService } from "../service/error-code.service";

import errors from '../../../../assets/i18n/error-codes.json';
import { SecureStorageService } from "../../../common/service/secure-storage.service";
import { ErrorCode } from "../model/error-code.model";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class ErrorCodeResolver {

    constructor(
        private readonly storageService: SecureStorageService,
        private readonly splashScreenStateService: SplashScreenStateService,
        private readonly service: ErrorCodeService) {
    }

    public resolve(_snapshot: ActivatedRouteSnapshot): Observable<ErrorCodeList> {
        this.splashScreenStateService.start();

        return this.service.list()
            .pipe(
                catchError(() => of({ errorCodeList: [] }) as Observable<any>),
                map((errorCodeList: ErrorCodeList) => this.mapItems(errorCodeList))
            );
    }

    private mapItems(errorCodes: ErrorCodeList): ErrorCodeList {
        errorCodes.errorCodeList = errorCodes.errorCodeList.map((errorCode: ErrorCode) => {
            return {
                code: errorCode.code,
                description: this.getResolvedError(errorCode.code)
            }
        });

        return errorCodes;
    }

    private getResolvedError(key: string): string {
        return this.getErrorMap()[key] ?? "N/A";
    }

    private getErrorMap(): any {
        return errors[this.getLanguage() as keyof typeof errors];
    }

    private getLanguage(): string {
        return this.storageService.getItemWithoutEncryption('language', 'en');
    }
}