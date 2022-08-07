import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { HospitalFormService, HospitalFormGroup } from './hospital-form.service';
import { IHospital } from '../hospital.model';
import { HospitalService } from '../service/hospital.service';

@Component({
  selector: 'jhi-hospital-update',
  templateUrl: './hospital-update.component.html',
})
export class HospitalUpdateComponent implements OnInit {
  isSaving = false;
  hospital: IHospital | null = null;

  editForm: HospitalFormGroup = this.hospitalFormService.createHospitalFormGroup();

  constructor(
    protected hospitalService: HospitalService,
    protected hospitalFormService: HospitalFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ hospital }) => {
      this.hospital = hospital;
      if (hospital) {
        this.updateForm(hospital);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const hospital = this.hospitalFormService.getHospital(this.editForm);
    if (hospital.id !== null) {
      this.subscribeToSaveResponse(this.hospitalService.update(hospital));
    } else {
      this.subscribeToSaveResponse(this.hospitalService.create(hospital));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHospital>>): void {
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

  protected updateForm(hospital: IHospital): void {
    this.hospital = hospital;
    this.hospitalFormService.resetForm(this.editForm, hospital);
  }
}
