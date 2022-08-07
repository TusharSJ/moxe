import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProviderComponent } from './list/provider.component';
import { ProviderDetailComponent } from './detail/provider-detail.component';
import { ProviderUpdateComponent } from './update/provider-update.component';
import { ProviderDeleteDialogComponent } from './delete/provider-delete-dialog.component';
import { ProviderRoutingModule } from './route/provider-routing.module';

@NgModule({
  imports: [SharedModule, ProviderRoutingModule],
  declarations: [ProviderComponent, ProviderDetailComponent, ProviderUpdateComponent, ProviderDeleteDialogComponent],
})
export class ProviderModule {}
