import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/compiler";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatSnackBar } from "@angular/material/snack-bar";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data, Router } from "@angular/router";
import { of, ReplaySubject, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { SharedDataService } from "../../common/service/shared-data-service";
import { TranslatorService } from "../../common/service/translator-service";
import { JobDetailListComponent } from "./job-detail-list.component";
import { JobDetail } from "./model/job-detail.model";
import { JobDetailService } from "./service/job-detail.service";

/**
 * @author Peter Szrnka
 */
describe('JobDetailListComponent', () => {
    let component : JobDetailListComponent;
    let fixture : ComponentFixture<JobDetailListComponent>;
    // Injected services
    let router: any;
    let activatedRoute : any = {};
    let jobDetailService: any;
    let snackbar: any;
    let translatorService: any;
    let authModeSubject = new ReplaySubject<string>();

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ AngularMaterialModule, NoopAnimationsModule, MomentPipe, JobDetailListComponent, TranslatorModule ],
            schemas: [ CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA ],
            providers: [
                { provide : Router, useValue : router },
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide : JobDetailService, useValue : jobDetailService },
                { provide : MatSnackBar, useValue : snackbar },
                { provide : SharedDataService, useValue : { authModeSubject$: authModeSubject } },
                { provide : TranslatorService, useValue : translatorService }
            ]
        });

        fixture = TestBed.createComponent(JobDetailListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        authModeSubject.next('db');
    };


    beforeEach(() => {
        router = {
            navigateByUrl: jest.fn().mockResolvedValue(true),
            navigate: jest.fn()
        };
        activatedRoute = class {
            data : Data = of({
                data : {
                    resultList : [
                        { id : 1, name: 'Job1', status: 'COMPLETED', startTime: new Date(), duration: 100 } as JobDetail,
                        { id : 2, name: 'Job2', status: 'COMPLETED', startTime: new Date(), duration: 100 } as JobDetail
                    ],
                    totalElements : 2
                }
            });
            snapshot = {
                queryParams : {}
            }
        };

        jobDetailService = {
            startManualExecution : jest.fn().mockReturnValue(of({}))
        };
        snackbar = {
            open : jest.fn()
        };
        translatorService = {
            translate : jest.fn().mockReturnValue('Job executed successfully')
        };
    });

    it('Should handle resolver error', () => {
        activatedRoute = class {
            data : Data = throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"}));
            snapshot = { queryParams : { "page" : 0 } };
        };
        configureTestBed();

        expect(component).toBeTruthy();
    });

    it('Should load component without page index', () => {
        activatedRoute.snapshot = { queryParams : { "page": 1 } };
        configureTestBed();

        component.onFetch({ pageSize : 25, pageIndex : 1 });
        component.applyFilter({ target: { value: 'a' }});
        expect(component).toBeTruthy();
        expect(router.navigateByUrl).toHaveBeenCalled();
    });

    it('Should return empty table', () => {
        configureTestBed();

        component.onFetch({ pageSize : 25, pageIndex : 1 });
        expect(component).toBeTruthy();
        expect(router.navigateByUrl).toHaveBeenCalled();
    });

    it('executeJob when jobUrl is provided', () => {
        configureTestBed();

        component.executeJob('generated_keystore_cleanup');

        expect(jobDetailService.startManualExecution).toHaveBeenCalled();
    });

    it('executeJob when jobUrl is invalid then handle error', () => {
        jobDetailService.startManualExecution = jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"})));
        configureTestBed();

        component.executeJob('invalid_job_url');

        expect(jobDetailService.startManualExecution).toHaveBeenCalled();
    });
});