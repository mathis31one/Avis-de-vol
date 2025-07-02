import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Review } from '../../models/review.model';

@Component({
  selector: 'app-response-dialog',
  templateUrl: './response-dialog.component.html',
  styleUrls: ['./response-dialog.component.scss']
})
export class ResponseDialogComponent {
  responseForm: FormGroup;
  submitting = false;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ResponseDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { review: Review, isAdmin: boolean }
  ) {
    this.responseForm = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(300)]]
    });
  }

  onSubmit(): void {
    if (this.responseForm.invalid) return;
    
    this.submitting = true;
    const response = this.responseForm.value;
    this.dialogRef.close(response);
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
