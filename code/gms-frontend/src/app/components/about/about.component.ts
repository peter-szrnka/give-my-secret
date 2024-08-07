import { Component, OnDestroy, OnInit } from "@angular/core";
import { Subscription } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { SystemStatus } from "../../common/model/system-status.model";
import { SetupService } from "../setup/service/setup-service";

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [ AngularMaterialModule, PipesModule ],
    selector: 'about',
    templateUrl: './about.component.html',
    styleUrls: ['./about.component.scss']
})
export class AboutComponent implements OnInit, OnDestroy {

    systemStatus?: SystemStatus;
    subscription: Subscription;

    constructor(private setupService: SetupService) {}

    ngOnInit(): void {
        this.subscription = this.setupService.checkReady().subscribe(systemStatus => this.systemStatus = systemStatus);
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }
}