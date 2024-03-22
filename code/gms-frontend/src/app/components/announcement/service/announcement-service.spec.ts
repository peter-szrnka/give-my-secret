import { HttpTestingController, HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { AnnouncementService } from "./announcement-service";
import { environment } from "../../../../environments/environment";
import { IEntitySaveResponseDto } from "../../../common/model/entity-save-response.model";
import { Paging } from "../../../common/model/paging.model";
import { Announcement } from "../model/announcement.model";

const TEST_ANNOUNCEMENT : Announcement = {
  id : 1,
  title : "Title",
  announcementDate : new Date(),
  description : "Description"
};

/**
 * @author Peter Szrnka
 */
describe("AnnouncementService", () => {
    let service : AnnouncementService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [AnnouncementService]
      });
      service = TestBed.inject(AnnouncementService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('Should save entity', () => {
        // arrange
        const expectedUrl = environment.baseUrl + "secure/announcement";
        const mockResponse : IEntitySaveResponseDto = {
          entityId : 1,
          success : true
        };

        //act
        service.save(TEST_ANNOUNCEMENT).subscribe((res) => {
          expect(res).toBe(mockResponse);
          expect(res.entityId).toEqual(1);
          expect(res.success).toBeTruthy();
        });

        // assert
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(TEST_ANNOUNCEMENT);
        req.flush(mockResponse);
        httpMock.verify();
    });

    it('Should delete entity', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/announcement/1";

      // act
      service.delete(1).subscribe((res) => expect(res).toBeCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('DELETE');
      httpMock.verify();
    });

    it('Should list results', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/announcement/list";
      const mockResponse : Announcement[] = [TEST_ANNOUNCEMENT];

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
      const expectedUrl = environment.baseUrl + "secure/announcement/1";

      // act
      service.getById(1).subscribe((res) => expect(res).toBe(TEST_ANNOUNCEMENT));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(TEST_ANNOUNCEMENT);
      httpMock.verify();
    });
});