import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review, ReviewCreateDto, ReviewUpdateDto, ReviewFilterDto, ReviewStatus } from '../models/review.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private readonly API_URL = `${environment.apiUrl}/reviews`;

  constructor(private http: HttpClient) {}

  createReview(review: ReviewCreateDto): Observable<Review> {
    return this.http.post<Review>(this.API_URL, review);
  }

  updateReview(id: number, review: ReviewUpdateDto): Observable<Review> {
    return this.http.put<Review>(`${this.API_URL}/${id}`, review);
  }

  deleteReview(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  publishReview(id: number): Observable<Review> {
    return this.http.put<Review>(`${this.API_URL}/${id}/publish`, {});
  }

  rejectReview(id: number): Observable<Review> {
    return this.http.put<Review>(`${this.API_URL}/${id}/reject`, {});
  }

  getReviews(filters?: ReviewFilterDto): Observable<Review[]> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.company) {
        params = params.set('company', filters.company);
      }
      
      if (filters.accountId) {
        params = params.set('accountId', filters.accountId.toString());
      }
      
      if (filters.notation) {
        params = params.set('notation', filters.notation.toString());
      }
      
      if (filters.status) {
        params = params.set('status', filters.status);
      }
    }
    
    return this.http.get<Review[]>(this.API_URL, { params });
  }

  getPublishedReviews(company?: string): Observable<Review[]> {
    const filters: ReviewFilterDto = {
      company: company,
      status: ReviewStatus.PUBLIE
    };
    return this.getReviews(filters);
  }

  getReviewById(id: number): Observable<Review> {
    return this.http.get<Review>(`${this.API_URL}/${id}`);
  }
  
  getReviewCount(): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/count`);
  }
}
