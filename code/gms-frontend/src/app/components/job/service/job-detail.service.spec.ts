import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../../environments/environment";
import { Paging } from "../../../common/model/paging.model";
import { JobDetail } from "../model/job-detail.model";
import { JobDetailService } from "./job-detail.service";

/**
 * @author Peter Szrnka
 */
describe("JobDetailService", () => {
    let service : JobDetailService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [JobDetailService]
      });
      service = TestBed.inject(JobDetailService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('Should list results', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/job/list?direction=asc&property=id&page=0&size=10";
      const mockResponse : JobDetail[] = [ { id:1, name:'TestJob', correlationId:'123456', duration:20, status:'COMPLETED', creationDate: new Date(), startTime: new Date() } ];

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
      expect(req.request.method).toBe('GET');
      req.flush(request);
      httpMock.verify();
    });
});