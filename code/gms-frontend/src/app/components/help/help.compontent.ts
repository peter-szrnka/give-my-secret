import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { AngularMaterialModule } from "../../angular-material-module";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { ErrorCode } from "./model/error-code.model";

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [ AngularMaterialModule, TranslatorModule, InformationMessageComponent ],
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