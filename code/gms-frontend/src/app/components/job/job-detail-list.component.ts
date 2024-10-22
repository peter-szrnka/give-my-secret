import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { catchError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { JobDetail } from "./model/job.-detail.model";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [AngularMaterialModule, NavBackComponent, MomentPipe],
    selector: 'job-detail-list',
    templateUrl: './job-detail-list.component.html'
})
export class JobDetailListComponent implements OnInit {

    columns: string[] = ['id', 'name', 'status', 'creationDate'];

    loading = true;
    public datasource: ArrayDataSource<JobDetail>;
    public error?: string;

    public tableConfig = {
        count: 0,
        pageIndex: 0,
        pageSize: localStorage.getItem("job_pageSize") ?? 25
    };

    constructor(
        private readonly router: Router,
        private readonly activatedRoute: ActivatedRoute) {
            this.tableConfig.pageIndex = this.activatedRoute.snapshot.queryParams['page'] ?? 0;
    }

    ngOnInit(): void {
        this.activatedRoute.data
            .pipe(catchError(async () => this.initDefaultDataTable()))
            .subscribe((response: any) => {
                this.tableConfig.count = response.data.totalElements;
                this.datasource = new ArrayDataSource<JobDetail>(response.data.resultList);
                this.error = response.data.error;
                this.loading = false;
            });
    }

    private initDefaultDataTable() {
        this.datasource = new ArrayDataSource<JobDetail>([]);
    }

    public onFetch(event: any) {
        localStorage.setItem("job_pageSize", event.pageSize);
        this.tableConfig.pageIndex = event.pageIndex;
        this.reloadPage();
    }

    reloadPage(): void {
        this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
          this.router.navigate(["/job/list"], { queryParams: { "page": this.tableConfig.pageIndex } });
        });
      }
}