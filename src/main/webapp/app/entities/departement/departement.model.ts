import { IUser } from 'app/admin/user-management/user-management.model';
import { TypeDepartement } from 'app/entities/enumerations/type-departement.model';

export interface IDepartement {
  id?: number;
  nom?: TypeDepartement;
  userUuid?: string;
  userId?: IUser | null;
}

export class Departement implements IDepartement {
  constructor(public id?: number, public nom?: TypeDepartement, public userUuid?: string, public userId?: IUser | null) {}
}

export function getDepartementIdentifier(departement: IDepartement): number | undefined {
  return departement.id;
}
