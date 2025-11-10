package com.example.demo.service.specifications;

import com.example.demo.entity.User;
import com.example.demo.dto.UserFilterDTO;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasName(String name) {
        return (root, query, cb) -> name == null ? null : cb.equal(root.get("name"), name);
    }

    public static Specification<User> hasSurname(String surname) {
        return (root, query, cb) -> surname == null ? null : cb.equal(root.get("surname"), surname);
    }

    public static Specification<User> filterUsers(UserFilterDTO filter) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("name"), filter.getName()));
            }
            if (filter.getSurname() != null && !filter.getSurname().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("surname"), filter.getSurname()));
            }
            return predicate;
        };
    }
}