import { Component, OnInit } from "@angular/core";
import { AngularMaterialModule } from "../../../angular-material-module";
import { SplashScreenStateService } from "../../service/splash-screen-service";
import { NgStyle } from "@angular/common";
import { takeUntil } from "rxjs";
import { BaseComponent } from "../abstractions/component/base.component";

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
export class SplashComponent extends BaseComponent implements OnInit {

  public opacityChange = 1;
  public splashTransition : string;
  public showSplash = false;

  constructor(private readonly splashScreenStateService: SplashScreenStateService) {
    super();
  }

  ngOnInit(): void {
    this.splashScreenStateService.splashScreenSubject$.pipe(takeUntil(this.destroy$)).subscribe((value) => {
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