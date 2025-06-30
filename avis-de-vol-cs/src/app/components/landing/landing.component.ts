import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
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
      number: 10000,
      displayNumber: '10,000+',
      label: 'Flight Reviews',
      icon: 'rate_review',
      growth: 15
    },
    {
      number: 500,
      displayNumber: '500+',
      label: 'Airlines Covered',
      icon: 'flight',
      growth: 8
    },
    {
      number: 50000,
      displayNumber: '50K+',
      label: 'Active Travelers',
      icon: 'people',
      growth: 23
    },
    {
      number: 1000,
      displayNumber: '1,000+',
      label: 'Routes Reviewed',
      icon: 'map',
      growth: 12
    }
  ];

  constructor(
    private authService: AuthService,
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
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
