import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProviderFormService } from './provider-form.service';
import { ProviderService } from '../service/provider.service';
import { IProvider } from '../provider.model';
import { IHospital } from 'app/entities/hospital/hospital.model';
import { HospitalService } from 'app/entities/hospital/service/hospital.service';

import { ProviderUpdateComponent } from './provider-update.component';

describe('Provider Management Update Component', () => {
  let comp: ProviderUpdateComponent;
  let fixture: ComponentFixture<ProviderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let providerFormService: ProviderFormService;
  let providerService: ProviderService;
  let hospitalService: HospitalService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProviderUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ProviderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProviderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    providerFormService = TestBed.inject(ProviderFormService);
    providerService = TestBed.inject(ProviderService);
    hospitalService = TestBed.inject(HospitalService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Hospital query and add missing value', () => {
      const provider: IProvider = { id: 456 };
      const hospital: IHospital = { id: 98265 };
      provider.hospital = hospital;

      const hospitalCollection: IHospital[] = [{ id: 39613 }];
      jest.spyOn(hospitalService, 'query').mockReturnValue(of(new HttpResponse({ body: hospitalCollection })));
      const additionalHospitals = [hospital];
      const expectedCollection: IHospital[] = [...additionalHospitals, ...hospitalCollection];
      jest.spyOn(hospitalService, 'addHospitalToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ provider });
      comp.ngOnInit();

      expect(hospitalService.query).toHaveBeenCalled();
      expect(hospitalService.addHospitalToCollectionIfMissing).toHaveBeenCalledWith(
        hospitalCollection,
        ...additionalHospitals.map(expect.objectContaining)
      );
      expect(comp.hospitalsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const provider: IProvider = { id: 456 };
      const hospital: IHospital = { id: 8346 };
      provider.hospital = hospital;

      activatedRoute.data = of({ provider });
      comp.ngOnInit();

      expect(comp.hospitalsSharedCollection).toContain(hospital);
      expect(comp.provider).toEqual(provider);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProvider>>();
      const provider = { id: 123 };
      jest.spyOn(providerFormService, 'getProvider').mockReturnValue(provider);
      jest.spyOn(providerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ provider });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: provider }));
      saveSubject.complete();

      // THEN
      expect(providerFormService.getProvider).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(providerService.update).toHaveBeenCalledWith(expect.objectContaining(provider));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProvider>>();
      const provider = { id: 123 };
      jest.spyOn(providerFormService, 'getProvider').mockReturnValue({ id: null });
      jest.spyOn(providerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ provider: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: provider }));
      saveSubject.complete();

      // THEN
      expect(providerFormService.getProvider).toHaveBeenCalled();
      expect(providerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProvider>>();
      const provider = { id: 123 };
      jest.spyOn(providerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ provider });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(providerService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareHospital', () => {
      it('Should forward to hospitalService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(hospitalService, 'compareHospital');
        comp.compareHospital(entity, entity2);
        expect(hospitalService.compareHospital).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
