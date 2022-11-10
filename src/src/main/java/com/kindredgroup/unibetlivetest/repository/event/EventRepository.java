package com.kindredgroup.unibetlivetest.repository.event;

import com.kindredgroup.unibetlivetest.model.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
            SELECT e
            FROM Event e
            """)
    List<Event> findAllEventLive();

}
