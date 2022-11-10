package com.kindredgroup.unibetlivetest.service;

import com.kindredgroup.unibetlivetest.api.body.AddBetBody;
import com.kindredgroup.unibetlivetest.model.entity.Bet;
import com.kindredgroup.unibetlivetest.model.entity.Customer;
import com.kindredgroup.unibetlivetest.model.entity.Selection;
import com.kindredgroup.unibetlivetest.model.exception.CustomException;
import com.kindredgroup.unibetlivetest.model.types.ExceptionType;
import com.kindredgroup.unibetlivetest.model.types.SelectionState;
import com.kindredgroup.unibetlivetest.repository.CustomerRepository;
import com.kindredgroup.unibetlivetest.repository.bet.BetRepository;
import com.kindredgroup.unibetlivetest.repository.selection.SelectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Log4j2
@Service
@RequiredArgsConstructor
public class BetService {

    private final SelectionRepository selectionRepository;
    private final CustomerRepository customerRepository;

    private final BetRepository betRepository;
    
    public Long addBet(AddBetBody addBetBody) {
        final String selectionId = addBetBody.selectionId();
        final Optional<Selection> selectionOpt = selectionRepository.findById(Long.valueOf(selectionId));

        if (selectionOpt.isEmpty()) {
            throw new CustomException(format("Selection not found : %s", selectionId), ExceptionType.SELECTION_NOT_FOUND);
        }

        final Customer customer = customerRepository.getCustomerByPseudo("unibest")
                .orElseThrow(() -> new CustomException(format("customer %s not found", "unibest"), ExceptionType.CUSTOMER_NOT_FOUND));

        checkIfSelectionIsOpen(selectionOpt.get());
        checkOdd(selectionOpt.get(), addBetBody.cote());
        checkBalance(customer, addBetBody.mise());
        checkBetAlreadyPlaced(customer.getId(), selectionId);
        return saveBetAndUpdateBalance(customer, selectionOpt.get(), addBetBody);
    }

    public List<Bet> getBetBySelections(List<Long> selectionIds) {
        return betRepository.findBySelectionIdInAndBetStateIsNull(selectionIds);
    }

    public List<Bet> saveBets(List<Bet> betsUpdated) {
        return betRepository.saveAll(betsUpdated);
    }

    private void checkIfSelectionIsOpen(Selection selection) {
        if (selection.getState().equals(SelectionState.CLOSED)) {
            throw new CustomException(format("Closed selection : %s - %s", selection.getId(), selection.getName()), ExceptionType.CLOSED_SELECTION);
        }
    }

    private void checkOdd(Selection selection, BigDecimal cote) {
        if (selection.getCurrentOdd().compareTo(cote) != 0) {
            throw new CustomException("Odds has been updated", ExceptionType.UPDATED_ODD);
        }
    }

    private void checkBalance(Customer customer, BigDecimal mise) {
        if (mise.compareTo(customer.getBalance()) > 0) {
            throw new CustomException(format("Not enough balance (%s) compare to your mise (%s)", customer.getBalance(), mise), ExceptionType.NOT_ENOUGH_BALANCE);
        }
    }

    /**
     * Un joueur ne peut prendre qu'un seul paris sur une selection
     */
    private void checkBetAlreadyPlaced(Long customerId, String selectionId) {
        betRepository.findByCustomerIdAndSelectionId(customerId, Long.parseLong(selectionId))
                .ifPresent(b -> {
                    throw new CustomException(format("Bet already placed for this selection. Bet id : %s", b.getId()), ExceptionType.BET_ALREAD_PLACED);
                });
    }

    /**
     * 1. Sauvegarde du paris
     * 2. Déduction de la mise sur la balance de l'utilisateur
     */
    private Long saveBetAndUpdateBalance(Customer customer, Selection selection, AddBetBody addBetBody) {
        final Bet bet = new Bet().setDate(new Date())
                .setMise(addBetBody.mise())
                .setCustomer(customer)
                .setSelection(selection);
        final Bet savedBet = betRepository.save(bet);
        log.info("Bet saved with id : {}", savedBet.getId());

        final Customer updatedCustomer = customer.setBalance(customer.getBalance().add(addBetBody.mise().negate()));
        customerRepository.save(updatedCustomer);
        log.info("Balance updated");

        return savedBet.getId();
    }

}
