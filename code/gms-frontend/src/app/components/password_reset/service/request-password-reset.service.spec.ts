import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../../environments/environment";
import { IEntitySaveResponseDto } from "../../../common/model/entity-save-response.model";
import { ResetPasswordRequestService } from "./request-password-reset.service";

/**
 * @author Peter Szrnka
 */
describe("ResetPasswordRequestService", () => {
    let service : ResetPasswordRequestService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [ResetPasswordRequestService]
      });
      service = TestBed.inject(ResetPasswordRequestService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('Should reset password', () => {
        // arrange
        const expectedUrl = environment.baseUrl + "secure/apikey";
        const mockResponse : IEntitySaveResponseDto = {};

        //act
        service.requestPasswordReset('user').subscribe((res) => expect(res).toBeTruthy());

        // assert
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('POST');
        req.flush(mockResponse);
        httpMock.verify();
    });
});