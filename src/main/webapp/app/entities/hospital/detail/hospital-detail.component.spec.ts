import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { HospitalDetailComponent } from './hospital-detail.component';

describe('Hospital Management Detail Component', () => {
  let comp: HospitalDetailComponent;
  let fixture: ComponentFixture<HospitalDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HospitalDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ hospital: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(HospitalDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(HospitalDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load hospital on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.hospital).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
