package ir.HomeServiceApplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "services")
public class Service extends BaseEntity<Long> {

    private String serviceName;

    private Long serviceBasePrice;

    private String serviceDescription;

    // دسته‌بندی کلی خدمت
    private String serviceCategory;

    // هر سفارش مربوط به یک خدمت است
    @OneToMany(mappedBy = "service")
    private List<Order> orders;

    // متخصصانی که این خدمت را انجام می‌دهند
    @ManyToMany(mappedBy = "services")
    private List<Specialist> specialists;

    // خدمت والد (برای زیرخدمت‌ها)
    @ManyToOne
    @JoinColumn(name = "parent_service_id")
    private Service parentService;

    // زیرخدمت‌های این خدمت
    @OneToMany(mappedBy = "parentService")
    private List<Service> childServices;
}
