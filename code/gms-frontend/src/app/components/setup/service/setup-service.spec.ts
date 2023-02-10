import { TestBed } from "@angular/core/testing";
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SetupService } from "./setup-service";
import { environment } from "../../../../environments/environment";
import { IEntitySaveResponseDto } from "../../../common/model/entity-save-response.model";
import { UserData } from "../../user/model/user-data.model";

/**
 * @author Peter Szrnka
 */
describe('SetupService', () => {
    let service: SetupService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [SetupService]
      });
      service = TestBed.inject(SetupService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should return OK', () => {
      const expectedUrl = environment.baseUrl + 'system/status';
  
      service.checkReady()
        .subscribe((res) => expect(res).toBe("OK"));
  
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush("OK");

      httpMock.verify();
    });

    it('should save admin user', () => {
      const expectedUrl = environment.baseUrl + 'setup/user';
      const mockResponse : IEntitySaveResponseDto = {
        entityId : 1,
        success: true
      };

      // act
      const request : UserData = {
        id : 1, status : "ACTIVE", name : "test", roles : []
      };

      service.saveAdminUser(request).subscribe((res) => expect(res).toBe(mockResponse));

      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      req.flush(mockResponse);

      httpMock.verify();
    });
  });