import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FlightService } from '../../../services/flight.service';
import { Flight, DateRange } from '../../../models/flight.model';

export interface FlightDialogData {
  flight: Flight | null;
  mode: 'create' | 'edit';
  dateRange?: DateRange | null;
}

@Component({
  selector: 'app-flight-dialog',
  templateUrl: './flight-dialog.component.html',
  styleUrls: ['./flight-dialog.component.scss']
})
export class FlightDialogComponent implements OnInit {
  flightForm: FormGroup;
  isEditMode: boolean;
  companies: string[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private flightService: FlightService,
    public dialogRef: MatDialogRef<FlightDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: FlightDialogData
  ) {
    this.isEditMode = data.mode === 'edit';
    
    this.flightForm = this.formBuilder.group({
      flightNumber: ['', [Validators.required, Validators.pattern(/^[A-Z]{2}[0-9]{3,4}$/)]],
      company: ['', [Validators.required, Validators.minLength(2)]],
      date: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.loadCompanies();
    
    if (this.isEditMode && this.data.flight) {
      this.flightForm.patchValue({
        flightNumber: this.data.flight.flightNumber,
        company: this.data.flight.company,
        date: this.data.flight.date
      });
    }
  }

  private loadCompanies(): void {
    this.flightService.getAllCompanies().subscribe({
      next: (companies) => {
        this.companies = companies;
      },
      error: (error) => {
        console.error('Error loading companies:', error);
      }
    });
  }

  getTitle(): string {
    return this.isEditMode ? 'Edit Flight' : 'Create New Flight';
  }

  getSubmitButtonText(): string {
    return this.isEditMode ? 'Update' : 'Create';
  }

  onSubmit(): void {
    if (this.flightForm.valid) {
      const flight: Flight = {
        flightNumber: this.flightForm.value.flightNumber,
        company: this.flightForm.value.company,
        date: this.flightForm.value.date
      };
      
      this.dialogRef.close(flight);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getFieldError(fieldName: string): string {
    const field = this.flightForm.get(fieldName);
    if (field?.touched && field?.errors) {
      if (field.errors['required']) {
        return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} is required`;
      }
      if (field.errors['minlength']) {
        return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} must be at least ${field.errors['minlength'].requiredLength} characters`;
      }
      if (field.errors['pattern']) {
        return 'Flight number must be in format: XX123 or XX1234 (2 letters + 3-4 digits)';
      }
    }
    return '';
  }

  dateFilter = (date: Date | null): boolean => {
    if (!date) return false;
    
    return true;
  };
}