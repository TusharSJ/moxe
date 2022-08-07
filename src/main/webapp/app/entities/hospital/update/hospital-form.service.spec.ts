import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../hospital.test-samples';

import { HospitalFormService } from './hospital-form.service';

describe('Hospital Form Service', () => {
  let service: HospitalFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HospitalFormService);
  });

  describe('Service methods', () => {
    describe('createHospitalFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createHospitalFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          })
        );
      });

      it('passing IHospital should create a new form with FormGroup', () => {
        const formGroup = service.createHospitalFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          })
        );
      });
    });

    describe('getHospital', () => {
      it('should return NewHospital for default Hospital initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createHospitalFormGroup(sampleWithNewData);

        const hospital = service.getHospital(formGroup) as any;

        expect(hospital).toMatchObject(sampleWithNewData);
      });

      it('should return NewHospital for empty Hospital initial value', () => {
        const formGroup = service.createHospitalFormGroup();

        const hospital = service.getHospital(formGroup) as any;

        expect(hospital).toMatchObject({});
      });

      it('should return IHospital', () => {
        const formGroup = service.createHospitalFormGroup(sampleWithRequiredData);

        const hospital = service.getHospital(formGroup) as any;

        expect(hospital).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IHospital should not enable id FormControl', () => {
        const formGroup = service.createHospitalFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewHospital should disable id FormControl', () => {
        const formGroup = service.createHospitalFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
