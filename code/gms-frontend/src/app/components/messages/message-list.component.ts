import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { Paging } from "../../common/model/paging.model";
import { Message } from "./model/message.model";
import { MessageService } from "./service/message-service";

const FILTER : Paging = {
    direction: "DESC",
    property : "creationDate",
    page : 0,
    size: 10
};

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'message-list',
    templateUrl: './message-list.component.html',
    styleUrls : ['./message-list.component.scss']
})
export class MessageListComponent implements OnInit {
    messageColumns: string[] = ['message', 'creationDate', 'operations'];
    datasource : ArrayDataSource<Message>;
    protected count  = 0;

    constructor(public service : MessageService) {
    }

    ngOnInit(): void {
        this.fetchData();
    }

    markAsRead(id : number) : void {
        this.service.markAsRead(id).subscribe(() => this.fetchData());
    }

    getCount() : number {
        return this.count;
    }

    private fetchData() : void {
        this.service.list(FILTER).subscribe(response => {
            this.datasource = new ArrayDataSource<Message>(response);
            this.count = response.length;
        });
    }
}