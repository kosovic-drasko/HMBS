package hmbs.service;

import hmbs.domain.Booking;
import hmbs.repository.BookingRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Booking}.
 */
@Service
@Transactional
public class BookingService {

    private final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Save a booking.
     *
     * @param booking the entity to save.
     * @return the persisted entity.
     */
    public Booking save(Booking booking) {
        log.debug("Request to save Booking : {}", booking);
        return bookingRepository.save(booking);
    }

    /**
     * Update a booking.
     *
     * @param booking the entity to save.
     * @return the persisted entity.
     */
    public Booking update(Booking booking) {
        log.debug("Request to save Booking : {}", booking);
        return bookingRepository.save(booking);
    }

    /**
     * Partially update a booking.
     *
     * @param booking the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Booking> partialUpdate(Booking booking) {
        log.debug("Request to partially update Booking : {}", booking);

        return bookingRepository
            .findById(booking.getId())
            .map(existingBooking -> {
                if (booking.getHotelId() != null) {
                    existingBooking.setHotelId(booking.getHotelId());
                }
                if (booking.getRoomId() != null) {
                    existingBooking.setRoomId(booking.getRoomId());
                }
                if (booking.getUserId() != null) {
                    existingBooking.setUserId(booking.getUserId());
                }
                if (booking.getCheckin() != null) {
                    existingBooking.setCheckin(booking.getCheckin());
                }
                if (booking.getCheckout() != null) {
                    existingBooking.setCheckout(booking.getCheckout());
                }
                if (booking.getNumOfGuests() != null) {
                    existingBooking.setNumOfGuests(booking.getNumOfGuests());
                }
                if (booking.getFinalPrice() != null) {
                    existingBooking.setFinalPrice(booking.getFinalPrice());
                }

                return existingBooking;
            })
            .map(bookingRepository::save);
    }

    /**
     * Get all the bookings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Booking> findAll(Pageable pageable) {
        log.debug("Request to get all Bookings");
        return bookingRepository.findAll(pageable);
    }

    /**
     * Get one booking by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Booking> findOne(Long id) {
        log.debug("Request to get Booking : {}", id);
        return bookingRepository.findById(id);
    }

    /**
     * Delete the booking by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Booking : {}", id);
        bookingRepository.deleteById(id);
    }
}
