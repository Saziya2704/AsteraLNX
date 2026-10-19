package com.newasteralnx.specification;

import com.newasteralnx.entity.Customer;
import com.newasteralnx.entity.CustomerStatus;
import org.springframework.data.jpa.domain.Specification;

public final class CustomerSpecifications {

    private CustomerSpecifications() {
    }

    public static Specification<Customer> from(String search, CustomerStatus status) {
        return (root, query, cb) -> {
            var predicate = cb.isFalse(root.get("deleted"));
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim().toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("companyName")), like),
                        cb.like(cb.lower(root.get("email")), like),
                        cb.like(cb.lower(root.get("customerCode")), like),
                        cb.like(cb.lower(root.get("contactPerson")), like)
                ));
            }
            return predicate;
        };
    }
}
