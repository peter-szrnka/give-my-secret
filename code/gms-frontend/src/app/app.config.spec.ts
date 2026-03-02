import { Location } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
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

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        DummyComponent,
        RouterModule.forRoot(routes)
      ],
      providers: [
        HttpClientTestingModule,
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
    });

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    fixture = TestBed.createComponent(DummyComponent);
  });

  it('should navigate to home component when path is empty', async() => {
    await router.navigate(['']);

    expect(location.path()).toEqual('');
  });

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
  ])('should navigate to component', async(navInput: string) => {
    await router.navigate([navInput]);
    await fixture.whenStable();
    await new Promise(r => setTimeout(r, 10));

    expect(location.path()).toEqual(navInput);
  });
});
