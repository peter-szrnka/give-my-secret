import { TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import { of } from "rxjs";
import { SharedDataService } from "../service/shared-data-service";
import { ROLE_GUARD } from "./role-guard";
import { HttpClientTestingModule } from "@angular/common/http/testing";

/**
 * @author Peter Szrnka
 */
describe('RoleGuard', () => {
    let router : any;
    let sharedData : any;

    const userData = {
        userId : "test",
        userName : "test-user",
        exp : 1,
        roles : ["ROLE_USER"]
    };

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
              {
                provide: SharedDataService,
                useValue: sharedData,
              },
              {
                provide: Router,
                useValue: router,
              }
            ],
          });
    };

    beforeEach(() => {
        router = {
            navigate : jest.fn().mockReturnValue(of(true))
        };
    });

    it('should return true', () => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(userData)
        };

        configureTestBed();
 
        // act
        const response = TestBed.runInInjectionContext(() => ROLE_GUARD({ data : { roles: ["ROLE_USER", "ROLE_VIEWER"] } } as any));

        // assert
        expect(response).toBeTruthy();
        expect(router.navigate).toHaveBeenCalledTimes(0);

    });

    it('should return false', () => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(userData)
        };
        configureTestBed();

        // act
        const response = TestBed.runInInjectionContext(() => ROLE_GUARD({ data : { roles: ["ROLE_ADMIN"] } } as any));

        // assert
        expect(response).toBeFalsy();
        expect(router.navigate).toHaveBeenCalledTimes(1);
        expect(router.navigate).toHaveBeenCalledWith([""]);
    });

    it('should deny empty user', () => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(undefined)
        };
        configureTestBed();
 
        // act
        const response = TestBed.runInInjectionContext(() => ROLE_GUARD({ data : { roles: ["ROLE_USER", "ROLE_VIEWER"] } } as any));

        // assert
        expect(response).toBeFalsy();
    });
});