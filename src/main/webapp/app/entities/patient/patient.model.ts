import dayjs from 'dayjs/esm';
import { IProvider } from 'app/entities/provider/provider.model';
import { Sex } from 'app/entities/enumerations/sex.model';

export interface IPatient {
  id: number;
  name?: string | null;
  dateOfBirth?: dayjs.Dayjs | null;
  sex?: Sex | null;
  provider?: Pick<IProvider, 'id'> | null;
}

export type NewPatient = Omit<IPatient, 'id'> & { id: null };
