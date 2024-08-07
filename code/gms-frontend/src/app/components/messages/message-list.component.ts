import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { catchError, of } from "rxjs";
import { ButtonConfig } from "../../common/components/nav-back/button-config";
import { Paging } from "../../common/model/paging.model";
import { SharedDataService } from "../../common/service/shared-data-service";
import { MessageList } from "./model/message-list.model";
import { Message } from "./model/message.model";
import { MessageService } from "./service/message-service";

export enum SelectionStatus {
    NONE = 0,
    SOME = 1,
    ALL = 2
}

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'message-list',
    templateUrl: './message-list.component.html',
    styleUrls: ['./message-list.component.scss']
})
export class MessageListComponent implements OnInit {
    messageColumns: string[] = ['message', 'creationDate', 'operations', 'selection'];
    results: Message[];
    datasource: ArrayDataSource<Message>;
    protected count = 0;
    error?: string;
    selectionStatus: SelectionStatus = SelectionStatus.NONE;
    buttonConfig: ButtonConfig[] = [
        {
            label: 'Delete selected messages',
            primary: true,
            enabled: this.selectionStatus !== SelectionStatus.NONE,
            callFunction: () => this.deleteMessages()
        }
    ];

    public tableConfig = {
        count: 0,
        pageIndex: 0,
        pageSize: Number(localStorage.getItem("messages_pageSize") ?? 10)
    };

    constructor(private service: MessageService, private sharedDataService: SharedDataService) {
    }

    public onFetch(event: any) {
        localStorage.setItem("messages_pageSize", event.pageSize);
        this.tableConfig.pageIndex = event.pageIndex;
        this.fetchData();
    }

    ngOnInit(): void {
        this.fetchData();
    }

    markAsRead(id: number): void {
        this.service.markAsRead(id).subscribe(() => this.fetchData());
    }

    getCount(): number {
        return this.count;
    }

    selectAll(): void {
        const currentStatus = (this.selectionStatus === SelectionStatus.NONE) ? true : ((this.selectionStatus === SelectionStatus.ALL) ? false : false);
        this.results.forEach(message => message.selectedToDelete = currentStatus);
        this.calculateSelectionStatus();
    }

    update(completed: boolean, index: number): void {
        this.results[index].selectedToDelete = completed;
        this.calculateSelectionStatus();
    }
    
    private calculateButtonConfig(): void {
        this.buttonConfig = [
            {
                label: 'Delete selected messages',
                primary: true,
                enabled: this.selectionStatus !== SelectionStatus.NONE,
                callFunction: () => this.deleteMessages()
            }
        ];
    }

    private deleteMessages(): void {
        const ids: number[] = this.results.filter(message => message.selectedToDelete === true).map(message => message.id) as number[];
        this.service.deleteAllByIds(ids).subscribe(() => {
            this.selectionStatus = SelectionStatus.NONE;
            this.fetchData();
        });
    }

    private calculateSelectionStatus(): void {
        const count = this.results.filter(message => message.selectedToDelete === true).length;
        if (count === 0) {
            this.selectionStatus = SelectionStatus.NONE;
        } else if (count === this.results.length) {
            this.selectionStatus = SelectionStatus.ALL;
        } else {
            this.selectionStatus = SelectionStatus.SOME;
        }

        this.calculateButtonConfig();
    }

    private fetchData(): void {
        this.service.list({
            direction: "DESC",
            property: "creationDate",
            page: this.tableConfig.pageIndex,
            size: this.tableConfig.pageSize
        } as Paging)
            .pipe(catchError((err) => of({ totalElements: 0, resultList: [], error: err.error.message } as MessageList)))
            .subscribe(response => {
                this.tableConfig.count = response.totalElements;
                this.tableConfig.pageSize = response.resultList.length;
                this.results = response.resultList;
                this.datasource = new ArrayDataSource<Message>(this.results);
                this.count = response.totalElements;
                this.error = response.error;
                this.sharedDataService.messageCountUpdateEvent.emit(this.count);
                this.calculateButtonConfig();
            });
    }
}