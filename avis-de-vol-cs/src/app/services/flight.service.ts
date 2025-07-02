import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Flight, FlightSearchCriteria, DateRange } from '../models/flight.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FlightService {
  private readonly API_URL = `${environment.apiUrl}/flights`;

  constructor(private http: HttpClient) {}

  getAllFlights(): Observable<Flight[]> {
    return this.http.get<Flight[]>(this.API_URL);
  }

  searchFlights(criteria: FlightSearchCriteria): Observable<Flight[]> {
    let params = new HttpParams();
    
    if (criteria.company && criteria.company.trim()) {
      params = params.set('company', criteria.company.trim());
    }
    
    if (criteria.startDate) {
      params = params.set('startDate', this.formatDate(criteria.startDate));
    }
    
    if (criteria.endDate) {
      params = params.set('endDate', this.formatDate(criteria.endDate));
    }
    
    return this.http.get<Flight[]>(this.API_URL, { params });
  }

  getFlightById(id: number): Observable<Flight> {
    return this.http.get<Flight>(`${this.API_URL}/${id}`);
  }

  createFlight(flight: Flight): Observable<Flight> {
    return this.http.post<Flight>(this.API_URL, flight);
  }

  updateFlight(id: number, flight: Flight): Observable<Flight> {
    return this.http.put<Flight>(`${this.API_URL}/${id}`, flight);
  }

  deleteFlight(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  existsByFlightNumber(flightNumber: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.API_URL}/exists/${flightNumber}`);
  }

  getFlightsByCompany(company: string): Observable<Flight[]> {
    return this.http.get<Flight[]>(`${this.API_URL}/company/${company}`);
  }

  getFlightsByDateRange(startDate: Date, endDate: Date, minDate?: Date, maxDate?: Date): Observable<Flight[]> {
    let params = new HttpParams()
      .set('startDate', this.formatDate(startDate))
      .set('endDate', this.formatDate(endDate));
    
    if (minDate) {
      params = params.set('minDate', this.formatDate(minDate));
    }
    
    if (maxDate) {
      params = params.set('maxDate', this.formatDate(maxDate));
    }
    
    return this.http.get<Flight[]>(`${this.API_URL}/date`, { params });
  }

  getAllCompanies(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/companies`);
  }

  getDateRange(): Observable<DateRange> {
    return this.http.get<DateRange>(`${this.API_URL}/date-range`);
  }

  getFlightCount(): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/count`);
  }

  getCompanyCount(): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/companies/count`);
  }

  private formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }
}
