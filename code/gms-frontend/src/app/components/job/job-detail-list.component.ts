import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { catchError } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { JobDetail } from "./model/job-detail.model";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [AngularMaterialModule, NavBackComponent, MomentPipe, TranslatorModule],
    selector: 'job-detail-list',
    templateUrl: './job-detail-list.component.html'
})
export class JobDetailListComponent implements OnInit {

    columns: string[] = ['id', 'name', 'correlationId', 'status', 'duration', 'creationDate', 'message'];

    loading = true;
    public datasource: MatTableDataSource<JobDetail>;
    public error?: string;

    public tableConfig = {
        count: 0,
        pageIndex: 0,
        pageSize: localStorage.getItem("job_pageSize") ?? 25
    };

    constructor(
        private readonly router: Router,
        private readonly activatedRoute: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.tableConfig.pageIndex = this.activatedRoute.snapshot.queryParams['page'] ?? 0;
        this.activatedRoute.data
            .pipe(catchError(async () => this.initDefaultDataTable()))
            .subscribe((response: any) => {
                this.tableConfig.count = response.data.totalElements;
                this.datasource = new MatTableDataSource<JobDetail>(response.data.resultList);
                this.error = response.data.error;
                this.loading = false;
            });
    }

    private initDefaultDataTable() {
        this.datasource = new MatTableDataSource<JobDetail>([]);
    }

    applyFilter(event: any) {
        const filterValue = (event.target as HTMLInputElement).value;
        this.datasource.filter = filterValue.trim().toLowerCase();
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