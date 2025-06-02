import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { AngularMaterialModule } from "../../angular-material-module";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { ErrorCode } from "./model/error-code.model";
import { BaseComponent } from "../../common/components/abstractions/component/base.component";
import { takeUntil } from "rxjs";

/**
 * @author Peter Szrnka
 */
@Component({
    imports: [AngularMaterialModule, TranslatorModule, InformationMessageComponent],
    selector: 'help',
    templateUrl: './help.component.html',
    styleUrls: ['./help.component.scss']
})
export class HelpComponent extends BaseComponent {

    columns: string[] = ['code', 'description'];
    public datasource: ArrayDataSource<ErrorCode>;

    constructor(private readonly activatedRoute: ActivatedRoute) {
        super();
    }

    override ngOnInit(): void {
        this.activatedRoute.data.pipe(takeUntil(this.destroy$)).subscribe((response: any) => this.datasource = new ArrayDataSource<ErrorCode>(response.data.errorCodeList));
    }
}