import dayjs from 'dayjs/esm';

export interface IBooking {
  id?: number;
  hotelId?: number | null;
  roomId?: number | null;
  userId?: number | null;
  checkin?: dayjs.Dayjs | null;
  checkout?: dayjs.Dayjs | null;
  numOfGuests?: number | null;
  finalPrice?: number | null;
}

export class Booking implements IBooking {
  constructor(
    public id?: number,
    public hotelId?: number | null,
    public roomId?: number | null,
    public userId?: number | null,
    public checkin?: dayjs.Dayjs | null,
    public checkout?: dayjs.Dayjs | null,
    public numOfGuests?: number | null,
    public finalPrice?: number | null
  ) {}
}

export function getBookingIdentifier(booking: IBooking): number | undefined {
  return booking.id;
}
