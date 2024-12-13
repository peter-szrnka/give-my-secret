import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { catchError, Observable } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { SharedDataService } from "../../common/service/shared-data-service";
import { TranslatorService } from "../../common/service/translator-service";
import { JobDetail } from "./model/job-detail.model";
import { JobDetailService } from "./service/job-detail.service";
import { GmsComponentsModule } from "../../common/components/gms-components-module";

const MANUAL_JOB_EXECUTION_CONFIG = [
    { label: 'job.button.event.maintenance', url : 'event_maintenance' },
    { label: 'job.button.keystore.cleanup', url : 'generated_keystore_cleanup' },
    { label: 'job.button.old.job.log.cleanup', url : 'job_maintenance' },
    { label: 'job.button.message.cleanup', url : 'message_cleanup' },
    { label: 'job.button.secret.rotation', url : 'secret_rotation' },
    { label: 'job.button.user.anonymization', url : 'user_anonymization' },
    { label: 'job.button.user.deletion', url : 'user_deletion' }
];

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [AngularMaterialModule, CommonModule, NavBackComponent, MomentPipe, TranslatorModule, GmsComponentsModule],
    selector: 'job-detail-list',
    templateUrl: './job-detail-list.component.html'
})
export class JobDetailListComponent implements OnInit {

    columns: string[] = ['id', 'name', 'correlationId', 'status', 'duration', 'creationDate', 'message'];
    job_execution_config = MANUAL_JOB_EXECUTION_CONFIG;

    loading = true;
    authMode$: Observable<string> = this.sharedData.authModeSubject$;
    public datasource: MatTableDataSource<JobDetail>;
    public error?: string;

    public tableConfig = {
        count: 0,
        pageIndex: 0,
        pageSize: localStorage.getItem("job_pageSize") ?? 25
    };

    constructor(
        private readonly router: Router,
        private readonly activatedRoute: ActivatedRoute,
        private readonly sharedData: SharedDataService,
        private readonly jobDetailService: JobDetailService, 
        private readonly snackbar : MatSnackBar,
        private readonly translatorService: TranslatorService) {
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

    executeJob(jobUrl: string) {
        this.jobDetailService.startManualExecution(jobUrl).subscribe({
            next: () => this.snackbar.open(this.translatorService.translate('job.manual.execution.success')),
            error: () => this.snackbar.open(this.translatorService.translate('job.manual.execution.error'))
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