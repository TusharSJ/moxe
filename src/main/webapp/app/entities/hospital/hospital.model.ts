export interface IHospital {
  id: number;
  name?: string | null;
  description?: string | null;
}

export type NewHospital = Omit<IHospital, 'id'> & { id: null };
