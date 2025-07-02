export interface Review {
  id?: number;
  flightId: number;
  comment: string;
  notation: number; // 1-5 stars
  company?: string;
  flightNumber?: string;
  date?: Date;
  status?: ReviewStatus;
  accountId?: number;
  accountName?: string;
}

export enum ReviewStatus {
  TRAITE = 'TRAITE',
  PUBLIE = 'PUBLIE',
  REJETE = 'REJETE'
}

export interface ReviewCreateDto {
  flightId: number;
  comment: string;
  notation: number;
}

export interface ReviewUpdateDto {
  comment?: string;
  notation?: number;
}

export interface ReviewFilterDto {
  company?: string;
  accountId?: number;
  notation?: number;
  status?: ReviewStatus;
}
