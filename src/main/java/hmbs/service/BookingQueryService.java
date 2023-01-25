package hmbs.service;

import hmbs.domain.*; // for static metamodels
import hmbs.domain.Booking;
import hmbs.repository.BookingRepository;
import hmbs.service.criteria.BookingCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Booking} entities in the database.
 * The main input is a {@link BookingCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Booking} or a {@link Page} of {@link Booking} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookingQueryService extends QueryService<Booking> {

    private final Logger log = LoggerFactory.getLogger(BookingQueryService.class);

    private final BookingRepository bookingRepository;

    public BookingQueryService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Return a {@link List} of {@link Booking} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Booking> findByCriteria(BookingCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Booking> specification = createSpecification(criteria);
        return bookingRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Booking} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Booking> findByCriteria(BookingCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Booking> specification = createSpecification(criteria);
        return bookingRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookingCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Booking> specification = createSpecification(criteria);
        return bookingRepository.count(specification);
    }

    /**
     * Function to convert {@link BookingCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Booking> createSpecification(BookingCriteria criteria) {
        Specification<Booking> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Booking_.id));
            }
            if (criteria.getHotelId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHotelId(), Booking_.hotelId));
            }
            if (criteria.getRoomId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRoomId(), Booking_.roomId));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUserId(), Booking_.userId));
            }
            if (criteria.getCheckin() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCheckin(), Booking_.checkin));
            }
            if (criteria.getCheckout() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCheckout(), Booking_.checkout));
            }
            if (criteria.getNumOfGuests() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumOfGuests(), Booking_.numOfGuests));
            }
            if (criteria.getFinalPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFinalPrice(), Booking_.finalPrice));
            }
        }
        return specification;
    }
}
