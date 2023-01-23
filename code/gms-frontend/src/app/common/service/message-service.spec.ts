import { HttpTestingController, HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../environments/environment";
import { Paging } from "../model/paging.model";
import { Message } from "../model/message.model";
import { MessageService } from "./message-service";

const TEST_MESSAGE : Message = {
  id: 1,
  opened: true,
  message: "Message",
  creationDate: new Date()
};

/**
 * @author Peter Szrnka
 */
describe("MessageService", () => {
    let service : MessageService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [MessageService]
      });
      service = TestBed.inject(MessageService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('Should delete entity', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/message/1";

      // act
      service.delete(1).subscribe((res) => expect(res).toBeCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('DELETE');
      httpMock.verify();
    });

    it('Should list results', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/message/list";
      const mockResponse : Message[] = [TEST_MESSAGE];

      // act
      const request : Paging = {
        direction : "asc",
        page: 0,
        property: "id",
        size: 10
      };
      service.list(request).subscribe((res) => expect(res).toBe(mockResponse));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(request);
      req.flush(request);
      httpMock.verify();
    });

    it('Should return by id', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/message/1";

      // act
      service.getById(1).subscribe((res) => expect(res).toBe(TEST_MESSAGE));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(TEST_MESSAGE);
      httpMock.verify();
    });

    it('Should return count', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/message/count";

      // act
      service.count().subscribe((res) => expect(res).toBe(2));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(2);
      httpMock.verify();
    });

    it('Should return all unread messages', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/message/unread";
      const mockResponse = 3;

      // act
      service.getAllUnread().subscribe(res => expect(res).toBe(mockResponse));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
      httpMock.verify();
    });

    it('Should mark message as read', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/message/mark_as_read";
      const mockResponse = "OK";

      // act
      service.markAsRead(1).subscribe(res => expect(res).toBe(mockResponse));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('PUT');
      req.flush(mockResponse);
      httpMock.verify();
    });
});