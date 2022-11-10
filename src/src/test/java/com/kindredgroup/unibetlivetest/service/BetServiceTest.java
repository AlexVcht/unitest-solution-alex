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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

public class BetServiceTest {

    @InjectMocks
    BetService betService;
    @Mock
    SelectionRepository selectionRepository;
    @Mock
    CustomerRepository customerRepository;
    @Mock
    BetRepository betRepository;
    private AutoCloseable closeable;

    @BeforeEach
    void init() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void addBet_shouldWork() {
        // Given
        final String selectionId = "1";
        final AddBetBody addBetBody = new AddBetBody(selectionId, BigDecimal.valueOf(1.50), BigDecimal.valueOf(25));
        final Selection selection = new Selection()
                .setName("Unibet IT gagne")
                .setState(SelectionState.OPENED)
                .setCurrentOdd(new BigDecimal("1.5"));
        final Long customerId = 1L;
        final Customer customer = new Customer()
                .setId(customerId)
                .setPseudo("unibest")
                .setBalance(new BigDecimal("50"));
        final Bet saveBet = new Bet().setId(1L);

        // When
        Mockito.when(selectionRepository.findById(Long.valueOf(selectionId))).thenReturn(Optional.ofNullable(selection));
        Mockito.when(customerRepository.getCustomerByPseudo("unibest")).thenReturn(Optional.ofNullable(customer));
        Mockito.when(betRepository.findByCustomerIdAndSelectionId(customerId, Long.parseLong(selectionId))).thenReturn(Optional.empty());
        Mockito.when(betRepository.save(Mockito.any(Bet.class))).thenReturn(saveBet);

        betService.addBet(addBetBody);

        // Then
        Mockito.verify(betRepository, Mockito.times(1)).save(Mockito.any(Bet.class));
    }

    @Nested
    class checkExceptions {

        @Test
        public void addBet_withNoExistingSelection_shouldNotWork() {
            // Given
            final String selectionId = "1";
            final AddBetBody addBetBody = new AddBetBody(selectionId, BigDecimal.valueOf(1.50), BigDecimal.valueOf(10));

            // When
            Mockito.when(selectionRepository.findById(Long.valueOf(selectionId))).thenReturn(Optional.empty());
            Throwable throwable = catchThrowable(() -> betService.addBet(addBetBody));

            // Then
            Assertions.assertThat(throwable).isInstanceOf(CustomException.class);
            final ExceptionType exceptionType = ((CustomException) throwable).getException();
            Assertions.assertThat(exceptionType).isEqualTo(ExceptionType.SELECTION_NOT_FOUND);
        }

        @Test
        public void addBet_withClosedSelection_shouldNotWork() {
            // Given
            final String selectionId = "1";
            final AddBetBody addBetBody = new AddBetBody(selectionId, new BigDecimal("1.50"), new BigDecimal("10"));
            final Selection selection = new Selection()
                    .setName("Unibet IT gagne")
                    .setState(SelectionState.CLOSED)
                    .setCurrentOdd(new BigDecimal("20.7"));
            final Customer customer = new Customer()
                    .setPseudo("unibest")
                    .setBalance(new BigDecimal("50"));

            // When
            Mockito.when(selectionRepository.findById(Long.valueOf(selectionId))).thenReturn(Optional.ofNullable(selection));
            Mockito.when(customerRepository.getCustomerByPseudo("unibest")).thenReturn(Optional.ofNullable(customer));
            Throwable throwable = catchThrowable(() -> betService.addBet(addBetBody));

            // Then
            Assertions.assertThat(throwable).isInstanceOf(CustomException.class);
            final ExceptionType exceptionType = ((CustomException) throwable).getException();
            Assertions.assertThat(exceptionType).isEqualTo(ExceptionType.CLOSED_SELECTION);
        }

        @Test
        public void addBet_withDifferentOdd_shouldNotWork() {
            // Given
            final String selectionId = "1";
            final AddBetBody addBetBody = new AddBetBody(selectionId, BigDecimal.valueOf(1.50), BigDecimal.valueOf(10));
            final Selection selection = new Selection()
                    .setName("Unibet IT gagne")
                    .setState(SelectionState.OPENED)
                    .setCurrentOdd(new BigDecimal("20.7"));
            final Customer customer = new Customer()
                    .setPseudo("unibest")
                    .setBalance(new BigDecimal("50"));

            // When
            Mockito.when(selectionRepository.findById(Long.valueOf(selectionId))).thenReturn(Optional.ofNullable(selection));
            Mockito.when(customerRepository.getCustomerByPseudo("unibest")).thenReturn(Optional.ofNullable(customer));
            Throwable throwable = catchThrowable(() -> betService.addBet(addBetBody));

            // Then
            Assertions.assertThat(throwable).isInstanceOf(CustomException.class);
            final ExceptionType exceptionType = ((CustomException) throwable).getException();
            Assertions.assertThat(exceptionType).isEqualTo(ExceptionType.UPDATED_ODD);
        }

        @Test
        public void addBet_withNotEnoughBalance_shouldNotWork() {
            // Given
            final String selectionId = "1";
            final AddBetBody addBetBody = new AddBetBody(selectionId, BigDecimal.valueOf(1.50), BigDecimal.valueOf(100));
            final Selection selection = new Selection()
                    .setName("Unibet IT gagne")
                    .setState(SelectionState.OPENED)
                    .setCurrentOdd(new BigDecimal("1.5"));
            final Customer customer = new Customer()
                    .setPseudo("unibest")
                    .setBalance(new BigDecimal("50"));

            // When
            Mockito.when(selectionRepository.findById(Long.valueOf(selectionId))).thenReturn(Optional.ofNullable(selection));
            Mockito.when(customerRepository.getCustomerByPseudo("unibest")).thenReturn(Optional.ofNullable(customer));
            Throwable throwable = catchThrowable(() -> betService.addBet(addBetBody));

            // Then
            Assertions.assertThat(throwable).isInstanceOf(CustomException.class);
            final ExceptionType exceptionType = ((CustomException) throwable).getException();
            Assertions.assertThat(exceptionType).isEqualTo(ExceptionType.NOT_ENOUGH_BALANCE);
        }

        @Test
        public void addBet_withBetAlreadyPlaced_shouldNotWork() {
            // Given
            final String selectionId = "1";
            final AddBetBody addBetBody = new AddBetBody(selectionId, BigDecimal.valueOf(1.50), BigDecimal.valueOf(25));
            final Selection selection = new Selection()
                    .setName("Unibet IT gagne")
                    .setState(SelectionState.OPENED)
                    .setCurrentOdd(new BigDecimal("1.5"));
            Long customerId = 1L;
            final Customer customer = new Customer()
                    .setId(customerId)
                    .setPseudo("unibest")
                    .setBalance(new BigDecimal("50"));
            final Bet bet = new Bet().setId(11L);

            // When
            Mockito.when(selectionRepository.findById(Long.valueOf(selectionId))).thenReturn(Optional.ofNullable(selection));
            Mockito.when(customerRepository.getCustomerByPseudo("unibest")).thenReturn(Optional.ofNullable(customer));
            Mockito.when(betRepository.findByCustomerIdAndSelectionId(customerId, Long.parseLong(selectionId))).thenReturn(Optional.ofNullable(bet));
            Throwable throwable = catchThrowable(() -> betService.addBet(addBetBody));

            // Then
            Assertions.assertThat(throwable).isInstanceOf(CustomException.class);
            final ExceptionType exceptionType = ((CustomException) throwable).getException();
            Assertions.assertThat(exceptionType).isEqualTo(ExceptionType.BET_ALREAD_PLACED);
        }
    }

}
