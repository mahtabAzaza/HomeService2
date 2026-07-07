package ir.HomeServiceApplication.DTO;

import java.time.LocalDateTime;

public class PlaceOrderRequest {

    private Long serviceId;
    private Long priceOffer;
    private LocalDateTime orderStartDateTime;
    private String address;
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
