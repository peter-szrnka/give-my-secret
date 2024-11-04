import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { catchError, of } from "rxjs";
import { Paging } from "../../common/model/paging.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { MessageList } from "./model/message-list.model";
import { Message } from "./model/message.model";
import { MessageService } from "./service/message-service";

export enum SelectionStatus {
    NONE = 0,
    SOME = 1,
    ALL = 2
};

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'message-list',
    templateUrl: './message-list.component.html',
    styleUrls: ['./message-list.component.scss']
})
export class MessageListComponent implements OnInit {
    messageColumns: string[] = ['message', 'creationDate', 'read_toggle', 'delete', 'selection'];
    results: Message[];
    datasource: ArrayDataSource<Message>;
    protected count = 0;
    unreadCount = 0;
    error?: string;
    selectionStatus: SelectionStatus = SelectionStatus.NONE;
    markAllAsReadEnabled: boolean = false;

    public tableConfig = {
        count: 0,
        pageIndex: 0,
        pageSize: Number(localStorage.getItem("messages_pageSize") ?? 10)
    };

    constructor(
        private readonly service: MessageService,
        private readonly sharedDataService: SharedDataService,
        public dialogService: DialogService
    ) {
    }

    public onFetch(event: any) {
        localStorage.setItem("messages_pageSize", event.pageSize);
        this.tableConfig.pageIndex = event.pageIndex;
        this.fetchData();
    }

    ngOnInit(): void {
        this.fetchData();
    }

    markAsRead(id: number, opened: boolean): void {
        this.service.markAsRead([id], opened).subscribe(() => this.fetchData());
    }

    getCount(): number {
        return this.count;
    }

    selectAll(): void {
        if (this.selectionStatus === SelectionStatus.SOME) {
            this.results.forEach(message => message.selected = false);
        } else {
            const currentStatus = (this.selectionStatus === SelectionStatus.NONE);
            this.results.forEach(message => message.selected = currentStatus);
        }

        this.calculateSelectionStatus();
    }

    update(checked: boolean, index: number): void {
        this.results[index].selected = checked;
        this.calculateSelectionStatus();
    }

    markAllSelectedAsRead(): void {
        if (this.selectionStatus === SelectionStatus.NONE) {
            return;
        }

        const ids: number[] = this.results.filter(message => message.selected === true).map(message => message.id) as number[];
        this.service.markAsRead(ids, true).subscribe(() => this.fetchData());
    }

    deleteMessage(id: number): void {
        this.promptDeleteAll([id], 'dialog.deleteMessage');
    }

    deleteMessages(): void {
        if (this.selectionStatus === SelectionStatus.NONE) {
            return;
        }

        const ids: number[] = this.results.filter(message => message.selected === true).map(message => message.id) as number[];
        this.promptDeleteAll(ids, "dialog.deleteSelectedMessages", ids.length);
    }

    private promptDeleteAll(ids: number[], key: string, arg?: any): void {
        const dialogRef = this.dialogService.openConfirmDeleteDialog({ result: true, key: key, arg: arg });

        dialogRef.afterClosed().subscribe(response => {
            if (response?.result !== true) {
                return;
            }

            this.service.deleteAllByIds(ids).subscribe(() => this.fetchData());
        });
    }

    private calculateSelectionStatus(): void {
        const count = this.results.filter(message => message.selected === true).length;
        if (count === 0) {
            this.selectionStatus = SelectionStatus.NONE;
        } else if (count === this.results.length) {
            this.selectionStatus = SelectionStatus.ALL;
        } else {
            this.selectionStatus = SelectionStatus.SOME;
        }
    }

    private fetchData(): void {
        this.service.list({
            direction: "DESC",
            property: "creationDate",
            page: this.tableConfig.pageIndex,
            size: this.tableConfig.pageSize
        } as Paging)
            .pipe(catchError((err) => of({ totalElements: 0, resultList: [], error: err.error.message } as MessageList)))
            .subscribe(response => this.handleResponse(response));
    }

    private handleResponse(response: MessageList): void {
        this.tableConfig.count = response.totalElements;
        this.tableConfig.pageSize = response.resultList.length;
        this.results = response.resultList;
        this.datasource = new ArrayDataSource<Message>(this.results);
        this.count = response.totalElements;
        this.error = response.error;
        this.sharedDataService.messageCountUpdateEvent.emit(this.count);
        this.markAllAsReadEnabled = this.results.some(message => !message.opened);
        this.selectionStatus = SelectionStatus.NONE;
        this.unreadCount = this.results.filter(message => message.opened === false).length;
    }
}