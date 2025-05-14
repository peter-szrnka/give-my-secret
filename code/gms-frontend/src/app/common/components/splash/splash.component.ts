import { Component } from "@angular/core";
import { AngularMaterialModule } from "../../../angular-material-module";
import { SplashScreenStateService } from "../../service/splash-screen-service";
import { NgStyle } from "@angular/common";

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [
        AngularMaterialModule,
        NgStyle
    ],
    selector: 'splash-screen',
    templateUrl: './splash.component.html',
    styleUrls: ['./splash.component.scss']
})
export class SplashComponent {

  public opacityChange = 1;
  public splashTransition : string;
  public showSplash = false;

  constructor(private readonly splashScreenStateService: SplashScreenStateService) {}

  ngOnInit(): void {
    this.splashScreenStateService.splashScreenSubject$.subscribe((value) => {
      if (value) {
        this.splashTransition = `opacity 0s`;
        this.opacityChange = 1;
      } else {
        this.splashTransition = `opacity 0.25s`;
        this.opacityChange = 0;
      }

      this.showSplash = value;
    });
  }
}