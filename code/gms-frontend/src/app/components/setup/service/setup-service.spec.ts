import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../../environments/environment";
import { IEntitySaveResponseDto } from "../../../common/model/entity-save-response.model";
import { SystemProperty } from "../../system_property/model/system-property.model";
import { UserData } from "../../user/model/user-data.model";
import { SetupService } from "./setup-service";

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
      const expectedUrl = environment.baseUrl + 'info/status';
  
      service.checkReady()
        .subscribe((res) => expect(res).toBe("OK"));
  
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush("OK");

      httpMock.verify();
    });

    it('should return admin user data', () => {
      const expectedUrl = environment.baseUrl + 'setup/current_super_admin';
      const mockResponse : UserData = {
        id : 1, status : "ACTIVE", name : "test", role : 'USER_ADMIN'
      };
  
      service.getAdminUserData()
        .subscribe((res) => expect(res).toBe(mockResponse));
  
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);

      httpMock.verify();
    });

    it('should step back', () => {
      const expectedUrl = environment.baseUrl + 'setup/step_back';
      const mockResponse = "NEED_SETUP";
  
      service.stepBack()
        .subscribe((res) => expect(res).toBe(mockResponse));
  
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);

      httpMock.verify();
    });

    it('should save initial step', () => {
      const expectedUrl = environment.baseUrl + 'setup/initial';
  
      service.saveInitialStep()
        .subscribe((res) => expect(res).toBe("OK"));
  
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
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
        id : 1, status : "ACTIVE", name : "test", role : 'USER_ADMIN'
      };

      service.saveAdminUser(request).subscribe((res) => expect(res).toBe(mockResponse));

      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      req.flush(mockResponse);

      httpMock.verify();
    });

    it('should save system properties', () => {
      const expectedUrl = environment.baseUrl + 'setup/properties';
  
      service.saveSystemProperties([
        { key : "key", value : "value" } as SystemProperty
      ])
        .subscribe((res) => expect(res).toBe("OK"));
  
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      req.flush("OK");

      httpMock.verify();
    });

    it('should save organization data', () => {
      const expectedUrl = environment.baseUrl + 'setup/org_data';
  
      service.saveOrganizationData([
        { key : "key", value : "value" } as SystemProperty
      ])
        .subscribe((res) => expect(res).toBe("OK"));
  
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      req.flush("OK");

      httpMock.verify();
    });

    it('should complete setup', () => {
      const expectedUrl = environment.baseUrl + 'setup/complete';
  
      service.completeSetup()
        .subscribe((res) => expect(res).toBe("OK"));
  
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      req.flush("OK");

      httpMock.verify();
    });
  });