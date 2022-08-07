import { IProvider, NewProvider } from './provider.model';

export const sampleWithRequiredData: IProvider = {
  id: 64840,
  name: 'Handcrafted AGP Loan',
};

export const sampleWithPartialData: IProvider = {
  id: 68460,
  name: 'Virginia',
  specialty: 'Ergonomic Nebraska',
};

export const sampleWithFullData: IProvider = {
  id: 41233,
  name: 'Ridge Engineer',
  specialty: 'system Soap',
};

export const sampleWithNewData: NewProvider = {
  name: 'PNG Intelligent',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
