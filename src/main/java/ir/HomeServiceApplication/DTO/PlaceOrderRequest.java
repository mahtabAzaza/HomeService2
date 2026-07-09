package ir.HomeServiceApplication.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class PlaceOrderRequest {

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Price offer is required")
    private Long priceOffer;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime orderStartDateTime;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Description is required")
    private String orderDescription;

    public PlaceOrderRequest() {}

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public Long getPriceOffer() { return priceOffer; }
    public void setPriceOffer(Long priceOffer) { this.priceOffer = priceOffer; }

    public LocalDateTime getOrderStartDateTime() { return orderStartDateTime; }
    public void setOrderStartDateTime(LocalDateTime orderStartDateTime) { this.orderStartDateTime = orderStartDateTime; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getOrderDescription() { return orderDescription; }
    public void setOrderDescription(String orderDescription) { this.orderDescription = orderDescription; }
}
