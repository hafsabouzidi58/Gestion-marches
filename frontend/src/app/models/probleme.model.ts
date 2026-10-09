export enum Priorite {
  FAIBLE = 'FAIBLE',
  MOYENNE = 'MOYENNE',
  HAUTE = 'HAUTE',
  URGENTE = 'URGENTE'
}

export enum EtatProbleme {
  EN_ATTENTE = 'EN_ATTENTE',
  EN_COURS_DE_TRAITEMENT = 'EN_COURS_DE_TRAITEMENT',
  RESOLU = 'RESOLU',
  REJETE = 'REJETE'
}

export interface ProblemeCreateDTO {
  titre: string;
  description: string;
  priorite: Priorite;
  marcheId: number;
}

export interface PieceJointeResponseDTO {
  id: number;
  nomFichier?: string;
  nomOriginal: string;
  nomStocke: string;
  cheminFichier: string;
  typeFichier: string;
  tailleFichier: number;
  dateUpload: string;
}
export interface MessageCreateDTO {
  contenu: string;
}
export interface MessageResponseDTO {
  id: number;
  contenu: string;
  auteurId: number;
  auteurNom: string;
  dateEnvoi: string;
  piecesJointes: PieceJointeResponseDTO[];
}

export interface ProblemeResponseDTO {
  id: number;
  titre: string;
  description: string;
  etat: EtatProbleme;
  priorite: Priorite;
  marcheId: number;
  marcheNumero?: string;
  declareParId: number;
  declareParNom: string;
  dateDeclaration: string;
  dateResolution?: string;

  piecesJointesDeclaration: PieceJointeResponseDTO[];
  messages?: MessageResponseDTO[];
}

