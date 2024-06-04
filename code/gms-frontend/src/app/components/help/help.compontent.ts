import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { ErrorCode } from "./model/error-code.model";


/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'help',
    templateUrl: './help.component.html',
    styleUrls: ['./help.component.scss']
})
export class HelpComponent implements OnInit {

    url: string = 'https://peter-szrnka.github.io/give-my-secret';

    columns: string[] = ['code', 'description'];
    public datasource: ArrayDataSource<ErrorCode>;

    constructor(private activatedRoute: ActivatedRoute) { }

    ngOnInit(): void {
        this.activatedRoute.data.subscribe((response: any) => {
            console.info("resp", response);
                this.datasource = new ArrayDataSource<ErrorCode>(response.data as ErrorCode[]);
            });
    }
}