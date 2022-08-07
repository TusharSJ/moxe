import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { HospitalComponent } from '../list/hospital.component';
import { HospitalDetailComponent } from '../detail/hospital-detail.component';
import { HospitalUpdateComponent } from '../update/hospital-update.component';
import { HospitalRoutingResolveService } from './hospital-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const hospitalRoute: Routes = [
  {
    path: '',
    component: HospitalComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: HospitalDetailComponent,
    resolve: {
      hospital: HospitalRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: HospitalUpdateComponent,
    resolve: {
      hospital: HospitalRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: HospitalUpdateComponent,
    resolve: {
      hospital: HospitalRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(hospitalRoute)],
  exports: [RouterModule],
})
export class HospitalRoutingModule {}
