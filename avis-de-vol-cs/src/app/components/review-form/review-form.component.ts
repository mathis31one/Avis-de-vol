import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FlightService } from '../../services/flight.service';
import { ReviewService } from '../../services/review.service';
import { Flight } from '../../models/flight.model';
import { ReviewCreateDto } from '../../models/review.model';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/auth.model';

@Component({
  selector: 'app-review-form',
  templateUrl: './review-form.component.html',
  styleUrls: ['./review-form.component.scss']
})
export class ReviewFormComponent implements OnInit {
  currentUser: User | null = null;
  reviewForm: FormGroup;
  selectedFlight: Flight | null = null;
  isLoading = false;
  submitting = false;
  maxRating = 5;
  stars: number[] = [1, 2, 3, 4, 5];
  hoveredRating = 0;
  
  constructor(
    private formBuilder: FormBuilder,
    private flightService: FlightService,
    private reviewService: ReviewService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.reviewForm = this.formBuilder.group({
      notation: [0, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]]
    });
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    
    // Get flight ID from route params
    const flightId = this.route.snapshot.paramMap.get('flightId');
    if (flightId) {
      this.loadFlightDetails(+flightId);
    } else {
      this.router.navigate(['/make-review']);
    }
  }

  loadFlightDetails(flightId: number): void {
    this.isLoading = true;
    this.flightService.getFlightById(flightId).subscribe({
      next: (flight) => {
        this.selectedFlight = flight;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading flight details:', error);
        this.showSnackBar('Error loading flight details', 'error');
        this.isLoading = false;
        this.router.navigate(['/make-review']);
      }
    });
  }

  setRating(rating: number): void {
    this.reviewForm.patchValue({ notation: rating });
  }

  onStarHover(rating: number): void {
    this.hoveredRating = rating;
  }

  onStarLeave(): void {
    this.hoveredRating = 0;
  }

  getStar(index: number): string {
    const rating = this.reviewForm.get('notation')?.value || 0;
    
    if (this.hoveredRating > 0) {
      return index <= this.hoveredRating ? 'star' : 'star_border';
    }
    
    return index <= rating ? 'star' : 'star_border';
  }

  getStarColor(index: number): string {
    const rating = this.reviewForm.get('notation')?.value || 0;
    const hoveredRating = this.hoveredRating;
    
    if ((hoveredRating > 0 && index <= hoveredRating) || (hoveredRating === 0 && index <= rating)) {
      return '#ffc107'; // Gold color for selected stars
    }
    
    return 'gray'; // Gray for unselected stars
  }

  onSubmit(): void {
    if (this.reviewForm.invalid || !this.selectedFlight?.id) {
      this.markFormGroupTouched(this.reviewForm);
      return;
    }

    const reviewData: ReviewCreateDto = {
      flightId: this.selectedFlight.id,
      notation: this.reviewForm.value.notation,
      comment: this.reviewForm.value.comment
    };

    this.submitting = true;
    this.reviewService.createReview(reviewData).subscribe({
      next: () => {
        this.showSnackBar('Your review has been submitted successfully!', 'success');
        this.submitting = false;
        this.router.navigate(['/landing']);
      },
      error: (error) => {
        console.error('Error submitting review:', error);
        this.showSnackBar('Error submitting review', 'error');
        this.submitting = false;
      }
    });
  }

  getErrorMessage(controlName: string): string {
    const control = this.reviewForm.get(controlName);
    
    if (!control || !control.errors || !control.touched) {
      return '';
    }
    
    if (control.errors['required']) {
      return 'This field is required';
    }
    
    if (controlName === 'notation') {
      if (control.errors['min']) {
        return 'Please select a rating';
      }
    }
    
    if (controlName === 'comment') {
      if (control.errors['minlength']) {
        return `Comment must be at least ${control.errors['minlength'].requiredLength} characters`;
      }
      if (control.errors['maxlength']) {
        return `Comment cannot exceed ${control.errors['maxlength'].requiredLength} characters`;
      }
    }
    
    return 'Invalid input';
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if ((control as any).controls) {
        this.markFormGroupTouched(control as FormGroup);
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
