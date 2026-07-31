package ir.HomeServiceApplication.specification;

import ir.HomeServiceApplication.DTO.OrderHistoryDto;
import ir.HomeServiceApplication.entity.Order;
import ir.HomeServiceApplication.entity.Proposal;
import ir.HomeServiceApplication.entity.OrderStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

public class OrderSpecification {

    public static Specification<Order> fromFilter(OrderHistoryDto filter) {

        return Stream.of(
                        customerIdEquals(filter.customerId()),
                        specialistIdEquals(filter.specialistId()),
                        statusEquals(filter.status()),
                        serviceIdEquals(filter.serviceId()),
                        priceAtLeast(filter.proposedPrice()),
                        createdAtBetween(filter.dateFrom(), filter.dateTo())
                )
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());
    }


    private static Specification<Order> createdAtBetween(
            LocalDateTime dateFrom,
            LocalDateTime dateTo
    ) {

        if (dateFrom != null && dateTo != null) {

            return (root, query, cb) ->
                    cb.between(
                            root.get("orderRegisterDate"),
                            dateFrom,
                            dateTo
                    );

        } else if (dateFrom != null) {

            return (root, query, cb) ->
                    cb.greaterThanOrEqualTo(
                            root.get("orderRegisterDate"),
                            dateFrom
                    );

        } else if (dateTo != null) {

            return (root, query, cb) ->
                    cb.lessThanOrEqualTo(
                            root.get("orderRegisterDate"),
                            dateTo
                    );
        }

        return null;
    }


    private static Specification<Order> priceAtLeast(Long minPrice) {

        return minPrice == null ? null :
                (root, query, cb) ->
                        cb.greaterThanOrEqualTo(
                                root.get("proposedPrice"),
                                minPrice
                        );
    }


    private static Specification<Order> serviceIdEquals(Long serviceId) {

        return serviceId == null ? null :
                (root, query, cb) ->
                        cb.equal(
                                root.get("service").get("id"),
                                serviceId
                        );
    }


    private static Specification<Order> statusEquals(OrderStatus status) {

        return status == null ? null :
                (root, query, cb) ->
                        cb.equal(
                                root.get("status"),
                                status
                        );
    }


    private static Specification<Order> specialistIdEquals(Long specialistId) {

        return specialistId == null ? null :
                (root, query, cb) -> {

                    Join<Order, Proposal> join =
                            root.join("proposals");

                    query.distinct(true);

                    return cb.equal(
                            join.get("specialist").get("id"),
                            specialistId
                    );
                };
    }


    private static Specification<Order> customerIdEquals(Long customerId) {

        return customerId == null ? null :
                (root, query, cb) ->
                        cb.equal(
                                root.get("customer").get("id"),
                                customerId
                        );
    }
}