package carsharingapp.controller;

import carsharingapp.dto.payment.CreatePaymentRequestDto;
import carsharingapp.dto.payment.PaymentDto;
import carsharingapp.model.User;
import carsharingapp.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Payment management", description = "Endpoints for payment management")
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Get all payments by status", description = "Get all payments by status")
    @ResponseBody
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/status")
    public List<PaymentDto> getAllPaymentsByStatus(@RequestParam(name = "status") String status,
                                   @PageableDefault(page = 0, size = 10) Pageable pageable,
                                   Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return paymentService.getPaymentsByStatus(user.getId(), status, pageable);
    }

    @Operation(summary = "Get all user's payments", description = "Get all users payments")
    @ResponseBody
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public List<PaymentDto> getAllPayments(@PageableDefault(page = 0, size = 10) Pageable pageable,
                                                   Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return paymentService.getPayments(user.getId(), pageable);
    }

    @Operation(summary = "Create payment session", description = "Create session")
    @ResponseBody
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/pay")
    public PaymentDto createSession(@RequestBody @Valid CreatePaymentRequestDto requestDto) {
        return paymentService.createPaymentSession(requestDto);
    }

    @Operation(summary = "Successful payment",
            description = "Redirection to successful payment page")
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/success")
    public String success(@RequestParam String sessionId) {
        paymentService.getSuccessfulPayment(sessionId);
        return "success";
    }

    @Operation(summary = "Canceled payment", description = "Redirection to canceled payment page")
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/cancel")
    public String cancel(@RequestParam String sessionId) {
        paymentService.getCancelledPayment(sessionId);
        return "canceled";
    }
}
