package ru.practicum.ewm.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.event.SearchFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SearchSpecificationBuilder {


    public List<Specification<Event>> searchFilterToAdminSpecifications(SearchFilter filter) {
        List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add(filter.getUsers() != null ? searchByUsers(filter.getUsers()) : null);
        specifications.add(filter.getStates() != null ? searchByStates(filter.getStates()) : null);
        specifications.add(filter.getCategories() != null ? searchByCategories(filter.getCategories()) : null);
        specifications.add(filter.getRangeStart() != null ?
                searchByRange(filter.getRangeStart(), filter.getRangeEnd()) : searchByNullRange());

        return specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Specification<Event>> searchFilterToSpecifications(SearchFilter filter) {
        List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add(filter.getText() != null ? searchByText(filter.getText()) : null);
        specifications.add(filter.getCategories() != null ? searchByCategories(filter.getCategories()) : null);
        specifications.add(filter.getPaid() != null ? searchByPaid(filter.getPaid()) : null);
        specifications.add(filter.getRangeStart() != null ?
                searchByRange(filter.getRangeStart(), filter.getRangeEnd()) : searchByNullRange());
        specifications.add(filter.getOnlyAvailable().equals(true) ? searchByAvailable(filter.getOnlyAvailable()) : null);
        specifications.add(searchByState(filter.getState()));
        specifications.add(searchSort(filter.getSort()));

        return specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Specification<Event> searchByUsers(List<Long> users) {
        return (root, query, cb) -> cb.in(root.get("initiator").get("id")).value(users);
    }

    private Specification<Event> searchByStates(List<EventState> states) {
        return (root, query, cb) -> cb.in(root.get("state")).value(states);
    }

    private Specification<Event> searchByText(String text) {
        return (root, query, cb) -> cb
                .or(cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%"));
    }

    private Specification<Event> searchByCategories(List<Long> categories) {
        return (root, query, cb) -> cb.in(root.get("category").get("id")).value(categories);
    }

    private Specification<Event> searchByPaid(Boolean paid) {
        return (root, query, cb) -> cb.equal(root.get("paid"), paid);
    }

    private Specification<Event> searchByRange(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> cb.between(root.get("eventDate"), start, end);
    }

    private Specification<Event> searchByNullRange() {
        return (root, query, cb) -> cb.between(root.get("eventDate"), LocalDateTime.now(), LocalDateTime.now().plusYears(99));
    }

    private Specification<Event> searchByAvailable(Boolean available) {
        return (root, query, cb) -> cb.equal(root.get("available"), available);
    }

    private Specification<Event> searchByState(EventState state) {
        return (root, query, cb) -> cb.equal(root.get("state"), state);
    }

    private Specification<Event> searchSort(String sort) {
        return (root, query, cb) -> {
            switch (sort) {
                case "VIEWS":
                    return query.orderBy(cb.asc(root.get("views"))).getRestriction();
                case "EVENT_DATE":
                default:
                    return query.orderBy(cb.asc(root.get("eventDate"))).getRestriction();
            }
        };
    }

}
