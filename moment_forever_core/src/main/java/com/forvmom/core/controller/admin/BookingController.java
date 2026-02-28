package com.forvmom.core.controller.admin;

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
 * Admin-facing booking controller.
 *
 * <p>
 * Mirrors the user-facing {@code /public/bookings} endpoint so that admins
 * can also initiate booking requests on behalf of users (e.g. via the admin
 * dashboard). All business logic lives in {@link BookingPublisherService}.
 */
@RestController
@RequestMapping("/api/admin/bookings")
@Tag(name = "Admin Booking API", description = "Admin endpoints for initiating and managing booking requests")
public class BookingController {

    private final BookingPublisherService bookingPublisherService;

    public BookingController(BookingPublisherService bookingPublisherService) {
        this.bookingPublisherService = bookingPublisherService;
    }

    @PostMapping
    @Operation(summary = "Create Booking Request (Admin)", description = "Validates the booking, resolves pricing, and publishes a booking-requested event to Kafka. Returns the generated booking reference.")
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