import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FlightService } from '../../services/flight.service';
import { Flight, FlightSearchCriteria } from '../../models/flight.model';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/auth.model';

@Component({
  selector: 'app-flights',
  templateUrl: './flights.component.html',
  styleUrls: ['./flights.component.scss']
})
export class FlightsComponent implements OnInit {
  currentUser: User | null = null;
  flights: Flight[] = [];
  filteredFlights: Flight[] = [];
  companies: string[] = [];
  isLoading = false;
  searchForm: FormGroup;
  isReviewMode = false;
  pageTitle = 'Available Flights';
  pageSubtitle = 'Browse and find flights';
  buttonLabel = 'Add Review';

  displayedColumns: string[] = ['flightNumber', 'company', 'date', 'actions'];

  constructor(
    private flightService: FlightService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder
  ) {
    this.searchForm = this.formBuilder.group({
      company: [''],
      startDate: [''],
      endDate: ['']
    });
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    
    // Check if we're in review mode from route data
    this.route.data.subscribe(data => {
      this.isReviewMode = data['reviewMode'] === true;
      if (this.isReviewMode) {
        this.pageTitle = 'Make a Review';
        this.pageSubtitle = 'Select a flight to review';
        this.buttonLabel = 'Write Review';
      }
    });
    
    this.loadFlights();
    this.loadCompanies();
  }

  loadFlights(): void {
    this.isLoading = true;
    this.flightService.getAllFlights().subscribe({
      next: (flights) => {
        this.flights = flights;
        this.filteredFlights = flights;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading flights:', error);
        this.showSnackBar('Error loading flights', 'error');
        this.isLoading = false;
      }
    });
  }

  loadCompanies(): void {
    this.flightService.getAllCompanies().subscribe({
      next: (companies) => {
        this.companies = companies;
      },
      error: (error) => {
        console.error('Error loading companies:', error);
      }
    });
  }

  onSearch(): void {
    const criteria: FlightSearchCriteria = {
      company: this.searchForm.value.company || undefined,
      startDate: this.searchForm.value.startDate || undefined,
      endDate: this.searchForm.value.endDate || undefined
    };

    this.isLoading = true;
    this.flightService.searchFlights(criteria).subscribe({
      next: (flights) => {
        this.filteredFlights = flights;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error searching flights:', error);
        this.showSnackBar('Error searching flights', 'error');
        this.isLoading = false;
      }
    });
  }

  clearSearch(): void {
    this.searchForm.reset();
    this.filteredFlights = this.flights;
  }

  addReview(flight: Flight): void {
    if (flight.id) {
      this.router.navigate(['/review-form', flight.id]);
    }
  }

  private showSnackBar(message: string, type: 'success' | 'error'): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      panelClass: type === 'success' ? 'success-snackbar' : 'error-snackbar'
    });
  }
}
