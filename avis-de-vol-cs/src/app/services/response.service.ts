import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Response, ResponseCreateDto } from '../models/review.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ResponseService {
  private readonly API_URL = `${environment.apiUrl}/responses`;

  constructor(private http: HttpClient) {}

  createResponse(response: ResponseCreateDto): Observable<Response> {
    return this.http.post<Response>(this.API_URL, response);
  }

  getResponsesByReview(reviewId: number): Observable<Response[]> {
    return this.http.get<Response[]>(`${this.API_URL}/review/${reviewId}`);
  }

  updateResponse(id: number, response: ResponseCreateDto): Observable<Response> {
    return this.http.put<Response>(`${this.API_URL}/${id}`, response);
  }

  deleteResponse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  getMyResponses(): Observable<Response[]> {
    return this.http.get<Response[]>(`${this.API_URL}/my-responses`);
  }
}
