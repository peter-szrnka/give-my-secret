import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import { LongValue } from "../../../common/model/long-value.model";
import { Message } from "../model/message.model";
import { MessageList } from "../model/message-list.model";
import { getHeaders } from "../../../common/utils/header-utils";
import { ServiceBase } from "../../../common/components/abstractions/service/service-base";

/**
 * @author Peter Szrnka
 */
@Injectable({providedIn : "root"})
export class MessageService extends ServiceBase<Message, MessageList> {

    constructor(http : HttpClient) {
        super(http, "message");
    }

    public getAllUnread() : Observable<number> {
        return this.http.get<LongValue>(environment.baseUrl + "secure/" + this.scope + '/unread', { withCredentials: true, headers : getHeaders() }).pipe(map(dto => dto.value));
    }

    public markAsRead(id : number) : Observable<string> {
        return this.http.put<string>(environment.baseUrl + "secure/" + this.scope + '/mark_as_read', {"ids":[id]}, { withCredentials: true, headers : getHeaders() });
    }

    public deleteAllByIds(ids : number[]) : Observable<string> {
        return this.http.post<string>(environment.baseUrl + "secure/" + this.scope + '/delete_all_by_ids', {"ids" : ids}, { withCredentials: true, headers : getHeaders() });
    }
}