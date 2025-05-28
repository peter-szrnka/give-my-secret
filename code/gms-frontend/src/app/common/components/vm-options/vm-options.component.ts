import { Component } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { AngularMaterialModule } from "../../../angular-material-module";
import { VmOption } from "../../model/common.model";
import { InformationService } from "../../service/info-service";
import { TranslatorModule } from "../pipes/translator/translator.module";

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
export class VmOptionsComponent {

    datasource: MatTableDataSource<VmOption> = new MatTableDataSource<VmOption>([]);
    columns: string[] = ['key', 'value'];

    vmOptions: VmOption[] = [];

    constructor(private readonly informationService: InformationService) { }

    ngOnInit(): void {
        this.fetchData();
    }

    applyFilter(event: any) {
        const filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
        this.datasource.filter = filterValue.trim().toLowerCase();
    }

    private fetchData() {
        this.informationService.getVmOptions().subscribe(data => this.datasource = new MatTableDataSource<VmOption>(data));
    }
}