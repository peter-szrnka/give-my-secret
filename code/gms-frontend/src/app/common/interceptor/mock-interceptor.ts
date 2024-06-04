/* eslint-disable @typescript-eslint/no-explicit-any */
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, of } from "rxjs";
import { environment } from "../../../environments/environment";

// mocks
import * as systemStatus from "../../mock/system.status.json";
import * as infoMe from "../../mock/info.me.json";
import * as mockHomeData from "../../mock/secure.home.json";
import * as unreadMessages from "../../mock/unread.messages.json";
import * as messages from "../../mock/secure.messages.json";
import * as errorCodes from "../../mock/error.codes.json";

const MOCK_MAP : any = {
    "system/status" : systemStatus,
    "info/me" : infoMe,
    "secure/home/" : mockHomeData,
    "secure/message/unread" : unreadMessages,
    "secure/message/list?direction=DESC&property=creationDate&page=0&size=10": messages,
    "error_codes" : errorCodes
};


/**
 * @author Peter Szrnka
 */
@Injectable()
export class MockInterceptor implements HttpInterceptor {

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const path = this.getPath(req.url);

        if (environment.enableMock) {
            console.info("Requested path", path);
            return of(new HttpResponse({ status: 200, body: MOCK_MAP[path] ?? {}}));
        }

        return next.handle(req);
    }

    getPath(url: string): string {
        const stripPrefix = url.substring(url.indexOf('://')+3);
        return stripPrefix.substring(stripPrefix.indexOf('/')+1);
    }
}