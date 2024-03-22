import { HttpTestingController, HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../../environments/environment";
import { Event } from "../model/event.model";
import { Paging } from "../../../common/model/paging.model";
import { EventService } from "./event-service";

const TEST_EVENT : Event = {
  id : 1,
  username : "user1",
  eventDate: new Date(),
  target : "USER",
  operation : "SAVE"
};

/**
 * @author Peter Szrnka
 */
describe("EventService", () => {
    let service : EventService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [EventService]
      });
      service = TestBed.inject(EventService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('Should delete entity', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/event/1";

      // act
      service.delete(1).subscribe((res) => expect(res).toBeCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('DELETE');
      httpMock.verify();
    });

    it('Should list results', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/event/list";
      const mockResponse : Event[] = [TEST_EVENT];

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

    it('Should list results by user id', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/event/list/2";
      const mockResponse : Event[] = [TEST_EVENT];

      // act
      const request : Paging = {
        direction : "asc",
        page: 0,
        property: "id",
        size: 10
      };
      service.listByUserId(request, 2).subscribe((res) => expect(res).toBe(mockResponse));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(request);
      req.flush(request);
      httpMock.verify();
    });

    it('Should return by id', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/event/1";

      // act
      service.getById(1).subscribe((res) => expect(res).toBe(TEST_EVENT));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(TEST_EVENT);
      httpMock.verify();
    });

    it('should enable entity', () => {
      // act
      service.toggle(1, true).subscribe((res) => expect(res).toBeUndefined());
    });
});