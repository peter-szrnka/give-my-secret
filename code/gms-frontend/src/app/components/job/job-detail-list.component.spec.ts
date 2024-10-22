import { HttpErrorResponse } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/compiler";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { ActivatedRoute, Data } from "@angular/router";
import { of, throwError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { JobDetailListComponent } from "./job-detail-list.component";
import { JobDetail } from "./model/job-detail.model";

/**
 * @author Peter Szrnka
 */
describe('JobDetailListComponent', () => {
    let component : JobDetailListComponent;
    let fixture : ComponentFixture<JobDetailListComponent>;
    // Injected services
    let activatedRoute : any = {};

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ AngularMaterialModule, NoopAnimationsModule, MomentPipe, JobDetailListComponent ],
            schemas: [ CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA ],
            providers: [
                { provide : ActivatedRoute, useClass : activatedRoute }
            ]
        });

        fixture = TestBed.createComponent(JobDetailListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };


    beforeEach(() => {
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
                queryParams : {
                    page : 0
                }
            }
        };
    });

    it('Should handle resolver error', () => {
        activatedRoute = class {
            data : Data = throwError(() => new HttpErrorResponse({ error : new Error("OOPS!"), status : 500, statusText: "OOPS!"}));
            snapshot = { queryParams : { page : 0 } };
        };
        configureTestBed();

        expect(component).toBeTruthy();
    });

    it('Should return empty table', () => {
        configureTestBed();

        expect(component).toBeTruthy();
    });
});