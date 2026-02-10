package com.example.demo.service.specifications;

import com.example.demo.dto.UserFilterDTO;
import com.example.demo.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterUsers(UserFilterDTO filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return null;
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"
                ));
            }

            if (filter.getSurname() != null && !filter.getSurname().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("surname")),
                        "%" + filter.getSurname().toLowerCase() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}