import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProviderComponent } from '../list/provider.component';
import { ProviderDetailComponent } from '../detail/provider-detail.component';
import { ProviderUpdateComponent } from '../update/provider-update.component';
import { ProviderRoutingResolveService } from './provider-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const providerRoute: Routes = [
  {
    path: '',
    component: ProviderComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProviderDetailComponent,
    resolve: {
      provider: ProviderRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProviderUpdateComponent,
    resolve: {
      provider: ProviderRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProviderUpdateComponent,
    resolve: {
      provider: ProviderRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(providerRoute)],
  exports: [RouterModule],
})
export class ProviderRoutingModule {}
