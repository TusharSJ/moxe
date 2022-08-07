import dayjs from 'dayjs/esm';

import { Sex } from 'app/entities/enumerations/sex.model';

import { IPatient, NewPatient } from './patient.model';

export const sampleWithRequiredData: IPatient = {
  id: 39843,
  name: 'AGP',
  dateOfBirth: dayjs('2022-08-04T22:56'),
  sex: Sex['FEMALE'],
};

export const sampleWithPartialData: IPatient = {
  id: 61013,
  name: 'withdrawal',
  dateOfBirth: dayjs('2022-08-05T01:06'),
  sex: Sex['OTHER'],
};

export const sampleWithFullData: IPatient = {
  id: 58023,
  name: 'Ameliorated Industrial applications',
  dateOfBirth: dayjs('2022-08-04T13:45'),
  sex: Sex['MALE'],
};

export const sampleWithNewData: NewPatient = {
  name: 'digital Congolese International',
  dateOfBirth: dayjs('2022-08-04T12:37'),
  sex: Sex['FEMALE'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
