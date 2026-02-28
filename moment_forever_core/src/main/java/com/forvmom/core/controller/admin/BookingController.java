package com.forvmom.core.controller.admin;

import com.forvmom.common.dto.request.BookingRequestDto;
import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.core.services.BookingOrchestrationService;
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
 * dashboard). All business logic lives in {@link BookingOrchestrationService}.
 */
@RestController
@RequestMapping("/api/admin/bookings")
@Tag(name = "Admin Booking API", description = "Admin endpoints for initiating and managing booking requests")
public class BookingController {

    private final BookingOrchestrationService bookingOrchestrationService;

    public BookingController(BookingOrchestrationService bookingOrchestrationService) {
        this.bookingOrchestrationService = bookingOrchestrationService;
    }

    @PostMapping
    @Operation(summary = "Create Booking Request (Admin)", description = "Reserves capacity atomically, writes an outbox record, then asynchronously enriches and publishes a booking-requested event to Kafka. Returns the generated booking reference.")
    public ResponseEntity<ApiResponse<?>> createBooking(
            @Valid @RequestBody BookingRequestDto bookingRequestDto) {

        String bookingId = bookingOrchestrationService.initiateBooking(bookingRequestDto);

        Map<String, Object> data = Map.of(
                "bookingId", bookingId,
                "status", "PENDING",
                "message", "Your booking request has been received and is being processed.");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ResponseUtil.buildOkResponse(data, "Booking request accepted"));
    }
}