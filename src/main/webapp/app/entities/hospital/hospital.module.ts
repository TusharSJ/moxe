import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { HospitalComponent } from './list/hospital.component';
import { HospitalDetailComponent } from './detail/hospital-detail.component';
import { HospitalUpdateComponent } from './update/hospital-update.component';
import { HospitalDeleteDialogComponent } from './delete/hospital-delete-dialog.component';
import { HospitalRoutingModule } from './route/hospital-routing.module';

@NgModule({
  imports: [SharedModule, HospitalRoutingModule],
  declarations: [HospitalComponent, HospitalDetailComponent, HospitalUpdateComponent, HospitalDeleteDialogComponent],
})
export class HospitalModule {}
