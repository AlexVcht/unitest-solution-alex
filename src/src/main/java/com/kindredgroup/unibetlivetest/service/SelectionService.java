package com.kindredgroup.unibetlivetest.service;

import com.kindredgroup.unibetlivetest.api.display.SelectionDisplay;
import com.kindredgroup.unibetlivetest.mapper.SelectionMapper;
import com.kindredgroup.unibetlivetest.model.entity.Event;
import com.kindredgroup.unibetlivetest.model.entity.Selection;
import com.kindredgroup.unibetlivetest.model.exception.CustomException;
import com.kindredgroup.unibetlivetest.model.types.ExceptionType;
import com.kindredgroup.unibetlivetest.model.types.SelectionResult;
import com.kindredgroup.unibetlivetest.model.types.SelectionState;
import com.kindredgroup.unibetlivetest.repository.event.EventRepository;
import com.kindredgroup.unibetlivetest.repository.selection.SelectionRepository;
import com.kindredgroup.unibetlivetest.utils.Helpers;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.lang.String.format;

@RequiredArgsConstructor
@Component
@Log4j2
public class SelectionService {

    private final SelectionRepository selectionRepository;
    private final EventRepository eventRepository;

    /**
     * 1. Récupère toute les selections ouvertes
     * 2. Mis à jour la cote aléatoirement
     */
    public Long updateOddsRandomly() {
        final List<Selection> selectionsOpened = selectionRepository.getSelectionByStateEquals(SelectionState.OPENED);
        if (selectionsOpened.isEmpty()) {
            return 0L;
        }

        final List<Selection> selectionsUpdated = selectionsOpened.stream()
                .map(selection -> selection.setCurrentOdd(Helpers.updateOddRandomly(selection.getCurrentOdd())))
                .toList();

        final List<Selection> selectionsSaved = selectionRepository.saveAll(selectionsUpdated);
        return (long) selectionsSaved.size();
    }

    /**
     * 1. Récupère toute les selections ouvertes
     * 2. Ferme 5 sélections aléatoirement.
     */
    public Long closeOddsRandomly() {
        final List<Selection> selectionsOpened = selectionRepository.getSelectionByStateEquals(SelectionState.OPENED);
        if (selectionsOpened.isEmpty()) {
            return 0L;
        }

        final List<Selection> selectionsUpdated = IntStream.range(0, 5)
                .mapToObj(i -> {
                    final Selection selectionUpdated = selectionsOpened.get(Helpers.getRandomIndex(0, selectionsOpened.size()))
                            .setState(SelectionState.CLOSED)
                            .setResult(Helpers.setResultRandomly());
                    return selectionRepository.save(selectionUpdated);
                }).toList();

        final List<Selection> selectionsSaved = selectionRepository.saveAll(selectionsUpdated);
        return (long) selectionsSaved.size();
    }

    /**
     * Récupère les sélections d'un évenement selon un state optionnel
     */
    public List<SelectionDisplay> getSelections(Long eventId, SelectionState state) {
        final List<Selection> selectionEntities = new ArrayList<>();

        final Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new CustomException(format("Aucun évenement trouvé avec cet id : %s et ce state : %s", eventId, state), ExceptionType.EVENT_NOT_FOUND);
        }

        if (state == null) {
            selectionEntities.addAll(selectionRepository.getSelectionByEventId(eventId));
        } else {
            selectionEntities.addAll(selectionRepository.getSelectionByEventIdAndState(eventId, state));
        }

        return SelectionMapper.entitiesToDisplay(selectionEntities);
    }

    public List<Selection> getSelectionsByStateAndResult(SelectionState state, SelectionResult won) {
        return selectionRepository.getSelectionByStateEqualsAndResultEquals(state, won);
    }
}
