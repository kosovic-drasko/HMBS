package hmbs.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Booking.
 */
@Entity
@Table(name = "booking")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "hotel_id")
    private Long hotelId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "checkin")
    private LocalDate checkin;

    @Column(name = "checkout")
    private LocalDate checkout;

    @Column(name = "num_of_guests")
    private Integer numOfGuests;

    @Column(name = "final_price")
    private Double finalPrice;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Booking id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHotelId() {
        return this.hotelId;
    }

    public Booking hotelId(Long hotelId) {
        this.setHotelId(hotelId);
        return this;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Long getRoomId() {
        return this.roomId;
    }

    public Booking roomId(Long roomId) {
        this.setRoomId(roomId);
        return this;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public Booking userId(Integer userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDate getCheckin() {
        return this.checkin;
    }

    public Booking checkin(LocalDate checkin) {
        this.setCheckin(checkin);
        return this;
    }

    public void setCheckin(LocalDate checkin) {
        this.checkin = checkin;
    }

    public LocalDate getCheckout() {
        return this.checkout;
    }

    public Booking checkout(LocalDate checkout) {
        this.setCheckout(checkout);
        return this;
    }

    public void setCheckout(LocalDate checkout) {
        this.checkout = checkout;
    }

    public Integer getNumOfGuests() {
        return this.numOfGuests;
    }

    public Booking numOfGuests(Integer numOfGuests) {
        this.setNumOfGuests(numOfGuests);
        return this;
    }

    public void setNumOfGuests(Integer numOfGuests) {
        this.numOfGuests = numOfGuests;
    }

    public Double getFinalPrice() {
        return this.finalPrice;
    }

    public Booking finalPrice(Double finalPrice) {
        this.setFinalPrice(finalPrice);
        return this;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booking)) {
            return false;
        }
        return id != null && id.equals(((Booking) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Booking{" +
            "id=" + getId() +
            ", hotelId=" + getHotelId() +
            ", roomId=" + getRoomId() +
            ", userId=" + getUserId() +
            ", checkin='" + getCheckin() + "'" +
            ", checkout='" + getCheckout() + "'" +
            ", numOfGuests=" + getNumOfGuests() +
            ", finalPrice=" + getFinalPrice() +
            "}";
    }
}
