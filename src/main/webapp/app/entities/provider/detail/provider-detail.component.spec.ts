import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProviderDetailComponent } from './provider-detail.component';

describe('Provider Management Detail Component', () => {
  let comp: ProviderDetailComponent;
  let fixture: ComponentFixture<ProviderDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProviderDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ provider: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProviderDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProviderDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load provider on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.provider).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
