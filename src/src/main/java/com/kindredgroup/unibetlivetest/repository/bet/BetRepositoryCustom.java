package com.kindredgroup.unibetlivetest.repository.bet;

import com.kindredgroup.unibetlivetest.model.entity.Bet;
import com.kindredgroup.unibetlivetest.model.types.SelectionState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BetRepositoryCustom {

    @Query("""
            SELECT b
            FROM Bet b
            JOIN Selection s on (s.id = b.selection.id)
            WHERE s.state = :selectionState
            """)
    List<Bet> findBetBySelectionState(@Param("selectionState") SelectionState selectionState);
}
