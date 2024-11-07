/* eslint-disable @typescript-eslint/no-explicit-any */
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable, of } from "rxjs";
import { LoggerService } from "../service/logger-service";

// mocks
import { Environment } from "../../../environments/environment.default";
import { ENV_CONFIG } from "../../app.module";
import * as errorCodes from "../../../assets/mock/error.codes.json";
import * as eventList from "../../../assets/mock/event-list.json";
import * as infoMe from "../../../assets/mock/info.me.json";
import * as mockHomeData from "../../../assets/mock/secure.home.json";
import * as messages from "../../../assets/mock/secure.messages.json";
import * as systemPropertyList from "../../../assets/mock/system-property-list.json";
import * as systemStatus from "../../../assets/mock/system.status.json";
import * as unreadMessages from "../../../assets/mock/unread.messages.json";

const MOCK_MAP : any = {
    "info/status" : systemStatus,
    "info/me" : infoMe,
    "secure/home/" : mockHomeData,
    "secure/message/unread" : unreadMessages,
    "secure/message/list?direction=DESC&property=creationDate&page=0&size=10": messages,
    "info/error_codes" : errorCodes,
    "secure/system_property/list?direction=DESC&property=key&page=0&size=50" : systemPropertyList,
    "secure/event/list?direction=DESC&property=eventDate&page=0&size=25" : eventList
};

/**
 * @author Peter Szrnka
 */
@Injectable()
export class MockInterceptor implements HttpInterceptor {

    constructor(@Inject(ENV_CONFIG) private readonly env: Environment, private readonly logger: LoggerService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const path = this.getPath(req.url);

        if (this.env.enableMock) {
            this.logger.info('Requested path', path);
            return of(new HttpResponse({ status: 200, body: MOCK_MAP[path] ?? {}}));
        }

        return next.handle(req);
    }

    private getPath(url: string): string {
        const stripPrefix = url.substring(url.indexOf('://')+3);
        return stripPrefix.substring(stripPrefix.indexOf('/')+1);
    }
}