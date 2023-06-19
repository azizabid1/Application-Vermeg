import { IUser } from 'app/entities/user/user.model';

export interface IPoste {
  id?: number;
  title?: string | null;
  description?: string | null;
  userUuid?: string;
  users?: IUser[] | null;
}

export class Poste implements IPoste {
  constructor(
    public id?: number,
    public title?: string | null,
    public description?: string | null,
    public userUuid?: string,
    public users?: IUser[] | null
  ) {}
}

export function getPosteIdentifier(poste: IPoste): number | undefined {
  return poste.id;
}
