package ir.HomeServiceApplication.specification;

import ir.HomeServiceApplication.entity.Role;
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.entity.User;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasRole(Role role) {
        return (root, query, cb) ->
                role == null ? null : cb.equal(root.get("role"), role);
    }

    public static Specification<User> firstNameContains(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.isBlank()) return null;
            return cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
        };
    }

    public static Specification<User> lastNameContains(String lastName) {
        return (root, query, cb) -> {
            if (lastName == null || lastName.isBlank()) return null;
            return cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasServiceNamed(String serviceName) {
        return (root, query, cb) -> {
            if (serviceName == null || serviceName.isBlank()) return null;
            query.distinct(true);
            var specialist = cb.treat(root, Specialist.class);
            var serviceJoin = specialist.join("services", JoinType.INNER);
            return cb.like(cb.lower(serviceJoin.get("serviceName")), "%" + serviceName.toLowerCase() + "%");
        };
    }

    public static Specification<User> scoreAtLeast(Integer min) {
        return (root, query, cb) -> {
            if (min == null) return null;
            var specialist = cb.treat(root, Specialist.class);
            return cb.greaterThanOrEqualTo(specialist.get("Score"), min);
        };
    }

    public static Specification<User> scoreAtMost(Integer max) {
        return (root, query, cb) -> {
            if (max == null) return null;
            var specialist = cb.treat(root, Specialist.class);
            return cb.lessThanOrEqualTo(specialist.get("Score"), max);
        };
    }
}
