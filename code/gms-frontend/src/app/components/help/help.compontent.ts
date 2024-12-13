import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { AngularMaterialModule } from "../../angular-material-module";
import { ErrorCode } from "./model/error-code.model";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [ AngularMaterialModule, TranslatorModule, GmsComponentsModule ],
    selector: 'help',
    templateUrl: './help.component.html',
    styleUrls: ['./help.component.scss']
})
export class HelpComponent implements OnInit {

    url: string = 'https://peter-szrnka.github.io/give-my-secret';

    columns: string[] = ['code', 'description'];
    public datasource: ArrayDataSource<ErrorCode>;

    constructor(private readonly activatedRoute: ActivatedRoute) { }

    ngOnInit(): void {
        this.activatedRoute.data.subscribe((response: any) => this.datasource = new ArrayDataSource<ErrorCode>(response.data.errorCodeList));
    }
}