import { Component } from "@angular/core";
import { MatIconModule } from "@angular/material/icon";
import { InformationService } from "../../common/service/info-service";
import { Router } from "@angular/router";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { SharedDataService } from "../../common/service/shared-data-service";

/**
 * @author Peter Szrnka
 */
@Component({
  standalone: true,
  imports: [MatIconModule, MatProgressBarModule],
  selector: 'error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss']
})
export class ErrorComponent {

  treshold = 10;
  counter = 0;
  retry = false;
  skipInterval = false;

  constructor(
    private readonly router: Router, 
    private readonly sharedDataService: SharedDataService,
    private readonly informationService: InformationService) { }

  public ngOnInit(): void {
    this.skipInterval = false;
    setInterval(() => {
      if (this.skipInterval || this.retry === true) {
        return;
      }

      this.counter++;

      if (this.counter <= this.treshold) {
        return;
      }

      this.retry = true;
      this.informationService.healthCheck()
        .then(() => this.handleSuccess())
        .catch(() => this.handleFailure());
    }, 1000);
  }

  private handleSuccess(): void {
    this.retry = false;
    this.skipInterval = true;
    this.sharedDataService.systemReady = undefined;
    this.sharedDataService.check();
    void this.router.navigate(['']);
  }

  private handleFailure(): void {
    this.retry = false;
    this.counter = 0;
  }
}