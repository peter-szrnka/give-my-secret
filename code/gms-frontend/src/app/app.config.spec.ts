import { Location } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { Router, RouterModule } from '@angular/router';
import { routes } from './app.config';
import { ROLE_GUARD } from './common/interceptor/role-guard';
import { ErrorCodeResolver } from './components/help/resolver/error-code.resolver';
import { of } from 'rxjs';
import { SecretListResolver } from './components/secret/resolver/secret-list.resolver';

@Component({ selector:'dummy-component', template: '' })
class DummyComponent {}

/**
 * @author Peter Szrnka
 */
describe('Routing Config', () => {
  let router: Router;
  let fixture: ComponentFixture<DummyComponent>;
  let location: Location;
  let activatedRouteData: any;

  beforeEach(async() => {
    TestBed.configureTestingModule({
      imports: [
        DummyComponent,
        RouterModule.forRoot(routes),
        HttpClientTestingModule,
      ],
      providers: [
        {
          provide: ROLE_GUARD,
          useValue: {
            canActivate: () => true,
          },
        },
        {
          provide: ErrorCodeResolver,
          useValue: {
            resolve: () => ({ data: 'dummy data' }),
          },
        },
        {
          provide: SecretListResolver,
          useValue: {
            resolve: () => of({}),
          }
        }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    fixture = TestBed.createComponent(DummyComponent);
  });

  it('should navigate to home component when path is empty', fakeAsync(() => {
    router.navigate(['']);
    tick();
    fixture.detectChanges();

    expect(location.path()).toBe('');
  }));

  it.each([
    '/error',
    '/setup',
    '/login',
    '/verify',
    '/password_reset',
    '/about',
    '/help',
    '/settings',
    '/api-testing',
    '/messages',
    '/secret/list'
  ])('should navigate to component', fakeAsync((navInput: string) => {
    fixture.detectChanges();
    router.navigate([navInput]);
    tick();

    expect(location.path()).toBe(navInput);
  }));
});
