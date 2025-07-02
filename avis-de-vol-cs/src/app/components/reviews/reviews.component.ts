import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { ReviewService } from '../../services/review.service';
import { ResponseService } from '../../services/response.service';
import { FlightService } from '../../services/flight.service';
import { Review, ResponseCreateDto } from '../../models/review.model';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/auth.model';
import { ResponseDialogComponent } from '../response-dialog/response-dialog.component';

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
  responseForm: FormGroup;
  expandedReviewId: number | null = null;
  submittingResponse = false;

  constructor(
    private reviewService: ReviewService,
    private responseService: ResponseService,
    private flightService: FlightService,
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    this.filterForm = this.formBuilder.group({
      company: ['']
    });
    
    this.responseForm = this.formBuilder.group({
      content: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(300)]]
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

  toggleResponses(review: Review): void {
    if (review.id === this.expandedReviewId) {
      this.expandedReviewId = null;
    } else {
      this.expandedReviewId = review.id || null;
      this.loadResponses(review);
    }
  }

  loadResponses(review: Review): void {
    if (!review.id) return;
    
    if (!review.responses) {
      this.responseService.getResponsesByReview(review.id).subscribe({
        next: (responses) => {
          review.responses = responses;
        },
        error: (error) => {
          console.error('Error loading responses:', error);
          this.showSnackBar('Error loading responses', 'error');
        }
      });
    }
  }

  openResponseDialog(review: Review): void {
    if (!this.currentUser) {
      this.showSnackBar('Please login to add responses', 'error');
      return;
    }
    
    const dialogRef = this.dialog.open(ResponseDialogComponent, {
      width: '600px',
      data: { review, isAdmin: false }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && review.id) {
        this.submittingResponse = true;
        const responseDto: ResponseCreateDto = {
          content: result.content,
          reviewId: review.id
        };

        this.responseService.createResponse(responseDto).subscribe({
          next: (response) => {
            if (!review.responses) {
              review.responses = [];
            }
            review.responses.push(response);
            this.submittingResponse = false;
            this.showSnackBar('Response added successfully', 'success');
          },
          error: (error) => {
            console.error('Error adding response:', error);
            this.showSnackBar('Error adding response', 'error');
            this.submittingResponse = false;
          }
        });
      }
    });
  }

  private showSnackBar(message: string, type: 'success' | 'error'): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      panelClass: type === 'success' ? 'success-snackbar' : 'error-snackbar'
    });
  }
}
