import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/compiler";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { SharedDataService } from "../../common/service/shared-data-service";
import { MessageListComponent, SelectionStatus } from "./message-list.component";
import { Message } from "./model/message.model";
import { MessageService } from "./service/message-service";

/**
 * @author Peter Szrnka
 */
describe('MessageListComponent', () => {
    let component : MessageListComponent;
    let fixture : ComponentFixture<MessageListComponent>;
    // Injected services
    let sharedDataService : any;
    let service : any;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ AngularMaterialModule, NoopAnimationsModule, PipesModule ],
            declarations : [MessageListComponent],
            schemas: [ CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA ],
            providers: [
                { provide : MessageService, useValue : service },
                { provide : SharedDataService, useValue : sharedDataService }
            ]
        });

        fixture = TestBed.createComponent(MessageListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };


    beforeEach(() => {
        service = {
            list : jest.fn().mockImplementation(() => {
                return of({
                    resultList : [
                        { id : 1, message : "1", opened : false, creationDate: new Date() } as Message,
                        { id : 2, message : "2", opened : false, creationDate: new Date() } as Message
                    ],
                    totalElements : 2
                });
            }),
            markAsRead : jest.fn().mockReturnValue(of("OK")),
            deleteAllByIds: jest.fn().mockReturnValue(of("OK")),
            toggleAllAsRead: jest.fn().mockReturnValue(of("OK")),
        };
        sharedDataService = {
            messageCountUpdateEvent : { emit : jest.fn() }
        };
    });

    it('Should create component', () => {
        configureTestBed();
        component.selectionStatus = SelectionStatus.SOME;
        component.selectAll();
        component.selectAll();
        component.onFetch({ pageSize : 0 });
        expect(component).toBeTruthy();
        expect(sharedDataService.messageCountUpdateEvent.emit).toHaveBeenCalledTimes(2);
    });

    it('should return count', () => {
        configureTestBed();
        const response : number = component.getCount();

        component.markAsRead(0);
        component.selectAll();
        component.selectAll();
        component.selectAll();
        component.update(true, 1);
        component.selectAll();
        component.deleteMessages();

        expect(component).toBeTruthy();
        expect(response).toEqual(2);
        expect(service.list).toHaveBeenCalledTimes(2);
        expect(sharedDataService.messageCountUpdateEvent.emit).toHaveBeenCalledTimes(2);
    });

    it('should delete messages', () => {
        configureTestBed();
        component.deleteMessages();

        component.selectAll();
        component.deleteMessages();

        expect(component).toBeTruthy();
    });

    it('should delete messages', () => {
        configureTestBed();

        component.update(true, 1);
        component.deleteMessages();

        component.selectAll();
        component.deleteMessages();

        expect(component).toBeTruthy();
    });

    it('should mark all as read', () => {
        configureTestBed();
        component.markAllSelectedAsRead();

        component.selectAll();
        component.markAllSelectedAsRead();

        expect(component).toBeTruthy();
    });

    it('should handle unknown error', () => {
        service.list = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})));
        configureTestBed();

        // assert
        expect(component).toBeTruthy();
        expect(component.error).toEqual('OOPS!');
    });
});