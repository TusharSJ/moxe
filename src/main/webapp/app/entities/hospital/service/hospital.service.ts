import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IHospital, NewHospital } from '../hospital.model';

export type PartialUpdateHospital = Partial<IHospital> & Pick<IHospital, 'id'>;

export type EntityResponseType = HttpResponse<IHospital>;
export type EntityArrayResponseType = HttpResponse<IHospital[]>;

@Injectable({ providedIn: 'root' })
export class HospitalService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/hospitals');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(hospital: NewHospital): Observable<EntityResponseType> {
    return this.http.post<IHospital>(this.resourceUrl, hospital, { observe: 'response' });
  }

  update(hospital: IHospital): Observable<EntityResponseType> {
    return this.http.put<IHospital>(`${this.resourceUrl}/${this.getHospitalIdentifier(hospital)}`, hospital, { observe: 'response' });
  }

  partialUpdate(hospital: PartialUpdateHospital): Observable<EntityResponseType> {
    return this.http.patch<IHospital>(`${this.resourceUrl}/${this.getHospitalIdentifier(hospital)}`, hospital, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IHospital>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IHospital[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getHospitalIdentifier(hospital: Pick<IHospital, 'id'>): number {
    return hospital.id;
  }

  compareHospital(o1: Pick<IHospital, 'id'> | null, o2: Pick<IHospital, 'id'> | null): boolean {
    return o1 && o2 ? this.getHospitalIdentifier(o1) === this.getHospitalIdentifier(o2) : o1 === o2;
  }

  addHospitalToCollectionIfMissing<Type extends Pick<IHospital, 'id'>>(
    hospitalCollection: Type[],
    ...hospitalsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const hospitals: Type[] = hospitalsToCheck.filter(isPresent);
    if (hospitals.length > 0) {
      const hospitalCollectionIdentifiers = hospitalCollection.map(hospitalItem => this.getHospitalIdentifier(hospitalItem)!);
      const hospitalsToAdd = hospitals.filter(hospitalItem => {
        const hospitalIdentifier = this.getHospitalIdentifier(hospitalItem);
        if (hospitalCollectionIdentifiers.includes(hospitalIdentifier)) {
          return false;
        }
        hospitalCollectionIdentifiers.push(hospitalIdentifier);
        return true;
      });
      return [...hospitalsToAdd, ...hospitalCollection];
    }
    return hospitalCollection;
  }
}
