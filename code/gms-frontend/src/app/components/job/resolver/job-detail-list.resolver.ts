import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot } from "@angular/router";
import { Observable, catchError, of } from "rxjs";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { JobDetailList } from "../model/job-detail-list.model";
import { JobDetailService } from "../service/job-detail.service";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class JobDetailListResolver {

    constructor(private readonly splashScreenStateService: SplashScreenStateService, private readonly  service : JobDetailService) {
    }

    public resolve(snapshot: ActivatedRouteSnapshot): Observable<JobDetailList> {
        this.splashScreenStateService.start();

        return this.service.list({
            direction: "DESC",
            property : "endTime",
            page : snapshot.queryParams['page'] ?? 0,
            size: JSON.parse(localStorage.getItem("job_pageSize") ?? '25')
        }).pipe(catchError((err) =>  of({ totalElements: 0, resultList: [], error: err.error.message }) as Observable<any>));
    }
}