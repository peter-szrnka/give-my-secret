import { HttpTestingController, HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../../environments/environment";
import { HomeService } from "./home.service";
import { HomeData } from "../model/home-data.model";
import { EMPTY_USER_DATA } from "../../user/model/user-data.model";

describe("HomeService", () => {
    let service : HomeService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [HomeService]
      });
      service = TestBed.inject(HomeService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('Should return data', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/home/";

      // act
      service.getData().subscribe((res : HomeData) => expect(res).toBe(EMPTY_USER_DATA));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(EMPTY_USER_DATA);
      httpMock.verify();
    });
});