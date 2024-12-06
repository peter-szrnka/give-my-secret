import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import { Paging } from "../../../common/model/paging.model";
import { getHeaders } from "../../../common/utils/header-utils";
import { JobDetailList } from "../model/job-detail-list.model";


@Injectable({
  providedIn: 'root'
})
export class JobDetailService {
  constructor(private readonly http: HttpClient) { }

  list(paging: Paging): Observable<JobDetailList> {
    return this.http.get<JobDetailList>(environment.baseUrl + `secure/job/list?direction=${paging.direction}&property=${paging.property}&page=${paging.page}&size=${paging.size}`, 
      { withCredentials: true, headers : getHeaders() });
  }

  startManualExecution(jobName: string): Observable<any> {
    return this.http.get(environment.baseUrl + `secure/job_execution/${jobName}`, { withCredentials: true, headers : getHeaders() });
  }
}