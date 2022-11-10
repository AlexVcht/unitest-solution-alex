package com.kindredgroup.unibetlivetest.batchs;


import com.kindredgroup.unibetlivetest.model.entity.Bet;
import com.kindredgroup.unibetlivetest.model.entity.Customer;
import com.kindredgroup.unibetlivetest.model.entity.Selection;
import com.kindredgroup.unibetlivetest.model.types.BetState;
import com.kindredgroup.unibetlivetest.model.types.SelectionResult;
import com.kindredgroup.unibetlivetest.model.types.SelectionState;
import com.kindredgroup.unibetlivetest.service.BetService;
import com.kindredgroup.unibetlivetest.service.CustomerService;
import com.kindredgroup.unibetlivetest.service.SelectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Log4j2
@RequiredArgsConstructor
public class
MarketBatch {

    private final BetService betService;
    private final SelectionService selectionService;
    private final CustomerService customerService;

    /**
     * Batch pour payer les paris gagant
     * 1. Récuperation des données (selections, bet et customers)
     * 2. Mise à jour des états des bets
     * 3. Mise à jour de la balance des utilisateurs ayant des paris gagnants
     */
    @Scheduled(fixedRateString = "${batch.rate.pay}")
    public void payBets() {
        long timer = System.currentTimeMillis();
        final List<Selection> selections = selectionService.getSelectionsByStateAndResult(SelectionState.CLOSED, SelectionResult.WON);

        if (selections.isEmpty()) {
            log.info("No closed selections found. ({} ms)", System.currentTimeMillis() - timer);
            return;
        }

        final List<Long> selectionIds = selections.stream().map(Selection::getId).toList();
        final List<Bet> bets = betService.getBetBySelections(selectionIds);

        if (bets.isEmpty()) {
            log.info("No bets founds. ({} ms)", System.currentTimeMillis() - timer);
            return;
        }

        // Here we only update won bets but we could also handle lost one
        final List<Bet> betsUpdated = updateBets(selections, bets);
        final List<Long> customerIds = betsUpdated.stream()
                .map(b -> b.getCustomer().getId())
                .distinct()
                .toList();

        final List<Bet> betsSaved = betService.saveBets(betsUpdated);
        final List<Customer> customers = customerService.findCustomerByIds(customerIds);
        final List<Customer> customerUpdated = updateBalance(customers, betsUpdated, selections);
        final List<Customer> customerSaved = customerService.saveCustomers(customerUpdated);

        log.info("{} bets payed for {} customers. ({} ms)", betsSaved.size(), customerSaved.size(), System.currentTimeMillis() - timer);
    }

    /**
     * Mise à jour des bets gagnés
     */
    private List<Bet> updateBets(List<Selection> selections, List<Bet> bets) {
        final List<Long> selectionIdsWon = selections.stream()
                .filter(s -> Objects.equals(SelectionResult.WON, s.getResult()))
                .map(Selection::getId)
                .toList();

        return bets.stream()
                .filter(b -> {
                    if (b.getSelection() != null && b.getSelection().getId() != null) {
                        return selectionIdsWon.contains(b.getSelection().getId());
                    }
                    return false;
                })
                .map(b -> b.setBetState(BetState.WON))
                .toList();
    }

    /**
     * Mise à jour de la balace de chaque utilisateur
     */
    private List<Customer> updateBalance(List<Customer> customers, List<Bet> betsUpdated, List<Selection> selections) {
        return customers.stream().map(c -> {
            BigDecimal balanceToAdd = new BigDecimal("0");
            final List<Bet> customerBets = betsUpdated.stream()
                    .filter(b -> {
                        if (b.getCustomer() != null) {
                            return b.getCustomer().getId().equals(c.getId());
                        }
                        return false;
                    }).toList();

            for (Bet b : customerBets) {
                final Optional<Selection> selectionOpt = selections.stream()
                        .filter(s -> Objects.equals(s.getId(), b.getSelection().getId()))
                        .findFirst();
                if (selectionOpt.isPresent()) {
                    final BigDecimal currentOdd = selectionOpt.get().getCurrentOdd();
                    balanceToAdd = balanceToAdd.add(currentOdd.multiply(b.getMise()));
                }
            }

            // In case customer has no balance initialized
            if (c.getBalance() != null) {
                return c.setBalance(c.getBalance().add(balanceToAdd));
            }
            return c.setBalance(balanceToAdd);
        }).toList();
    }

}
