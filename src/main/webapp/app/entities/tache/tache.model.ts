import { IProjet } from 'app/entities/projet/projet.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface ITache {
  id?: number;
  userUuid?: string;
  titre?: string | null;
  description?: string | null;
  statusTache?: Status | null;
  projets?: IProjet[] | null;
}

export class Tache implements ITache {
  constructor(
    public id?: number,
    public userUuid?: string,
    public titre?: string | null,
    public description?: string | null,
    public statusTache?: Status | null,
    public projets?: IProjet[] | null
  ) {}
}

export function getTacheIdentifier(tache: ITache): number | undefined {
  return tache.id;
}
