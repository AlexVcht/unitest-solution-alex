package com.kindredgroup.unibetlivetest.service;

import com.kindredgroup.unibetlivetest.api.display.EventDisplay;
import com.kindredgroup.unibetlivetest.mapper.EventMapper;
import com.kindredgroup.unibetlivetest.model.entity.Event;
import com.kindredgroup.unibetlivetest.repository.event.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    /**
     * On a supposé que tous les évents duraient 5 min car il n'y a pas de date de fin d'event
     */
    public List<EventDisplay> getEvents(Boolean isLive) {
        final List<Event> events = new ArrayList<>();

        if (isLive == null || !isLive) {
            final List<Event> allEvent = eventRepository.findAll();
            events.addAll(allEvent);
        } else {
            final List<Event> allEvent = eventRepository.findAllEventLive();
            final List<Event> eventLive = allEvent.stream()
                    // 5 minutes = 300000 ms
                    .filter(e -> new Date().getTime() - e.getStartDate().getTime() <= 300000)
                    .toList();
            events.addAll(eventLive);
        }

        return EventMapper.entitiesToDisplay(events);
    }

}
