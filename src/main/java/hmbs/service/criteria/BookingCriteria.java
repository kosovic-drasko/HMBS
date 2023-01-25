package hmbs.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link hmbs.domain.Booking} entity. This class is used
 * in {@link hmbs.web.rest.BookingResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /bookings?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class BookingCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter hotelId;

    private LongFilter roomId;

    private IntegerFilter userId;

    private LocalDateFilter checkin;

    private LocalDateFilter checkout;

    private IntegerFilter numOfGuests;

    private DoubleFilter finalPrice;

    private Boolean distinct;

    public BookingCriteria() {}

    public BookingCriteria(BookingCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.hotelId = other.hotelId == null ? null : other.hotelId.copy();
        this.roomId = other.roomId == null ? null : other.roomId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.checkin = other.checkin == null ? null : other.checkin.copy();
        this.checkout = other.checkout == null ? null : other.checkout.copy();
        this.numOfGuests = other.numOfGuests == null ? null : other.numOfGuests.copy();
        this.finalPrice = other.finalPrice == null ? null : other.finalPrice.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BookingCriteria copy() {
        return new BookingCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getHotelId() {
        return hotelId;
    }

    public LongFilter hotelId() {
        if (hotelId == null) {
            hotelId = new LongFilter();
        }
        return hotelId;
    }

    public void setHotelId(LongFilter hotelId) {
        this.hotelId = hotelId;
    }

    public LongFilter getRoomId() {
        return roomId;
    }

    public LongFilter roomId() {
        if (roomId == null) {
            roomId = new LongFilter();
        }
        return roomId;
    }

    public void setRoomId(LongFilter roomId) {
        this.roomId = roomId;
    }

    public IntegerFilter getUserId() {
        return userId;
    }

    public IntegerFilter userId() {
        if (userId == null) {
            userId = new IntegerFilter();
        }
        return userId;
    }

    public void setUserId(IntegerFilter userId) {
        this.userId = userId;
    }

    public LocalDateFilter getCheckin() {
        return checkin;
    }

    public LocalDateFilter checkin() {
        if (checkin == null) {
            checkin = new LocalDateFilter();
        }
        return checkin;
    }

    public void setCheckin(LocalDateFilter checkin) {
        this.checkin = checkin;
    }

    public LocalDateFilter getCheckout() {
        return checkout;
    }

    public LocalDateFilter checkout() {
        if (checkout == null) {
            checkout = new LocalDateFilter();
        }
        return checkout;
    }

    public void setCheckout(LocalDateFilter checkout) {
        this.checkout = checkout;
    }

    public IntegerFilter getNumOfGuests() {
        return numOfGuests;
    }

    public IntegerFilter numOfGuests() {
        if (numOfGuests == null) {
            numOfGuests = new IntegerFilter();
        }
        return numOfGuests;
    }

    public void setNumOfGuests(IntegerFilter numOfGuests) {
        this.numOfGuests = numOfGuests;
    }

    public DoubleFilter getFinalPrice() {
        return finalPrice;
    }

    public DoubleFilter finalPrice() {
        if (finalPrice == null) {
            finalPrice = new DoubleFilter();
        }
        return finalPrice;
    }

    public void setFinalPrice(DoubleFilter finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BookingCriteria that = (BookingCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(hotelId, that.hotelId) &&
            Objects.equals(roomId, that.roomId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(checkin, that.checkin) &&
            Objects.equals(checkout, that.checkout) &&
            Objects.equals(numOfGuests, that.numOfGuests) &&
            Objects.equals(finalPrice, that.finalPrice) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hotelId, roomId, userId, checkin, checkout, numOfGuests, finalPrice, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookingCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (hotelId != null ? "hotelId=" + hotelId + ", " : "") +
            (roomId != null ? "roomId=" + roomId + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (checkin != null ? "checkin=" + checkin + ", " : "") +
            (checkout != null ? "checkout=" + checkout + ", " : "") +
            (numOfGuests != null ? "numOfGuests=" + numOfGuests + ", " : "") +
            (finalPrice != null ? "finalPrice=" + finalPrice + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
