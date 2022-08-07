import { IHospital } from 'app/entities/hospital/hospital.model';

export interface IProvider {
  id: number;
  name?: string | null;
  specialty?: string | null;
  hospital?: Pick<IHospital, 'id'> | null;
}

export type NewProvider = Omit<IProvider, 'id'> & { id: null };
