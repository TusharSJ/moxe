import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProviderFormService, ProviderFormGroup } from './provider-form.service';
import { IProvider } from '../provider.model';
import { ProviderService } from '../service/provider.service';
import { IHospital } from 'app/entities/hospital/hospital.model';
import { HospitalService } from 'app/entities/hospital/service/hospital.service';

@Component({
  selector: 'jhi-provider-update',
  templateUrl: './provider-update.component.html',
})
export class ProviderUpdateComponent implements OnInit {
  isSaving = false;
  provider: IProvider | null = null;

  hospitalsSharedCollection: IHospital[] = [];

  editForm: ProviderFormGroup = this.providerFormService.createProviderFormGroup();

  constructor(
    protected providerService: ProviderService,
    protected providerFormService: ProviderFormService,
    protected hospitalService: HospitalService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareHospital = (o1: IHospital | null, o2: IHospital | null): boolean => this.hospitalService.compareHospital(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ provider }) => {
      this.provider = provider;
      if (provider) {
        this.updateForm(provider);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const provider = this.providerFormService.getProvider(this.editForm);
    if (provider.id !== null) {
      this.subscribeToSaveResponse(this.providerService.update(provider));
    } else {
      this.subscribeToSaveResponse(this.providerService.create(provider));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProvider>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(provider: IProvider): void {
    this.provider = provider;
    this.providerFormService.resetForm(this.editForm, provider);

    this.hospitalsSharedCollection = this.hospitalService.addHospitalToCollectionIfMissing<IHospital>(
      this.hospitalsSharedCollection,
      provider.hospital
    );
  }

  protected loadRelationshipsOptions(): void {
    this.hospitalService
      .query()
      .pipe(map((res: HttpResponse<IHospital[]>) => res.body ?? []))
      .pipe(
        map((hospitals: IHospital[]) =>
          this.hospitalService.addHospitalToCollectionIfMissing<IHospital>(hospitals, this.provider?.hospital)
        )
      )
      .subscribe((hospitals: IHospital[]) => (this.hospitalsSharedCollection = hospitals));
  }
}
