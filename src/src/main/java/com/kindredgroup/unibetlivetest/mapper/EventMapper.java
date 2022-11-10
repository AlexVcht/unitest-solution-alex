package com.kindredgroup.unibetlivetest.mapper;

import com.kindredgroup.unibetlivetest.api.display.EventDisplay;
import com.kindredgroup.unibetlivetest.model.entity.Event;

import java.util.List;

public class EventMapper {

    public static List<EventDisplay> entitiesToDisplay(List<Event> events) {
        return events.stream()
                .map(e -> new EventDisplay(e.getId(), e.getName(), e.getStartDate()))
                .toList();
    }

}
