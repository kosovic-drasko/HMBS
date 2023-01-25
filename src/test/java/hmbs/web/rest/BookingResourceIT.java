package hmbs.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import hmbs.IntegrationTest;
import hmbs.domain.Booking;
import hmbs.repository.BookingRepository;
import hmbs.service.criteria.BookingCriteria;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BookingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookingResourceIT {

    private static final Long DEFAULT_HOTEL_ID = 1L;
    private static final Long UPDATED_HOTEL_ID = 2L;
    private static final Long SMALLER_HOTEL_ID = 1L - 1L;

    private static final Long DEFAULT_ROOM_ID = 1L;
    private static final Long UPDATED_ROOM_ID = 2L;
    private static final Long SMALLER_ROOM_ID = 1L - 1L;

    private static final Integer DEFAULT_USER_ID = 1;
    private static final Integer UPDATED_USER_ID = 2;
    private static final Integer SMALLER_USER_ID = 1 - 1;

    private static final LocalDate DEFAULT_CHECKIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CHECKIN = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CHECKIN = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_CHECKOUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CHECKOUT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CHECKOUT = LocalDate.ofEpochDay(-1L);

    private static final Integer DEFAULT_NUM_OF_GUESTS = 1;
    private static final Integer UPDATED_NUM_OF_GUESTS = 2;
    private static final Integer SMALLER_NUM_OF_GUESTS = 1 - 1;

    private static final Double DEFAULT_FINAL_PRICE = 1D;
    private static final Double UPDATED_FINAL_PRICE = 2D;
    private static final Double SMALLER_FINAL_PRICE = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/bookings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookingMockMvc;

    private Booking booking;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Booking createEntity(EntityManager em) {
        Booking booking = new Booking()
            .hotelId(DEFAULT_HOTEL_ID)
            .roomId(DEFAULT_ROOM_ID)
            .userId(DEFAULT_USER_ID)
            .checkin(DEFAULT_CHECKIN)
            .checkout(DEFAULT_CHECKOUT)
            .numOfGuests(DEFAULT_NUM_OF_GUESTS)
            .finalPrice(DEFAULT_FINAL_PRICE);
        return booking;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Booking createUpdatedEntity(EntityManager em) {
        Booking booking = new Booking()
            .hotelId(UPDATED_HOTEL_ID)
            .roomId(UPDATED_ROOM_ID)
            .userId(UPDATED_USER_ID)
            .checkin(UPDATED_CHECKIN)
            .checkout(UPDATED_CHECKOUT)
            .numOfGuests(UPDATED_NUM_OF_GUESTS)
            .finalPrice(UPDATED_FINAL_PRICE);
        return booking;
    }

    @BeforeEach
    public void initTest() {
        booking = createEntity(em);
    }

    @Test
    @Transactional
    void createBooking() throws Exception {
        int databaseSizeBeforeCreate = bookingRepository.findAll().size();
        // Create the Booking
        restBookingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isCreated());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeCreate + 1);
        Booking testBooking = bookingList.get(bookingList.size() - 1);
        assertThat(testBooking.getHotelId()).isEqualTo(DEFAULT_HOTEL_ID);
        assertThat(testBooking.getRoomId()).isEqualTo(DEFAULT_ROOM_ID);
        assertThat(testBooking.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testBooking.getCheckin()).isEqualTo(DEFAULT_CHECKIN);
        assertThat(testBooking.getCheckout()).isEqualTo(DEFAULT_CHECKOUT);
        assertThat(testBooking.getNumOfGuests()).isEqualTo(DEFAULT_NUM_OF_GUESTS);
        assertThat(testBooking.getFinalPrice()).isEqualTo(DEFAULT_FINAL_PRICE);
    }

    @Test
    @Transactional
    void createBookingWithExistingId() throws Exception {
        // Create the Booking with an existing ID
        booking.setId(1L);

        int databaseSizeBeforeCreate = bookingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isBadRequest());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBookings() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList
        restBookingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(booking.getId().intValue())))
            .andExpect(jsonPath("$.[*].hotelId").value(hasItem(DEFAULT_HOTEL_ID.intValue())))
            .andExpect(jsonPath("$.[*].roomId").value(hasItem(DEFAULT_ROOM_ID.intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].checkin").value(hasItem(DEFAULT_CHECKIN.toString())))
            .andExpect(jsonPath("$.[*].checkout").value(hasItem(DEFAULT_CHECKOUT.toString())))
            .andExpect(jsonPath("$.[*].numOfGuests").value(hasItem(DEFAULT_NUM_OF_GUESTS)))
            .andExpect(jsonPath("$.[*].finalPrice").value(hasItem(DEFAULT_FINAL_PRICE.doubleValue())));
    }

    @Test
    @Transactional
    void getBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get the booking
        restBookingMockMvc
            .perform(get(ENTITY_API_URL_ID, booking.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(booking.getId().intValue()))
            .andExpect(jsonPath("$.hotelId").value(DEFAULT_HOTEL_ID.intValue()))
            .andExpect(jsonPath("$.roomId").value(DEFAULT_ROOM_ID.intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.checkin").value(DEFAULT_CHECKIN.toString()))
            .andExpect(jsonPath("$.checkout").value(DEFAULT_CHECKOUT.toString()))
            .andExpect(jsonPath("$.numOfGuests").value(DEFAULT_NUM_OF_GUESTS))
            .andExpect(jsonPath("$.finalPrice").value(DEFAULT_FINAL_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    void getBookingsByIdFiltering() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        Long id = booking.getId();

        defaultBookingShouldBeFound("id.equals=" + id);
        defaultBookingShouldNotBeFound("id.notEquals=" + id);

        defaultBookingShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBookingShouldNotBeFound("id.greaterThan=" + id);

        defaultBookingShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBookingShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBookingsByHotelIdIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where hotelId equals to DEFAULT_HOTEL_ID
        defaultBookingShouldBeFound("hotelId.equals=" + DEFAULT_HOTEL_ID);

        // Get all the bookingList where hotelId equals to UPDATED_HOTEL_ID
        defaultBookingShouldNotBeFound("hotelId.equals=" + UPDATED_HOTEL_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByHotelIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where hotelId not equals to DEFAULT_HOTEL_ID
        defaultBookingShouldNotBeFound("hotelId.notEquals=" + DEFAULT_HOTEL_ID);

        // Get all the bookingList where hotelId not equals to UPDATED_HOTEL_ID
        defaultBookingShouldBeFound("hotelId.notEquals=" + UPDATED_HOTEL_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByHotelIdIsInShouldWork() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where hotelId in DEFAULT_HOTEL_ID or UPDATED_HOTEL_ID
        defaultBookingShouldBeFound("hotelId.in=" + DEFAULT_HOTEL_ID + "," + UPDATED_HOTEL_ID);

        // Get all the bookingList where hotelId equals to UPDATED_HOTEL_ID
        defaultBookingShouldNotBeFound("hotelId.in=" + UPDATED_HOTEL_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByHotelIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where hotelId is not null
        defaultBookingShouldBeFound("hotelId.specified=true");

        // Get all the bookingList where hotelId is null
        defaultBookingShouldNotBeFound("hotelId.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingsByHotelIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where hotelId is greater than or equal to DEFAULT_HOTEL_ID
        defaultBookingShouldBeFound("hotelId.greaterThanOrEqual=" + DEFAULT_HOTEL_ID);

        // Get all the bookingList where hotelId is greater than or equal to UPDATED_HOTEL_ID
        defaultBookingShouldNotBeFound("hotelId.greaterThanOrEqual=" + UPDATED_HOTEL_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByHotelIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where hotelId is less than or equal to DEFAULT_HOTEL_ID
        defaultBookingShouldBeFound("hotelId.lessThanOrEqual=" + DEFAULT_HOTEL_ID);

        // Get all the bookingList where hotelId is less than or equal to SMALLER_HOTEL_ID
        defaultBookingShouldNotBeFound("hotelId.lessThanOrEqual=" + SMALLER_HOTEL_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByHotelIdIsLessThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where hotelId is less than DEFAULT_HOTEL_ID
        defaultBookingShouldNotBeFound("hotelId.lessThan=" + DEFAULT_HOTEL_ID);

        // Get all the bookingList where hotelId is less than UPDATED_HOTEL_ID
        defaultBookingShouldBeFound("hotelId.lessThan=" + UPDATED_HOTEL_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByHotelIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where hotelId is greater than DEFAULT_HOTEL_ID
        defaultBookingShouldNotBeFound("hotelId.greaterThan=" + DEFAULT_HOTEL_ID);

        // Get all the bookingList where hotelId is greater than SMALLER_HOTEL_ID
        defaultBookingShouldBeFound("hotelId.greaterThan=" + SMALLER_HOTEL_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByRoomIdIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where roomId equals to DEFAULT_ROOM_ID
        defaultBookingShouldBeFound("roomId.equals=" + DEFAULT_ROOM_ID);

        // Get all the bookingList where roomId equals to UPDATED_ROOM_ID
        defaultBookingShouldNotBeFound("roomId.equals=" + UPDATED_ROOM_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByRoomIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where roomId not equals to DEFAULT_ROOM_ID
        defaultBookingShouldNotBeFound("roomId.notEquals=" + DEFAULT_ROOM_ID);

        // Get all the bookingList where roomId not equals to UPDATED_ROOM_ID
        defaultBookingShouldBeFound("roomId.notEquals=" + UPDATED_ROOM_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByRoomIdIsInShouldWork() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where roomId in DEFAULT_ROOM_ID or UPDATED_ROOM_ID
        defaultBookingShouldBeFound("roomId.in=" + DEFAULT_ROOM_ID + "," + UPDATED_ROOM_ID);

        // Get all the bookingList where roomId equals to UPDATED_ROOM_ID
        defaultBookingShouldNotBeFound("roomId.in=" + UPDATED_ROOM_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByRoomIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where roomId is not null
        defaultBookingShouldBeFound("roomId.specified=true");

        // Get all the bookingList where roomId is null
        defaultBookingShouldNotBeFound("roomId.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingsByRoomIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where roomId is greater than or equal to DEFAULT_ROOM_ID
        defaultBookingShouldBeFound("roomId.greaterThanOrEqual=" + DEFAULT_ROOM_ID);

        // Get all the bookingList where roomId is greater than or equal to UPDATED_ROOM_ID
        defaultBookingShouldNotBeFound("roomId.greaterThanOrEqual=" + UPDATED_ROOM_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByRoomIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where roomId is less than or equal to DEFAULT_ROOM_ID
        defaultBookingShouldBeFound("roomId.lessThanOrEqual=" + DEFAULT_ROOM_ID);

        // Get all the bookingList where roomId is less than or equal to SMALLER_ROOM_ID
        defaultBookingShouldNotBeFound("roomId.lessThanOrEqual=" + SMALLER_ROOM_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByRoomIdIsLessThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where roomId is less than DEFAULT_ROOM_ID
        defaultBookingShouldNotBeFound("roomId.lessThan=" + DEFAULT_ROOM_ID);

        // Get all the bookingList where roomId is less than UPDATED_ROOM_ID
        defaultBookingShouldBeFound("roomId.lessThan=" + UPDATED_ROOM_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByRoomIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where roomId is greater than DEFAULT_ROOM_ID
        defaultBookingShouldNotBeFound("roomId.greaterThan=" + DEFAULT_ROOM_ID);

        // Get all the bookingList where roomId is greater than SMALLER_ROOM_ID
        defaultBookingShouldBeFound("roomId.greaterThan=" + SMALLER_ROOM_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where userId equals to DEFAULT_USER_ID
        defaultBookingShouldBeFound("userId.equals=" + DEFAULT_USER_ID);

        // Get all the bookingList where userId equals to UPDATED_USER_ID
        defaultBookingShouldNotBeFound("userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByUserIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where userId not equals to DEFAULT_USER_ID
        defaultBookingShouldNotBeFound("userId.notEquals=" + DEFAULT_USER_ID);

        // Get all the bookingList where userId not equals to UPDATED_USER_ID
        defaultBookingShouldBeFound("userId.notEquals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where userId in DEFAULT_USER_ID or UPDATED_USER_ID
        defaultBookingShouldBeFound("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID);

        // Get all the bookingList where userId equals to UPDATED_USER_ID
        defaultBookingShouldNotBeFound("userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where userId is not null
        defaultBookingShouldBeFound("userId.specified=true");

        // Get all the bookingList where userId is null
        defaultBookingShouldNotBeFound("userId.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingsByUserIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where userId is greater than or equal to DEFAULT_USER_ID
        defaultBookingShouldBeFound("userId.greaterThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the bookingList where userId is greater than or equal to UPDATED_USER_ID
        defaultBookingShouldNotBeFound("userId.greaterThanOrEqual=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByUserIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where userId is less than or equal to DEFAULT_USER_ID
        defaultBookingShouldBeFound("userId.lessThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the bookingList where userId is less than or equal to SMALLER_USER_ID
        defaultBookingShouldNotBeFound("userId.lessThanOrEqual=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByUserIdIsLessThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where userId is less than DEFAULT_USER_ID
        defaultBookingShouldNotBeFound("userId.lessThan=" + DEFAULT_USER_ID);

        // Get all the bookingList where userId is less than UPDATED_USER_ID
        defaultBookingShouldBeFound("userId.lessThan=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByUserIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where userId is greater than DEFAULT_USER_ID
        defaultBookingShouldNotBeFound("userId.greaterThan=" + DEFAULT_USER_ID);

        // Get all the bookingList where userId is greater than SMALLER_USER_ID
        defaultBookingShouldBeFound("userId.greaterThan=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckinIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkin equals to DEFAULT_CHECKIN
        defaultBookingShouldBeFound("checkin.equals=" + DEFAULT_CHECKIN);

        // Get all the bookingList where checkin equals to UPDATED_CHECKIN
        defaultBookingShouldNotBeFound("checkin.equals=" + UPDATED_CHECKIN);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckinIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkin not equals to DEFAULT_CHECKIN
        defaultBookingShouldNotBeFound("checkin.notEquals=" + DEFAULT_CHECKIN);

        // Get all the bookingList where checkin not equals to UPDATED_CHECKIN
        defaultBookingShouldBeFound("checkin.notEquals=" + UPDATED_CHECKIN);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckinIsInShouldWork() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkin in DEFAULT_CHECKIN or UPDATED_CHECKIN
        defaultBookingShouldBeFound("checkin.in=" + DEFAULT_CHECKIN + "," + UPDATED_CHECKIN);

        // Get all the bookingList where checkin equals to UPDATED_CHECKIN
        defaultBookingShouldNotBeFound("checkin.in=" + UPDATED_CHECKIN);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckinIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkin is not null
        defaultBookingShouldBeFound("checkin.specified=true");

        // Get all the bookingList where checkin is null
        defaultBookingShouldNotBeFound("checkin.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingsByCheckinIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkin is greater than or equal to DEFAULT_CHECKIN
        defaultBookingShouldBeFound("checkin.greaterThanOrEqual=" + DEFAULT_CHECKIN);

        // Get all the bookingList where checkin is greater than or equal to UPDATED_CHECKIN
        defaultBookingShouldNotBeFound("checkin.greaterThanOrEqual=" + UPDATED_CHECKIN);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckinIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkin is less than or equal to DEFAULT_CHECKIN
        defaultBookingShouldBeFound("checkin.lessThanOrEqual=" + DEFAULT_CHECKIN);

        // Get all the bookingList where checkin is less than or equal to SMALLER_CHECKIN
        defaultBookingShouldNotBeFound("checkin.lessThanOrEqual=" + SMALLER_CHECKIN);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckinIsLessThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkin is less than DEFAULT_CHECKIN
        defaultBookingShouldNotBeFound("checkin.lessThan=" + DEFAULT_CHECKIN);

        // Get all the bookingList where checkin is less than UPDATED_CHECKIN
        defaultBookingShouldBeFound("checkin.lessThan=" + UPDATED_CHECKIN);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckinIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkin is greater than DEFAULT_CHECKIN
        defaultBookingShouldNotBeFound("checkin.greaterThan=" + DEFAULT_CHECKIN);

        // Get all the bookingList where checkin is greater than SMALLER_CHECKIN
        defaultBookingShouldBeFound("checkin.greaterThan=" + SMALLER_CHECKIN);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckoutIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkout equals to DEFAULT_CHECKOUT
        defaultBookingShouldBeFound("checkout.equals=" + DEFAULT_CHECKOUT);

        // Get all the bookingList where checkout equals to UPDATED_CHECKOUT
        defaultBookingShouldNotBeFound("checkout.equals=" + UPDATED_CHECKOUT);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckoutIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkout not equals to DEFAULT_CHECKOUT
        defaultBookingShouldNotBeFound("checkout.notEquals=" + DEFAULT_CHECKOUT);

        // Get all the bookingList where checkout not equals to UPDATED_CHECKOUT
        defaultBookingShouldBeFound("checkout.notEquals=" + UPDATED_CHECKOUT);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckoutIsInShouldWork() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkout in DEFAULT_CHECKOUT or UPDATED_CHECKOUT
        defaultBookingShouldBeFound("checkout.in=" + DEFAULT_CHECKOUT + "," + UPDATED_CHECKOUT);

        // Get all the bookingList where checkout equals to UPDATED_CHECKOUT
        defaultBookingShouldNotBeFound("checkout.in=" + UPDATED_CHECKOUT);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckoutIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkout is not null
        defaultBookingShouldBeFound("checkout.specified=true");

        // Get all the bookingList where checkout is null
        defaultBookingShouldNotBeFound("checkout.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingsByCheckoutIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkout is greater than or equal to DEFAULT_CHECKOUT
        defaultBookingShouldBeFound("checkout.greaterThanOrEqual=" + DEFAULT_CHECKOUT);

        // Get all the bookingList where checkout is greater than or equal to UPDATED_CHECKOUT
        defaultBookingShouldNotBeFound("checkout.greaterThanOrEqual=" + UPDATED_CHECKOUT);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckoutIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkout is less than or equal to DEFAULT_CHECKOUT
        defaultBookingShouldBeFound("checkout.lessThanOrEqual=" + DEFAULT_CHECKOUT);

        // Get all the bookingList where checkout is less than or equal to SMALLER_CHECKOUT
        defaultBookingShouldNotBeFound("checkout.lessThanOrEqual=" + SMALLER_CHECKOUT);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckoutIsLessThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkout is less than DEFAULT_CHECKOUT
        defaultBookingShouldNotBeFound("checkout.lessThan=" + DEFAULT_CHECKOUT);

        // Get all the bookingList where checkout is less than UPDATED_CHECKOUT
        defaultBookingShouldBeFound("checkout.lessThan=" + UPDATED_CHECKOUT);
    }

    @Test
    @Transactional
    void getAllBookingsByCheckoutIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where checkout is greater than DEFAULT_CHECKOUT
        defaultBookingShouldNotBeFound("checkout.greaterThan=" + DEFAULT_CHECKOUT);

        // Get all the bookingList where checkout is greater than SMALLER_CHECKOUT
        defaultBookingShouldBeFound("checkout.greaterThan=" + SMALLER_CHECKOUT);
    }

    @Test
    @Transactional
    void getAllBookingsByNumOfGuestsIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where numOfGuests equals to DEFAULT_NUM_OF_GUESTS
        defaultBookingShouldBeFound("numOfGuests.equals=" + DEFAULT_NUM_OF_GUESTS);

        // Get all the bookingList where numOfGuests equals to UPDATED_NUM_OF_GUESTS
        defaultBookingShouldNotBeFound("numOfGuests.equals=" + UPDATED_NUM_OF_GUESTS);
    }

    @Test
    @Transactional
    void getAllBookingsByNumOfGuestsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where numOfGuests not equals to DEFAULT_NUM_OF_GUESTS
        defaultBookingShouldNotBeFound("numOfGuests.notEquals=" + DEFAULT_NUM_OF_GUESTS);

        // Get all the bookingList where numOfGuests not equals to UPDATED_NUM_OF_GUESTS
        defaultBookingShouldBeFound("numOfGuests.notEquals=" + UPDATED_NUM_OF_GUESTS);
    }

    @Test
    @Transactional
    void getAllBookingsByNumOfGuestsIsInShouldWork() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where numOfGuests in DEFAULT_NUM_OF_GUESTS or UPDATED_NUM_OF_GUESTS
        defaultBookingShouldBeFound("numOfGuests.in=" + DEFAULT_NUM_OF_GUESTS + "," + UPDATED_NUM_OF_GUESTS);

        // Get all the bookingList where numOfGuests equals to UPDATED_NUM_OF_GUESTS
        defaultBookingShouldNotBeFound("numOfGuests.in=" + UPDATED_NUM_OF_GUESTS);
    }

    @Test
    @Transactional
    void getAllBookingsByNumOfGuestsIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where numOfGuests is not null
        defaultBookingShouldBeFound("numOfGuests.specified=true");

        // Get all the bookingList where numOfGuests is null
        defaultBookingShouldNotBeFound("numOfGuests.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingsByNumOfGuestsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where numOfGuests is greater than or equal to DEFAULT_NUM_OF_GUESTS
        defaultBookingShouldBeFound("numOfGuests.greaterThanOrEqual=" + DEFAULT_NUM_OF_GUESTS);

        // Get all the bookingList where numOfGuests is greater than or equal to UPDATED_NUM_OF_GUESTS
        defaultBookingShouldNotBeFound("numOfGuests.greaterThanOrEqual=" + UPDATED_NUM_OF_GUESTS);
    }

    @Test
    @Transactional
    void getAllBookingsByNumOfGuestsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where numOfGuests is less than or equal to DEFAULT_NUM_OF_GUESTS
        defaultBookingShouldBeFound("numOfGuests.lessThanOrEqual=" + DEFAULT_NUM_OF_GUESTS);

        // Get all the bookingList where numOfGuests is less than or equal to SMALLER_NUM_OF_GUESTS
        defaultBookingShouldNotBeFound("numOfGuests.lessThanOrEqual=" + SMALLER_NUM_OF_GUESTS);
    }

    @Test
    @Transactional
    void getAllBookingsByNumOfGuestsIsLessThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where numOfGuests is less than DEFAULT_NUM_OF_GUESTS
        defaultBookingShouldNotBeFound("numOfGuests.lessThan=" + DEFAULT_NUM_OF_GUESTS);

        // Get all the bookingList where numOfGuests is less than UPDATED_NUM_OF_GUESTS
        defaultBookingShouldBeFound("numOfGuests.lessThan=" + UPDATED_NUM_OF_GUESTS);
    }

    @Test
    @Transactional
    void getAllBookingsByNumOfGuestsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where numOfGuests is greater than DEFAULT_NUM_OF_GUESTS
        defaultBookingShouldNotBeFound("numOfGuests.greaterThan=" + DEFAULT_NUM_OF_GUESTS);

        // Get all the bookingList where numOfGuests is greater than SMALLER_NUM_OF_GUESTS
        defaultBookingShouldBeFound("numOfGuests.greaterThan=" + SMALLER_NUM_OF_GUESTS);
    }

    @Test
    @Transactional
    void getAllBookingsByFinalPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where finalPrice equals to DEFAULT_FINAL_PRICE
        defaultBookingShouldBeFound("finalPrice.equals=" + DEFAULT_FINAL_PRICE);

        // Get all the bookingList where finalPrice equals to UPDATED_FINAL_PRICE
        defaultBookingShouldNotBeFound("finalPrice.equals=" + UPDATED_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllBookingsByFinalPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where finalPrice not equals to DEFAULT_FINAL_PRICE
        defaultBookingShouldNotBeFound("finalPrice.notEquals=" + DEFAULT_FINAL_PRICE);

        // Get all the bookingList where finalPrice not equals to UPDATED_FINAL_PRICE
        defaultBookingShouldBeFound("finalPrice.notEquals=" + UPDATED_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllBookingsByFinalPriceIsInShouldWork() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where finalPrice in DEFAULT_FINAL_PRICE or UPDATED_FINAL_PRICE
        defaultBookingShouldBeFound("finalPrice.in=" + DEFAULT_FINAL_PRICE + "," + UPDATED_FINAL_PRICE);

        // Get all the bookingList where finalPrice equals to UPDATED_FINAL_PRICE
        defaultBookingShouldNotBeFound("finalPrice.in=" + UPDATED_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllBookingsByFinalPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where finalPrice is not null
        defaultBookingShouldBeFound("finalPrice.specified=true");

        // Get all the bookingList where finalPrice is null
        defaultBookingShouldNotBeFound("finalPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingsByFinalPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where finalPrice is greater than or equal to DEFAULT_FINAL_PRICE
        defaultBookingShouldBeFound("finalPrice.greaterThanOrEqual=" + DEFAULT_FINAL_PRICE);

        // Get all the bookingList where finalPrice is greater than or equal to UPDATED_FINAL_PRICE
        defaultBookingShouldNotBeFound("finalPrice.greaterThanOrEqual=" + UPDATED_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllBookingsByFinalPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where finalPrice is less than or equal to DEFAULT_FINAL_PRICE
        defaultBookingShouldBeFound("finalPrice.lessThanOrEqual=" + DEFAULT_FINAL_PRICE);

        // Get all the bookingList where finalPrice is less than or equal to SMALLER_FINAL_PRICE
        defaultBookingShouldNotBeFound("finalPrice.lessThanOrEqual=" + SMALLER_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllBookingsByFinalPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where finalPrice is less than DEFAULT_FINAL_PRICE
        defaultBookingShouldNotBeFound("finalPrice.lessThan=" + DEFAULT_FINAL_PRICE);

        // Get all the bookingList where finalPrice is less than UPDATED_FINAL_PRICE
        defaultBookingShouldBeFound("finalPrice.lessThan=" + UPDATED_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllBookingsByFinalPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList where finalPrice is greater than DEFAULT_FINAL_PRICE
        defaultBookingShouldNotBeFound("finalPrice.greaterThan=" + DEFAULT_FINAL_PRICE);

        // Get all the bookingList where finalPrice is greater than SMALLER_FINAL_PRICE
        defaultBookingShouldBeFound("finalPrice.greaterThan=" + SMALLER_FINAL_PRICE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookingShouldBeFound(String filter) throws Exception {
        restBookingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(booking.getId().intValue())))
            .andExpect(jsonPath("$.[*].hotelId").value(hasItem(DEFAULT_HOTEL_ID.intValue())))
            .andExpect(jsonPath("$.[*].roomId").value(hasItem(DEFAULT_ROOM_ID.intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].checkin").value(hasItem(DEFAULT_CHECKIN.toString())))
            .andExpect(jsonPath("$.[*].checkout").value(hasItem(DEFAULT_CHECKOUT.toString())))
            .andExpect(jsonPath("$.[*].numOfGuests").value(hasItem(DEFAULT_NUM_OF_GUESTS)))
            .andExpect(jsonPath("$.[*].finalPrice").value(hasItem(DEFAULT_FINAL_PRICE.doubleValue())));

        // Check, that the count call also returns 1
        restBookingMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookingShouldNotBeFound(String filter) throws Exception {
        restBookingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookingMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBooking() throws Exception {
        // Get the booking
        restBookingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();

        // Update the booking
        Booking updatedBooking = bookingRepository.findById(booking.getId()).get();
        // Disconnect from session so that the updates on updatedBooking are not directly saved in db
        em.detach(updatedBooking);
        updatedBooking
            .hotelId(UPDATED_HOTEL_ID)
            .roomId(UPDATED_ROOM_ID)
            .userId(UPDATED_USER_ID)
            .checkin(UPDATED_CHECKIN)
            .checkout(UPDATED_CHECKOUT)
            .numOfGuests(UPDATED_NUM_OF_GUESTS)
            .finalPrice(UPDATED_FINAL_PRICE);

        restBookingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBooking.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBooking))
            )
            .andExpect(status().isOk());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
        Booking testBooking = bookingList.get(bookingList.size() - 1);
        assertThat(testBooking.getHotelId()).isEqualTo(UPDATED_HOTEL_ID);
        assertThat(testBooking.getRoomId()).isEqualTo(UPDATED_ROOM_ID);
        assertThat(testBooking.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testBooking.getCheckin()).isEqualTo(UPDATED_CHECKIN);
        assertThat(testBooking.getCheckout()).isEqualTo(UPDATED_CHECKOUT);
        assertThat(testBooking.getNumOfGuests()).isEqualTo(UPDATED_NUM_OF_GUESTS);
        assertThat(testBooking.getFinalPrice()).isEqualTo(UPDATED_FINAL_PRICE);
    }

    @Test
    @Transactional
    void putNonExistingBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
        booking.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, booking.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(booking))
            )
            .andExpect(status().isBadRequest());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
        booking.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(booking))
            )
            .andExpect(status().isBadRequest());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
        booking.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookingWithPatch() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();

        // Update the booking using partial update
        Booking partialUpdatedBooking = new Booking();
        partialUpdatedBooking.setId(booking.getId());

        partialUpdatedBooking.roomId(UPDATED_ROOM_ID).checkout(UPDATED_CHECKOUT).numOfGuests(UPDATED_NUM_OF_GUESTS);

        restBookingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBooking.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBooking))
            )
            .andExpect(status().isOk());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
        Booking testBooking = bookingList.get(bookingList.size() - 1);
        assertThat(testBooking.getHotelId()).isEqualTo(DEFAULT_HOTEL_ID);
        assertThat(testBooking.getRoomId()).isEqualTo(UPDATED_ROOM_ID);
        assertThat(testBooking.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testBooking.getCheckin()).isEqualTo(DEFAULT_CHECKIN);
        assertThat(testBooking.getCheckout()).isEqualTo(UPDATED_CHECKOUT);
        assertThat(testBooking.getNumOfGuests()).isEqualTo(UPDATED_NUM_OF_GUESTS);
        assertThat(testBooking.getFinalPrice()).isEqualTo(DEFAULT_FINAL_PRICE);
    }

    @Test
    @Transactional
    void fullUpdateBookingWithPatch() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();

        // Update the booking using partial update
        Booking partialUpdatedBooking = new Booking();
        partialUpdatedBooking.setId(booking.getId());

        partialUpdatedBooking
            .hotelId(UPDATED_HOTEL_ID)
            .roomId(UPDATED_ROOM_ID)
            .userId(UPDATED_USER_ID)
            .checkin(UPDATED_CHECKIN)
            .checkout(UPDATED_CHECKOUT)
            .numOfGuests(UPDATED_NUM_OF_GUESTS)
            .finalPrice(UPDATED_FINAL_PRICE);

        restBookingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBooking.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBooking))
            )
            .andExpect(status().isOk());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
        Booking testBooking = bookingList.get(bookingList.size() - 1);
        assertThat(testBooking.getHotelId()).isEqualTo(UPDATED_HOTEL_ID);
        assertThat(testBooking.getRoomId()).isEqualTo(UPDATED_ROOM_ID);
        assertThat(testBooking.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testBooking.getCheckin()).isEqualTo(UPDATED_CHECKIN);
        assertThat(testBooking.getCheckout()).isEqualTo(UPDATED_CHECKOUT);
        assertThat(testBooking.getNumOfGuests()).isEqualTo(UPDATED_NUM_OF_GUESTS);
        assertThat(testBooking.getFinalPrice()).isEqualTo(UPDATED_FINAL_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
        booking.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, booking.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(booking))
            )
            .andExpect(status().isBadRequest());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
        booking.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(booking))
            )
            .andExpect(status().isBadRequest());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
        booking.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        int databaseSizeBeforeDelete = bookingRepository.findAll().size();

        // Delete the booking
        restBookingMockMvc
            .perform(delete(ENTITY_API_URL_ID, booking.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
