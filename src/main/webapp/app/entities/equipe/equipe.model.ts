import { IUser } from 'app/entities/user/user.model';
import { IProjet } from 'app/entities/projet/projet.model';
import { IVote } from 'app/entities/vote/vote.model';

export interface IEquipe {
  id?: number;
  nom?: string | null;
  nombrePersonne?: number | null;
  users?: IUser[] | null;
  projet?: IProjet | null;
  vote?: IVote | null;
}

export class Equipe implements IEquipe {
  constructor(
    public id?: number,
    public nom?: string | null,
    public nombrePersonne?: number | null,
    public users?: IUser[] | null,
    public projet?: IProjet | null,
    public vote?: IVote | null
  ) {}
}

export function getEquipeIdentifier(equipe: IEquipe): number | undefined {
  return equipe.id;
}
