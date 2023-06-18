import { IEquipe } from 'app/entities/equipe/equipe.model';
import { Rendement } from 'app/entities/enumerations/rendement.model';

export interface IVote {
  id?: number;
  userUuid?: string;
  typeVote?: Rendement;
  equipes?: IEquipe[] | null;
}

export class Vote implements IVote {
  constructor(public id?: number, public userUuid?: string, public typeVote?: Rendement, public equipes?: IEquipe[] | null) {}
}

export function getVoteIdentifier(vote: IVote): number | undefined {
  return vote.id;
}
