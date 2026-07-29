package ir.HomeServiceApplication.DTO;

public record PaymentLinkResponseDto(
        String token,
        String paymentUrl
) {
}
