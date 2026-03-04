//package com.forvmom.core.controller.pub;
//
//import com.forvmom.common.dto.request.BookingRequestDto;
//import com.forvmom.common.response.ApiResponse;
//import com.forvmom.common.response.ResponseUtil;
//import com.forvmom.core.services.BookingOrchestrationService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
///**
// * User-facing booking controller.
// *
// * <p>
// * Requires a valid JWT (USER role). Authenticated user identity is resolved
// * inside {@link BookingOrchestrationService} via {@code SecurityContextHolder}.
// *
// * <p>
// * Flow: validate request → atomic inventory++ → write outbox (same TX) → 202 →
// * async enrich → publish {@code booking-requested} Kafka event.
// */
//@RestController
//@RequestMapping("/public/bookings")
//@Tag(name = "Booking API", description = "Endpoints for creating booking requests")
//public class BookingControllerPublic {
//
//        private final BookingOrchestrationService bookingOrchestrationService;
//
//        public BookingControllerPublic(BookingOrchestrationService bookingOrchestrationService) {
//                this.bookingOrchestrationService = bookingOrchestrationService;
//        }
//
//        /**
//         * Initiates a booking request for the authenticated user.
//         *
//         * <p>
//         * The booking is not confirmed immediately — capacity is reserved atomically,
//         * then a Kafka event is published asynchronously by the enrichment task.
//         *
//         * @param bookingRequestDto the booking request payload
//         * @return HTTP 202 with the generated booking reference ID
//         */
//        @PostMapping
//        @Operation(summary = "Create Booking Request", description = "Reserves capacity atomically, writes an outbox record, then asynchronously "
//                        + "enriches and publishes the booking-requested event. Returns a booking reference ID immediately. "
//                        + "Requires USER authentication.")
//        public ResponseEntity<ApiResponse<?>> createBooking(
//                        @Valid @RequestBody BookingRequestDto bookingRequestDto) {
//
//                String bookingId = bookingOrchestrationService.initiateBooking(bookingRequestDto);
//
//                Map<String, Object> data = Map.of(
//                                "bookingId", bookingId,
//                                "status", "PENDING",
//                                "message", "Your booking request has been received and is being processed.");
//
//                return ResponseEntity
//                                .status(HttpStatus.ACCEPTED)
//                                .body(ResponseUtil.buildOkResponse(data, "Booking request accepted"));
//        }
//}
