package com.forvmom.core.controller.pub;

import com.forvmom.common.dto.request.BookingRequestDto;
import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.core.services.BookingPublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * User-facing booking controller.
 *
 * <p>
 * Requires a valid JWT (USER role). Authenticated user identity is resolved
 * inside {@link BookingPublisherService} via {@code SecurityContextHolder}.
 *
 * <p>
 * Flow: validate request → resolve pricing → resolve add-ons →
 * publish {@code booking-requested} Kafka event → return booking reference.
 */
@RestController
@RequestMapping("/public/bookings")
@Tag(name = "Booking API", description = "Endpoints for creating booking requests")
public class BookingControllerPublic {

    private final BookingPublisherService bookingPublisherService;

    public BookingControllerPublic(BookingPublisherService bookingPublisherService) {
        this.bookingPublisherService = bookingPublisherService;
    }

    /**
     * Initiates a booking request for the authenticated user.
     *
     * <p>
     * The booking is not confirmed immediately — it is published as a Kafka
     * event to the {@code booking-requested} topic and processed asynchronously
     * by the booking microservice.
     *
     * @param bookingRequestDto the booking request payload
     * @return HTTP 202 with the generated booking reference ID
     */
    @PostMapping
    @Operation(summary = "Create Booking Request", description = "Validates the booking selection, resolves the final price, and publishes a "
            + "booking-requested event. Returns a booking reference ID to track the request. "
            + "Requires USER authentication.")
    public ResponseEntity<ApiResponse<?>> createBooking(
            @Valid @RequestBody BookingRequestDto bookingRequestDto) {

        String bookingId = bookingPublisherService.publishBookingRequest(bookingRequestDto);

        Map<String, Object> data = Map.of(
                "bookingId", bookingId,
                "status", "PENDING",
                "message", "Your booking request has been received and is being processed.");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ResponseUtil.buildOkResponse(data, "Booking request accepted"));
    }
}
