import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'hospital',
        data: { pageTitle: 'Hospitals' },
        loadChildren: () => import('./hospital/hospital.module').then(m => m.HospitalModule),
      },
      {
        path: 'provider',
        data: { pageTitle: 'Providers' },
        loadChildren: () => import('./provider/provider.module').then(m => m.ProviderModule),
      },
      {
        path: 'patient',
        data: { pageTitle: 'Patients' },
        loadChildren: () => import('./patient/patient.module').then(m => m.PatientModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
