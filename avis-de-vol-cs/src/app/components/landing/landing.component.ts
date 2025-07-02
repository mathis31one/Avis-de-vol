import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ReviewService } from '../../services/review.service';
import { FlightService } from '../../services/flight.service';
import { User, Role } from '../../models/auth.model';

interface Feature {
  title: string;
  description: string;
  icon: string;
  color: string;
}

interface Stat {
  number: number;
  displayNumber: string;
  label: string;
  icon: string;
  growth: number;
  isLoading?: boolean;
}

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.scss']
})
export class LandingComponent implements OnInit {
  currentUser: User | null = null;
  Role = Role; // Make Role enum available in template

  features: Feature[] = [
    {
      title: 'Authentic Reviews',
      description: 'Read genuine experiences from verified travelers who actually flew with the airlines.',
      icon: 'verified',
      color: 'primary'
    },
    {
      title: 'Smart Comparisons',
      description: 'Compare airlines, routes, and aircraft with our intelligent comparison tools.',
      icon: 'compare_arrows',
      color: 'accent'
    },
    {
      title: 'Real-time Updates',
      description: 'Get instant notifications about flight delays, gate changes, and cancellations.',
      icon: 'notifications_active',
      color: 'primary'
    },
    {
      title: 'Global Coverage',
      description: 'Access reviews for over 500 airlines and thousands of routes worldwide.',
      icon: 'public',
      color: 'accent'
    },
    {
      title: 'Expert Insights',
      description: 'Benefit from detailed analysis and tips from aviation industry experts.',
      icon: 'psychology',
      color: 'primary'
    },
    {
      title: 'Community Driven',
      description: 'Join a community of 50,000+ travelers sharing their flight experiences.',
      icon: 'groups',
      color: 'accent'
    }
  ];

  stats: Stat[] = [
    {
      number: 0,
      displayNumber: '0+',
      label: 'Flight Reviews',
      icon: 'rate_review',
      growth: 15,
      isLoading: true
    },
    {
      number: 0,
      displayNumber: '0+',
      label: 'Airlines Covered',
      icon: 'flight',
      growth: 8,
      isLoading: true
    },
    {
      number: 50000,
      displayNumber: '50K+',
      label: 'Active Travelers',
      icon: 'people',
      growth: 23
    },
    {
      number: 0,
      displayNumber: '0+',
      label: 'Routes Reviewed',
      icon: 'map',
      growth: 12,
      isLoading: true
    }
  ];

  constructor(
    private authService: AuthService,
    private reviewService: ReviewService,
    private flightService: FlightService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check if user is authenticated
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    // Subscribe to current user
    this.authService.currentUser$.subscribe((user: User | null) => {
      this.currentUser = user;
    });
    
    // Load real statistics from the backend
    this.loadStatistics();
  }
  
  loadStatistics(): void {
    // Get review count
    this.reviewService.getReviewCount().subscribe({
      next: (count) => {
        const reviewStat = this.stats.find(s => s.label === 'Flight Reviews');
        if (reviewStat) {
          reviewStat.number = count;
          reviewStat.displayNumber = this.formatNumber(count);
          reviewStat.isLoading = false;
        }
      },
      error: (error) => {
        console.error('Error fetching review count:', error);
        const reviewStat = this.stats.find(s => s.label === 'Flight Reviews');
        if (reviewStat) {
          reviewStat.isLoading = false;
        }
      }
    });
    
    // Get company count
    this.flightService.getCompanyCount().subscribe({
      next: (count) => {
        const companyStat = this.stats.find(s => s.label === 'Airlines Covered');
        if (companyStat) {
          companyStat.number = count;
          companyStat.displayNumber = this.formatNumber(count);
          companyStat.isLoading = false;
        }
      },
      error: (error) => {
        console.error('Error fetching company count:', error);
        const companyStat = this.stats.find(s => s.label === 'Airlines Covered');
        if (companyStat) {
          companyStat.isLoading = false;
        }
      }
    });
    
    // Get flight count for routes
    this.flightService.getFlightCount().subscribe({
      next: (count) => {
        const routesStat = this.stats.find(s => s.label === 'Routes Reviewed');
        if (routesStat) {
          routesStat.number = count;
          routesStat.displayNumber = this.formatNumber(count);
          routesStat.isLoading = false;
        }
      },
      error: (error) => {
        console.error('Error fetching flight count:', error);
        const routesStat = this.stats.find(s => s.label === 'Routes Reviewed');
        if (routesStat) {
          routesStat.isLoading = false;
        }
      }
    });
  }
  
  formatNumber(num: number): string {
    if (num >= 1000000) {
      return (num / 1000000).toFixed(1) + 'M+';
    } else if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'K+';
    } else {
      return num.toString();
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
