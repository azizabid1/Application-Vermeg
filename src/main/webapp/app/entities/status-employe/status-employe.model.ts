import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IStatusEmploye {
  id?: number;
  disponibilite?: boolean | null;
  mission?: boolean | null;
  debutConge?: dayjs.Dayjs | null;
  finConge?: dayjs.Dayjs | null;
  userId?: IUser | null;
}

export class StatusEmploye implements IStatusEmploye {
  constructor(
    public id?: number,
    public disponibilite?: boolean | null,
    public mission?: boolean | null,
    public debutConge?: dayjs.Dayjs | null,
    public finConge?: dayjs.Dayjs | null,
    public userId?: IUser | null
  ) {
    this.disponibilite = this.disponibilite ?? false;
    this.mission = this.mission ?? false;
  }
}

export function getStatusEmployeIdentifier(statusEmploye: IStatusEmploye): number | undefined {
  return statusEmploye.id;
}
