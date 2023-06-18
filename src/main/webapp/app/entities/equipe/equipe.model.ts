import { IUser } from 'app/entities/user/user.model';
import { IVote } from 'app/entities/vote/vote.model';

export interface IEquipe {
  id?: number;
  nom?: string | null;
  nombrePersonne?: number | null;
  userUuid?: string;
  userId?: IUser | null;
  votes?: IVote[] | null;
}

export class Equipe implements IEquipe {
  constructor(
    public id?: number,
    public nom?: string | null,
    public nombrePersonne?: number | null,
    public userUuid?: string,
    public userId?: IUser | null,
    public votes?: IVote[] | null
  ) {}
}

export function getEquipeIdentifier(equipe: IEquipe): number | undefined {
  return equipe.id;
}
