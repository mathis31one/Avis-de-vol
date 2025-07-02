import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { ReviewService } from '../../services/review.service';
import { ResponseService } from '../../services/response.service';
import { FlightService } from '../../services/flight.service';
import { Review, ReviewStatus, ResponseCreateDto } from '../../models/review.model';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/auth.model';
import { ResponseDialogComponent } from '../response-dialog/response-dialog.component';

@Component({
  selector: 'app-admin-reviews',
  templateUrl: './admin-reviews.component.html',
  styleUrls: ['./admin-reviews.component.scss']
})
export class AdminReviewsComponent implements OnInit {
  currentUser: User | null = null;
  reviews: Review[] = [];
  companies: string[] = [];
  isLoading = false;
  isProcessing = false;
  filterForm: FormGroup;
  responseForm: FormGroup;
  ReviewStatus = ReviewStatus; // Make enum available to template

  statusOptions = [
    { value: '', label: 'All Reviews' },
    { value: ReviewStatus.TRAITE, label: 'Pending Reviews' },
    { value: ReviewStatus.PUBLIE, label: 'Published Reviews' },
    { value: ReviewStatus.REJETE, label: 'Rejected Reviews' }
  ];

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
      company: [''],
      status: ['']
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
    const filters = {
      company: this.filterForm.value.company || undefined,
      status: this.filterForm.value.status || undefined
    };

    this.reviewService.getReviews(filters).subscribe({
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

  publishReview(review: Review): void {
    if (!review.id) return;
    
    this.isProcessing = true;
    this.reviewService.publishReview(review.id).subscribe({
      next: (updatedReview) => {
        const index = this.reviews.findIndex(r => r.id === review.id);
        if (index !== -1) {
          this.reviews[index] = updatedReview;
        }
        this.showSnackBar('Review published successfully', 'success');
        this.isProcessing = false;
      },
      error: (error) => {
        console.error('Error publishing review:', error);
        this.showSnackBar('Error publishing review', 'error');
        this.isProcessing = false;
      }
    });
  }

  rejectReview(review: Review): void {
    if (!review.id) return;
    
    this.isProcessing = true;
    this.reviewService.rejectReview(review.id).subscribe({
      next: (updatedReview) => {
        const index = this.reviews.findIndex(r => r.id === review.id);
        if (index !== -1) {
          this.reviews[index] = updatedReview;
        }
        this.showSnackBar('Review rejected successfully', 'success');
        this.isProcessing = false;
      },
      error: (error) => {
        console.error('Error rejecting review:', error);
        this.showSnackBar('Error rejecting review', 'error');
        this.isProcessing = false;
      }
    });
  }

  deleteReview(review: Review): void {
    if (!review.id) return;
    
    if (confirm('Are you sure you want to delete this review?')) {
      this.isProcessing = true;
      this.reviewService.deleteReview(review.id).subscribe({
        next: () => {
          this.reviews = this.reviews.filter(r => r.id !== review.id);
          this.showSnackBar('Review deleted successfully', 'success');
          this.isProcessing = false;
        },
        error: (error) => {
          console.error('Error deleting review:', error);
          this.showSnackBar('Error deleting review', 'error');
          this.isProcessing = false;
        }
      });
    }
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
    const dialogRef = this.dialog.open(ResponseDialogComponent, {
      width: '600px',
      data: { review, isAdmin: true }
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

  deleteResponse(review: Review, responseId: number): void {
    if (confirm('Are you sure you want to delete this response?')) {
      this.responseService.deleteResponse(responseId).subscribe({
        next: () => {
          if (review.responses) {
            review.responses = review.responses.filter(r => r.id !== responseId);
          }
          this.showSnackBar('Response deleted successfully', 'success');
        },
        error: (error) => {
          console.error('Error deleting response:', error);
          this.showSnackBar('Error deleting response', 'error');
        }
      });
    }
  }

  getStars(rating: number): string[] {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(i <= rating ? 'star' : 'star_border');
    }
    return stars;
  }

  getStatusClass(status: ReviewStatus | undefined): string {
    if (!status) return '';
    
    switch (status) {
      case ReviewStatus.TRAITE:
        return 'status-pending';
      case ReviewStatus.PUBLIE:
        return 'status-published';
      case ReviewStatus.REJETE:
        return 'status-rejected';
      default:
        return '';
    }
  }

  private showSnackBar(message: string, type: 'success' | 'error'): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      panelClass: type === 'success' ? 'success-snackbar' : 'error-snackbar'
    });
  }
}
