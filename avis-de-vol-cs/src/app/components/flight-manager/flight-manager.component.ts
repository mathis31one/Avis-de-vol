import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormBuilder, FormGroup } from '@angular/forms';
import { FlightService } from '../../services/flight.service';
import { AuthService } from '../../services/auth.service';
import { Flight, FlightSearchCriteria } from '../../models/flight.model';
import { User } from '../../models/auth.model';
import { FlightDialogComponent } from './flight-dialog/flight-dialog.component';

@Component({
  selector: 'app-flight-manager',
  templateUrl: './flight-manager.component.html',
  styleUrls: ['./flight-manager.component.scss']
})
export class FlightManagerComponent implements OnInit {
  currentUser: User | null = null;
  flights: Flight[] = [];
  filteredFlights: Flight[] = [];
  companies: string[] = [];
  isLoading = false;
  searchForm: FormGroup;

  displayedColumns: string[] = ['flightNumber', 'company', 'date', 'actions'];

  constructor(
    private flightService: FlightService,
    private authService: AuthService,
    private dialog: MatDialog,
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

  openCreateDialog(): void {
    const dialogRef = this.dialog.open(FlightDialogComponent, {
      width: '500px',
      data: { flight: null, mode: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.createFlight(result);
      }
    });
  }

  openEditDialog(flight: Flight): void {
    const dialogRef = this.dialog.open(FlightDialogComponent, {
      width: '500px',
      data: { flight: { ...flight }, mode: 'edit' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && flight.id) {
        this.updateFlight(flight.id, result);
      }
    });
  }

  createFlight(flight: Flight): void {
    this.flightService.createFlight(flight).subscribe({
      next: (createdFlight) => {
        this.flights.push(createdFlight);
        this.filteredFlights = [...this.flights];
        this.showSnackBar('Flight created successfully', 'success');
        this.loadCompanies(); // Refresh companies list
      },
      error: (error) => {
        console.error('Error creating flight:', error);
        if (error.error?.message) {
          this.showSnackBar(error.error.message, 'error');
        } else {
          this.showSnackBar('Error creating flight', 'error');
        }
      }
    });
  }

  updateFlight(id: number, flight: Flight): void {
    this.flightService.updateFlight(id, flight).subscribe({
      next: (updatedFlight) => {
        const index = this.flights.findIndex(f => f.id === id);
        if (index !== -1) {
          this.flights[index] = updatedFlight;
          this.filteredFlights = [...this.flights];
        }
        this.showSnackBar('Flight updated successfully', 'success');
        this.loadCompanies(); // Refresh companies list
      },
      error: (error) => {
        console.error('Error updating flight:', error);
        if (error.error?.message) {
          this.showSnackBar(error.error.message, 'error');
        } else {
          this.showSnackBar('Error updating flight', 'error');
        }
      }
    });
  }

  deleteFlight(flight: Flight): void {
    if (confirm(`Are you sure you want to delete flight ${flight.flightNumber}?`)) {
      if (flight.id) {
        this.flightService.deleteFlight(flight.id).subscribe({
          next: () => {
            this.flights = this.flights.filter(f => f.id !== flight.id);
            this.filteredFlights = [...this.flights];
            this.showSnackBar('Flight deleted successfully', 'success');
          },
          error: (error) => {
            console.error('Error deleting flight:', error);
            this.showSnackBar('Error deleting flight', 'error');
          }
        });
      }
    }
  }

  private showSnackBar(message: string, type: 'success' | 'error'): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      panelClass: type === 'success' ? 'success-snackbar' : 'error-snackbar'
    });
  }
}
