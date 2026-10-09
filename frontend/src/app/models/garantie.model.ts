export interface Garantie {
  id?: number;
  marcheId?: number;
  numMarche?: string;
  typeGarantie: string;
  montant: number;
  dateConstitution?: string;
  dateLiberation?: string;
  statutLiberation?: string;
}
