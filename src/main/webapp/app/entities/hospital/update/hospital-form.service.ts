import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IHospital, NewHospital } from '../hospital.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IHospital for edit and NewHospitalFormGroupInput for create.
 */
type HospitalFormGroupInput = IHospital | PartialWithRequiredKeyOf<NewHospital>;

type HospitalFormDefaults = Pick<NewHospital, 'id'>;

type HospitalFormGroupContent = {
  id: FormControl<IHospital['id'] | NewHospital['id']>;
  name: FormControl<IHospital['name']>;
  description: FormControl<IHospital['description']>;
};

export type HospitalFormGroup = FormGroup<HospitalFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class HospitalFormService {
  createHospitalFormGroup(hospital: HospitalFormGroupInput = { id: null }): HospitalFormGroup {
    const hospitalRawValue = {
      ...this.getFormDefaults(),
      ...hospital,
    };
    return new FormGroup<HospitalFormGroupContent>({
      id: new FormControl(
        { value: hospitalRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(hospitalRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(hospitalRawValue.description),
    });
  }

  getHospital(form: HospitalFormGroup): IHospital | NewHospital {
    return form.getRawValue() as IHospital | NewHospital;
  }

  resetForm(form: HospitalFormGroup, hospital: HospitalFormGroupInput): void {
    const hospitalRawValue = { ...this.getFormDefaults(), ...hospital };
    form.reset(
      {
        ...hospitalRawValue,
        id: { value: hospitalRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): HospitalFormDefaults {
    return {
      id: null,
    };
  }
}
