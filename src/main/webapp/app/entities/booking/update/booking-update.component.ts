import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IBooking, Booking } from '../booking.model';
import { BookingService } from '../service/booking.service';

@Component({
  selector: 'jhi-booking-update',
  templateUrl: './booking-update.component.html',
})
export class BookingUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    hotelId: [],
    roomId: [],
    userId: [],
    checkin: [],
    checkout: [],
    numOfGuests: [],
    finalPrice: [],
  });

  constructor(protected bookingService: BookingService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ booking }) => {
      this.updateForm(booking);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const booking = this.createFromForm();
    if (booking.id !== undefined) {
      this.subscribeToSaveResponse(this.bookingService.update(booking));
    } else {
      this.subscribeToSaveResponse(this.bookingService.create(booking));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBooking>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(booking: IBooking): void {
    this.editForm.patchValue({
      id: booking.id,
      hotelId: booking.hotelId,
      roomId: booking.roomId,
      userId: booking.userId,
      checkin: booking.checkin,
      checkout: booking.checkout,
      numOfGuests: booking.numOfGuests,
      finalPrice: booking.finalPrice,
    });
  }

  protected createFromForm(): IBooking {
    return {
      ...new Booking(),
      id: this.editForm.get(['id'])!.value,
      hotelId: this.editForm.get(['hotelId'])!.value,
      roomId: this.editForm.get(['roomId'])!.value,
      userId: this.editForm.get(['userId'])!.value,
      checkin: this.editForm.get(['checkin'])!.value,
      checkout: this.editForm.get(['checkout'])!.value,
      numOfGuests: this.editForm.get(['numOfGuests'])!.value,
      finalPrice: this.editForm.get(['finalPrice'])!.value,
    };
  }
}
