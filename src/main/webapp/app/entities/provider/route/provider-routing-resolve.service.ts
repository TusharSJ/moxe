import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProvider } from '../provider.model';
import { ProviderService } from '../service/provider.service';

@Injectable({ providedIn: 'root' })
export class ProviderRoutingResolveService implements Resolve<IProvider | null> {
  constructor(protected service: ProviderService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProvider | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((provider: HttpResponse<IProvider>) => {
          if (provider.body) {
            return of(provider.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
