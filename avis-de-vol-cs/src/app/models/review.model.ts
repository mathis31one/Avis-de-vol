export interface Review {
  id?: number;
  flightId: number;
  content: string;
  notation: number; // 1-5 stars
  company?: string;
  flightNumber?: string;
  date?: Date;
  status?: ReviewStatus;
  accountId?: number;
  accountName?: string;
  responses?: Response[]; // Added responses array
}

export enum ReviewStatus {
  TRAITE = 'TRAITE',
  PUBLIE = 'PUBLIE',
  REJETE = 'REJETE'
}

export interface ReviewCreateDto {
  flightId: number;
  content: string;
  notation: number;
}

export interface ReviewUpdateDto {
  content?: string;
  notation?: number;
}

export interface ReviewFilterDto {
  company?: string;
  accountId?: number;
  notation?: number;
  status?: ReviewStatus;
}

// New interfaces for responses
export interface Response {
  id?: number;
  content: string;
  reviewId: number;
  userId: number;
  userName?: string;
  userFirstName?: string; // Add firstname field
}

export interface ResponseCreateDto {
  content: string;
  reviewId: number;
}
