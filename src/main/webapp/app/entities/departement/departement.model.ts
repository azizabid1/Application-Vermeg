import { IUser } from 'app/entities/user/user.model';
import { TypeDepartement } from 'app/entities/enumerations/type-departement.model';

export interface IDepartement {
  id?: number;
  nom?: TypeDepartement | null;
  userUuid?: string;
  users?: IUser[] | null;
}

export class Departement implements IDepartement {
  constructor(public id?: number, public nom?: TypeDepartement | null, public userUuid?: string, public users?: IUser[] | null) {}
}

export function getDepartementIdentifier(departement: IDepartement): number | undefined {
  return departement.id;
}
