import dayjs from 'dayjs/esm';

export interface IDevis {
  id?: number;
  prixTotal?: number | null;
  prixHT?: number | null;
  prixService?: number | null;
  dureeProjet?: dayjs.Dayjs | null;
}

export class Devis implements IDevis {
  constructor(
    public id?: number,
    public prixTotal?: number | null,
    public prixHT?: number | null,
    public prixService?: number | null,
    public dureeProjet?: dayjs.Dayjs | null
  ) {}
}

export function getDevisIdentifier(devis: IDevis): number | undefined {
  return devis.id;
}
