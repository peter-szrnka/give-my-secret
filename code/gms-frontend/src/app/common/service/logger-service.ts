import { Injectable } from "@angular/core";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class LoggerService {

    public info(message: string, ...optionalParams: any): void {
        console.info(message, optionalParams);
    }
}