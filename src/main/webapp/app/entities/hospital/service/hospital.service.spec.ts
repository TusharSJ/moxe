import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IHospital } from '../hospital.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../hospital.test-samples';

import { HospitalService } from './hospital.service';

const requireRestSample: IHospital = {
  ...sampleWithRequiredData,
};

describe('Hospital Service', () => {
  let service: HospitalService;
  let httpMock: HttpTestingController;
  let expectedResult: IHospital | IHospital[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(HospitalService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Hospital', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const hospital = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(hospital).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Hospital', () => {
      const hospital = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(hospital).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Hospital', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Hospital', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Hospital', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addHospitalToCollectionIfMissing', () => {
      it('should add a Hospital to an empty array', () => {
        const hospital: IHospital = sampleWithRequiredData;
        expectedResult = service.addHospitalToCollectionIfMissing([], hospital);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(hospital);
      });

      it('should not add a Hospital to an array that contains it', () => {
        const hospital: IHospital = sampleWithRequiredData;
        const hospitalCollection: IHospital[] = [
          {
            ...hospital,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addHospitalToCollectionIfMissing(hospitalCollection, hospital);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Hospital to an array that doesn't contain it", () => {
        const hospital: IHospital = sampleWithRequiredData;
        const hospitalCollection: IHospital[] = [sampleWithPartialData];
        expectedResult = service.addHospitalToCollectionIfMissing(hospitalCollection, hospital);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(hospital);
      });

      it('should add only unique Hospital to an array', () => {
        const hospitalArray: IHospital[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const hospitalCollection: IHospital[] = [sampleWithRequiredData];
        expectedResult = service.addHospitalToCollectionIfMissing(hospitalCollection, ...hospitalArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const hospital: IHospital = sampleWithRequiredData;
        const hospital2: IHospital = sampleWithPartialData;
        expectedResult = service.addHospitalToCollectionIfMissing([], hospital, hospital2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(hospital);
        expect(expectedResult).toContain(hospital2);
      });

      it('should accept null and undefined values', () => {
        const hospital: IHospital = sampleWithRequiredData;
        expectedResult = service.addHospitalToCollectionIfMissing([], null, hospital, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(hospital);
      });

      it('should return initial array if no Hospital is added', () => {
        const hospitalCollection: IHospital[] = [sampleWithRequiredData];
        expectedResult = service.addHospitalToCollectionIfMissing(hospitalCollection, undefined, null);
        expect(expectedResult).toEqual(hospitalCollection);
      });
    });

    describe('compareHospital', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareHospital(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareHospital(entity1, entity2);
        const compareResult2 = service.compareHospital(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareHospital(entity1, entity2);
        const compareResult2 = service.compareHospital(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareHospital(entity1, entity2);
        const compareResult2 = service.compareHospital(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
