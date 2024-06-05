import { Injectable } from "@angular/core";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class LoggerService {

    public error(message: string, ...optionalParams: any): void {
        console.error(message, optionalParams);
    }

    public info(message: string, ...optionalParams: any): void {
        console.info(message, optionalParams);
    }

    public log(message: string, ...optionalParams: any): void {
        console.log(message, optionalParams);
    }

    public warn(message: string, ...optionalParams: any): void {
        console.warn(message, optionalParams);
    }
}