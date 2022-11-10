package com.kindredgroup.unibetlivetest.repository.bet;

import com.kindredgroup.unibetlivetest.model.entity.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BetRepository extends BetRepositoryCustom, JpaRepository<Bet, Long> {
    Optional<Bet> findByCustomerIdAndSelectionId(Long customerId, Long selectionId);
    
    List<Bet> findBySelectionIdInAndBetStateIsNull(List<Long> selectionIds);
}
