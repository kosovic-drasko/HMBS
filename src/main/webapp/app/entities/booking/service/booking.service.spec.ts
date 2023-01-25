import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IBooking, Booking } from '../booking.model';

import { BookingService } from './booking.service';

describe('Booking Service', () => {
  let service: BookingService;
  let httpMock: HttpTestingController;
  let elemDefault: IBooking;
  let expectedResult: IBooking | IBooking[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BookingService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      hotelId: 0,
      roomId: 0,
      userId: 0,
      checkin: currentDate,
      checkout: currentDate,
      numOfGuests: 0,
      finalPrice: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          checkin: currentDate.format(DATE_FORMAT),
          checkout: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Booking', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          checkin: currentDate.format(DATE_FORMAT),
          checkout: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          checkin: currentDate,
          checkout: currentDate,
        },
        returnedFromService
      );

      service.create(new Booking()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Booking', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          hotelId: 1,
          roomId: 1,
          userId: 1,
          checkin: currentDate.format(DATE_FORMAT),
          checkout: currentDate.format(DATE_FORMAT),
          numOfGuests: 1,
          finalPrice: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          checkin: currentDate,
          checkout: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Booking', () => {
      const patchObject = Object.assign(
        {
          checkout: currentDate.format(DATE_FORMAT),
        },
        new Booking()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          checkin: currentDate,
          checkout: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Booking', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          hotelId: 1,
          roomId: 1,
          userId: 1,
          checkin: currentDate.format(DATE_FORMAT),
          checkout: currentDate.format(DATE_FORMAT),
          numOfGuests: 1,
          finalPrice: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          checkin: currentDate,
          checkout: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Booking', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addBookingToCollectionIfMissing', () => {
      it('should add a Booking to an empty array', () => {
        const booking: IBooking = { id: 123 };
        expectedResult = service.addBookingToCollectionIfMissing([], booking);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(booking);
      });

      it('should not add a Booking to an array that contains it', () => {
        const booking: IBooking = { id: 123 };
        const bookingCollection: IBooking[] = [
          {
            ...booking,
          },
          { id: 456 },
        ];
        expectedResult = service.addBookingToCollectionIfMissing(bookingCollection, booking);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Booking to an array that doesn't contain it", () => {
        const booking: IBooking = { id: 123 };
        const bookingCollection: IBooking[] = [{ id: 456 }];
        expectedResult = service.addBookingToCollectionIfMissing(bookingCollection, booking);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(booking);
      });

      it('should add only unique Booking to an array', () => {
        const bookingArray: IBooking[] = [{ id: 123 }, { id: 456 }, { id: 66444 }];
        const bookingCollection: IBooking[] = [{ id: 123 }];
        expectedResult = service.addBookingToCollectionIfMissing(bookingCollection, ...bookingArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const booking: IBooking = { id: 123 };
        const booking2: IBooking = { id: 456 };
        expectedResult = service.addBookingToCollectionIfMissing([], booking, booking2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(booking);
        expect(expectedResult).toContain(booking2);
      });

      it('should accept null and undefined values', () => {
        const booking: IBooking = { id: 123 };
        expectedResult = service.addBookingToCollectionIfMissing([], null, booking, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(booking);
      });

      it('should return initial array if no Booking is added', () => {
        const bookingCollection: IBooking[] = [{ id: 123 }];
        expectedResult = service.addBookingToCollectionIfMissing(bookingCollection, undefined, null);
        expect(expectedResult).toEqual(bookingCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
