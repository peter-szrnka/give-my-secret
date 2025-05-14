import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { Observable } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { SystemStatus } from "../../common/model/system-status.model";
import { SetupService } from "../setup/service/setup-service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
@Component({
    imports: [AngularMaterialModule, CommonModule, MomentPipe, TranslatorModule],
    selector: 'about',
    templateUrl: './about.component.html',
    styleUrls: ['./about.component.scss']
})
export class AboutComponent implements OnInit {

    systemStatus$: Observable<SystemStatus>;

    constructor(private readonly setupService: SetupService) {}

    ngOnInit(): void {
        this.systemStatus$ = this.setupService.checkReady();
    }
}