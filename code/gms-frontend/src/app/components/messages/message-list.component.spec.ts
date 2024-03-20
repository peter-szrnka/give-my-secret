import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/compiler";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { RouterTestingModule } from "@angular/router/testing";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { Message } from "./model/message.model";
import { MessageService } from "./service/message-service";
import { MessageListComponent } from "./message-list.component";
import { HttpErrorResponse } from "@angular/common/http";

/**
 * @author Peter Szrnka
 */
describe('MessageListComponent', () => {
    let component : MessageListComponent;
    let fixture : ComponentFixture<MessageListComponent>;
    // Injected services
    let service : any;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ RouterTestingModule, AngularMaterialModule, NoopAnimationsModule, PipesModule ],
            declarations : [MessageListComponent],
            schemas: [ CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA ],
            providers: [
                { provide : MessageService, useValue : service }
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
                        { id : 2, message : "2", opened : true, creationDate: new Date() } as Message
                    ],
                    totalElements : 2
                });
            }),
            markAsRead : jest.fn().mockReturnValue(of("OK"))
        }
    });

    it('should return count', () => {
        configureTestBed();
        const response : number = component.getCount();

        component.markAsRead(1);

        expect(component).toBeTruthy();
        expect(response).toEqual(2);
        expect(service.list).toHaveBeenCalledTimes(2);
    });

    it('should handle unknown error', () => {
        service.list = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})))
        configureTestBed();

        // assert
        expect(component).toBeTruthy();
        expect(component.error).toEqual('OOPS!');
    });
});