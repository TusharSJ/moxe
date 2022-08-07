import { IHospital, NewHospital } from './hospital.model';

export const sampleWithRequiredData: IHospital = {
  id: 63080,
  name: 'Islands Nebraska',
};

export const sampleWithPartialData: IHospital = {
  id: 64438,
  name: 'auxiliary Legacy',
  description: 'Tokelau evolve',
};

export const sampleWithFullData: IHospital = {
  id: 29673,
  name: 'Factors Account',
  description: 'SMS parsing Innovative',
};

export const sampleWithNewData: NewHospital = {
  name: 'e-services technologies relationships',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
