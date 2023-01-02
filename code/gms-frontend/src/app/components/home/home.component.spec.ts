import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";import { ActivatedRoute, Data } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { of } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { HomeComponent } from "./home.component";

describe('HomeComponent', () => {
    let component : HomeComponent;
    let fixture : ComponentFixture<HomeComponent>;
    // Injected services
    let activatedRoute: any;
    let splashScreenStateService : any;

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ RouterTestingModule, AngularMaterialModule, PipesModule ],
            schemas : [CUSTOM_ELEMENTS_SCHEMA],
            declarations : [HomeComponent],
            providers: [
                { provide : ActivatedRoute, useClass : activatedRoute },
                { provide: SplashScreenStateService, useValue: splashScreenStateService }
            ]
        });

        fixture = TestBed.createComponent(HomeComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        activatedRoute = class {
            data : Data = of({
                data : {
                    apiKeyCount: 0,
                    keystoreCount: 0,
                    userCount: 0,
                    announcements: [],
                    latestEvents: [],
                    isAdmin: false
                 }
            })
        };

        splashScreenStateService = {
            stop : jest.fn()
        };
    });

    it('should load component', () => {
        configTestBed();

        // assert
        expect(component).toBeTruthy();
        expect(splashScreenStateService.stop).toHaveBeenCalled();
        expect(component.eventDataSource).toBeDefined();
    });
});