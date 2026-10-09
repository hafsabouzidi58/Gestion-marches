
export interface Decompte {
  id?: number;
  numDecompte: string;
  dateDecompte?: string;
  montantBrut: number;
  retenueGarantie?: number; // 👈 Ajouter cette ligne
  montantNet?: number;      // 👈 Ajouter cette ligne
  marcheId?: number;
}
export interface ApiError {
  message: string;
}
