import { IProjet } from 'app/entities/projet/projet.model';

export interface IDevis {
  id?: number;
  prixTotal?: number | null;
  prixHT?: number | null;
  prixService?: number | null;
  dureeProjet?: number | null;
  userUuid?: string;
  projet?: IProjet | null;
}

export class Devis implements IDevis {
  constructor(
    public id?: number,
    public prixTotal?: number | null,
    public prixHT?: number | null,
    public prixService?: number | null,
    public dureeProjet?: number | null,
    public userUuid?: string,
    public projet?: IProjet | null
  ) {}
}

export function getDevisIdentifier(devis: IDevis): number | undefined {
  return devis.id;
}
