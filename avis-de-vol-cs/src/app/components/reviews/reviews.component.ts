import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ReviewService } from '../../services/review.service';
import { FlightService } from '../../services/flight.service';
import { Review } from '../../models/review.model';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/auth.model';

@Component({
  selector: 'app-reviews',
  templateUrl: './reviews.component.html',
  styleUrls: ['./reviews.component.scss']
})
export class ReviewsComponent implements OnInit {
  currentUser: User | null = null;
  reviews: Review[] = [];
  companies: string[] = [];
  isLoading = false;
  filterForm: FormGroup;

  constructor(
    private reviewService: ReviewService,
    private flightService: FlightService,
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.filterForm = this.formBuilder.group({
      company: ['']
    });
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    this.loadCompanies();
    this.loadReviews();
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

  loadReviews(): void {
    this.isLoading = true;
    const company = this.filterForm.value.company || undefined;

    this.reviewService.getPublishedReviews(company).subscribe({
      next: (reviews) => {
        this.reviews = reviews;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading reviews:', error);
        this.showSnackBar('Error loading reviews', 'error');
        this.isLoading = false;
      }
    });
  }

  onFilterChange(): void {
    this.loadReviews();
  }

  clearFilter(): void {
    this.filterForm.reset();
    this.loadReviews();
  }

  getStars(rating: number): string[] {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(i <= rating ? 'star' : 'star_border');
    }
    return stars;
  }

  private showSnackBar(message: string, type: 'success' | 'error'): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      panelClass: type === 'success' ? 'success-snackbar' : 'error-snackbar'
    });
  }
}
