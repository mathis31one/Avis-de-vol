import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { LandingComponent } from './components/landing/landing.component';
import { AdminPanelComponent } from './components/admin-panel/admin-panel.component';
import { FlightManagerComponent } from './components/flight-manager/flight-manager.component';
import { AdminGuard } from './guards/admin.guard';
import { FlightsComponent } from './components/flights/flights.component';
import { ReviewFormComponent } from './components/review-form/review-form.component';
import { ReviewsComponent } from './components/reviews/reviews.component';
import { AdminReviewsComponent } from './components/admin-reviews/admin-reviews.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'landing', component: LandingComponent },
  { path: 'flights', component: FlightsComponent },
  { path: 'make-review', component: FlightsComponent, data: { reviewMode: true } },
  { path: 'review-form/:flightId', component: ReviewFormComponent },
  { path: 'reviews', component: ReviewsComponent },
  { 
    path: 'admin', 
    component: AdminPanelComponent, 
    canActivate: [AdminGuard]
  },
  { 
    path: 'admin/flights', 
    component: FlightManagerComponent, 
    canActivate: [AdminGuard]
  },
  {
    path: 'admin/reviews',
    component: AdminReviewsComponent,
    canActivate: [AdminGuard]
  },
  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
