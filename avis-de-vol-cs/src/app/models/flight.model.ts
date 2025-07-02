export interface Flight {
  id?: number;
  flightNumber: string;
  company: string;
  date: Date;
}

export interface FlightSearchCriteria {
  company?: string;
  startDate?: Date;
  endDate?: Date;
}

export interface FlightErrorResponse {
  message: string;
}

export interface DateRange {
  minDate: Date;
  maxDate: Date;
}
