import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { Observable } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { SystemStatus } from "../../common/model/system-status.model";
import { SetupService } from "../setup/service/setup-service";

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [ AngularMaterialModule, CommonModule , MomentPipe ],
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