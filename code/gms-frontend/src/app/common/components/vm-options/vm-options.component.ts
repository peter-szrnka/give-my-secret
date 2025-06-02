import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { AngularMaterialModule } from "../../../angular-material-module";
import { VmOption } from "../../model/common.model";
import { InformationService } from "../../service/info-service";
import { TranslatorModule } from "../pipes/translator/translator.module";
import { takeUntil } from "rxjs";
import { BaseComponent } from "../abstractions/component/base.component";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'vm-options',
    templateUrl: './vm-options.component.html',
    standalone: true,
    imports: [
        AngularMaterialModule,
        ReactiveFormsModule,
        FormsModule,
        TranslatorModule
    ]
})
export class VmOptionsComponent extends BaseComponent implements OnInit {

    datasource: MatTableDataSource<VmOption> = new MatTableDataSource<VmOption>([]);
    columns: string[] = ['key', 'value'];

    vmOptions: VmOption[] = [];

    constructor(private readonly informationService: InformationService) {
        super();
    }

    ngOnInit(): void {
        this.fetchData();
    }

    applyFilter(event: any) {
        const filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
        this.datasource.filter = filterValue.trim().toLowerCase();
    }

    private fetchData() {
        this.informationService.getVmOptions().pipe(takeUntil(this.destroy$)).subscribe(data => this.datasource = new MatTableDataSource<VmOption>(data));
    }
}