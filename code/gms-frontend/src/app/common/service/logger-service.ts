import { Injectable } from "@angular/core";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class LoggerService {

    public error(message: string, ...optionalParams: any): void {
        console.error(`%c ${message}`, 'color: #f00;', optionalParams);
    }

    public info(message: string, ...optionalParams: any): void {
        console.info(`%c ${message}`, 'color: #0099ff;', optionalParams);
    }

    public log(message: string, ...optionalParams: any): void {
        console.log(`%c ${message}`, 'color: #000;', optionalParams);
    }

    public warn(message: string, ...optionalParams: any): void {
        console.warn(`%c ${message}`, 'color: #ff6000;', optionalParams);
    }
}