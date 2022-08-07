import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHospital } from '../hospital.model';
import { HospitalService } from '../service/hospital.service';

@Injectable({ providedIn: 'root' })
export class HospitalRoutingResolveService implements Resolve<IHospital | null> {
  constructor(protected service: HospitalService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IHospital | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((hospital: HttpResponse<IHospital>) => {
          if (hospital.body) {
            return of(hospital.body);
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
