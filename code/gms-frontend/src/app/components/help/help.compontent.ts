import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { takeUntil } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { BaseComponent } from "../../common/components/abstractions/component/base.component";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { ErrorCode } from "./model/error-code.model";
import { TranslatorPipe } from "../../common/components/pipes/translator/translator.pipe";

/**
 * @author Peter Szrnka
 */
@Component({
    imports: [AngularMaterialModule, InformationMessageComponent, TranslatorPipe],
    selector: 'help',
    templateUrl: './help.component.html',
    styleUrls: ['./help.component.scss']
})
export class HelpComponent extends BaseComponent implements OnInit {

    columns: string[] = ['code', 'description'];
    public datasource: ArrayDataSource<ErrorCode>;

    constructor(private readonly activatedRoute: ActivatedRoute) {
        super();
    }

    ngOnInit(): void {
        this.activatedRoute.data.pipe(takeUntil(this.destroy$)).subscribe((response: any) => this.datasource = new ArrayDataSource<ErrorCode>(response.data.errorCodeList));
    }
}