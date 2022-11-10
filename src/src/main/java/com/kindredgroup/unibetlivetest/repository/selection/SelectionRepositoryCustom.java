package com.kindredgroup.unibetlivetest.repository.selection;

import com.kindredgroup.unibetlivetest.model.entity.Selection;
import com.kindredgroup.unibetlivetest.model.types.SelectionState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SelectionRepositoryCustom {

    @Query("""
            SELECT s
            FROM Selection s
            JOIN Market m on (s.market.id = m.id)
            JOIN Event e on (e.id = m.event.id)
            WHERE e.id = :eventId
            """)
    List<Selection> getSelectionByEventId(@Param("eventId") Long eventId);

    @Query("""
            SELECT s
            FROM Selection s
            JOIN Market m on (s.market.id = m.id)
            JOIN Event e on (e.id = m.event.id)
            WHERE e.id = :eventId AND s.state = :state
            """)
    List<Selection> getSelectionByEventIdAndState(@Param("eventId") Long eventId, @Param("state") SelectionState state);

}
