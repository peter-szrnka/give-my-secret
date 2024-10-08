import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { Observable } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { SystemStatus } from "../../common/model/system-status.model";
import { SetupService } from "../setup/service/setup-service";

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [ AngularMaterialModule, CommonModule , PipesModule ],
    selector: 'about',
    templateUrl: './about.component.html',
    styleUrls: ['./about.component.scss']
})
export class AboutComponent implements OnInit {

    systemStatus$: Observable<SystemStatus>;

    constructor(private setupService: SetupService) {}

    ngOnInit(): void {
        this.systemStatus$ = this.setupService.checkReady();
    }
}