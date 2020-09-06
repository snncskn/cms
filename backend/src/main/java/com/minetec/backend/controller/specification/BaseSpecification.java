package com.minetec.backend.controller.specification;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author Sinan
 */

public abstract class BaseSpecification<E> {

    private final String wildcard = "%";

    protected String containsLowerCase(final String searchField) {
        return wildcard + searchField.toLowerCase() + wildcard;
    }

    protected Specification<E> contains(final String attribute, final String value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }

            return cb.like(
                cb.lower(root.get(attribute)),
                containsLowerCase(value)
            );
        };
    }

    protected Specification<E> equals(final String attribute, final String value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }

            return cb.equal(
                cb.lower(root.get(attribute)),
                containsLowerCase(value)
            );
        };
    }

    protected Specification<E> equals(final String attribute, final boolean value) {
        return (root, query, cb) -> {
            return cb.equal(root.get(attribute), value);
        };
    }

}
