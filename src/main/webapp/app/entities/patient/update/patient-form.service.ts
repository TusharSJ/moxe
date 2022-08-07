import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPatient, NewPatient } from '../patient.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPatient for edit and NewPatientFormGroupInput for create.
 */
type PatientFormGroupInput = IPatient | PartialWithRequiredKeyOf<NewPatient>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPatient | NewPatient> = Omit<T, 'dateOfBirth'> & {
  dateOfBirth?: string | null;
};

type PatientFormRawValue = FormValueOf<IPatient>;

type NewPatientFormRawValue = FormValueOf<NewPatient>;

type PatientFormDefaults = Pick<NewPatient, 'id' | 'dateOfBirth'>;

type PatientFormGroupContent = {
  id: FormControl<PatientFormRawValue['id'] | NewPatient['id']>;
  name: FormControl<PatientFormRawValue['name']>;
  dateOfBirth: FormControl<PatientFormRawValue['dateOfBirth']>;
  sex: FormControl<PatientFormRawValue['sex']>;
  provider: FormControl<PatientFormRawValue['provider']>;
};

export type PatientFormGroup = FormGroup<PatientFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PatientFormService {
  createPatientFormGroup(patient: PatientFormGroupInput = { id: null }): PatientFormGroup {
    const patientRawValue = this.convertPatientToPatientRawValue({
      ...this.getFormDefaults(),
      ...patient,
    });
    return new FormGroup<PatientFormGroupContent>({
      id: new FormControl(
        { value: patientRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(patientRawValue.name, {
        validators: [Validators.required],
      }),
      dateOfBirth: new FormControl(patientRawValue.dateOfBirth, {
        validators: [Validators.required],
      }),
      sex: new FormControl(patientRawValue.sex, {
        validators: [Validators.required],
      }),
      provider: new FormControl(patientRawValue.provider),
    });
  }

  getPatient(form: PatientFormGroup): IPatient | NewPatient {
    return this.convertPatientRawValueToPatient(form.getRawValue() as PatientFormRawValue | NewPatientFormRawValue);
  }

  resetForm(form: PatientFormGroup, patient: PatientFormGroupInput): void {
    const patientRawValue = this.convertPatientToPatientRawValue({ ...this.getFormDefaults(), ...patient });
    form.reset(
      {
        ...patientRawValue,
        id: { value: patientRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PatientFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateOfBirth: currentTime,
    };
  }

  private convertPatientRawValueToPatient(rawPatient: PatientFormRawValue | NewPatientFormRawValue): IPatient | NewPatient {
    return {
      ...rawPatient,
      dateOfBirth: dayjs(rawPatient.dateOfBirth, DATE_TIME_FORMAT),
    };
  }

  private convertPatientToPatientRawValue(
    patient: IPatient | (Partial<NewPatient> & PatientFormDefaults)
  ): PatientFormRawValue | PartialWithRequiredKeyOf<NewPatientFormRawValue> {
    return {
      ...patient,
      dateOfBirth: patient.dateOfBirth ? patient.dateOfBirth.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
