import { Component } from '@angular/core';
import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { routes } from './app.config';

@Component({ template: '' })
class DummyComponent {}

xdescribe('Routing Config', () => {
  let router: Router;
  let fixture: ComponentFixture<DummyComponent>;
  let location: Location;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        DummyComponent,
        RouterTestingModule.withRoutes(routes),
        HttpClientTestingModule,
      ],
    });

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    fixture = TestBed.createComponent(DummyComponent);
    router.initialNavigation();
  });

  it('should navigate to home component when path is empty', fakeAsync(() => {
    router.navigate(['']);
    tick(); // simulate async time passing
    fixture.detectChanges();

    expect(location.path()).toBe('/');
  }));

  it('should navigate to secret list component', fakeAsync(() => {
    router.navigate(['/secret/list']);
    tick();
    fixture.detectChanges();

    expect(location.path()).toBe('/secret/list');
  }));
});
