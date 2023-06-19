import dayjs from 'dayjs/esm';
import { IEquipe } from 'app/entities/equipe/equipe.model';
import { ITache } from 'app/entities/tache/tache.model';
import { IDevis } from 'app/entities/devis/devis.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface IProjet {
  id?: number;
  userUuid?: string;
  nomProjet?: string | null;
  dateDebut?: dayjs.Dayjs | null;
  dateFin?: dayjs.Dayjs | null;
  technologies?: string | null;
  statusProjet?: Status | null;
  nombreTotal?: number | null;
  nombreRestant?: number | null;
  equipe?: IEquipe | null;
  taches?: ITache[] | null;
  devis?: IDevis | null;
}

export class Projet implements IProjet {
  constructor(
    public id?: number,
    public userUuid?: string,
    public nomProjet?: string | null,
    public dateDebut?: dayjs.Dayjs | null,
    public dateFin?: dayjs.Dayjs | null,
    public technologies?: string | null,
    public statusProjet?: Status | null,
    public nombreTotal?: number | null,
    public nombreRestant?: number | null,
    public equipe?: IEquipe | null,
    public taches?: ITache[] | null,
    public devis?: IDevis | null
  ) {}
}

export function getProjetIdentifier(projet: IProjet): number | undefined {
  return projet.id;
}
