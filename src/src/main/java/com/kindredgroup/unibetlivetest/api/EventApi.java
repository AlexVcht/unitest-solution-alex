package com.kindredgroup.unibetlivetest.api;

import com.kindredgroup.unibetlivetest.api.display.EventDisplay;
import com.kindredgroup.unibetlivetest.api.display.SelectionDisplay;
import com.kindredgroup.unibetlivetest.model.types.SelectionState;
import com.kindredgroup.unibetlivetest.service.EventService;
import com.kindredgroup.unibetlivetest.service.SelectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@Log4j2
@RequestMapping(Urls.BASE_PATH)
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventApi {

    private final EventService eventService;
    private final SelectionService selectionService;

    @Operation(summary = "Récupère tous les événements de la base")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EventDisplay.class)))}),
            @ApiResponse(responseCode = "204", description = "No live event", content = {@Content}),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = {@Content}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content})
    })
    @GetMapping(Urls.EVENTS)
    public ResponseEntity<List<EventDisplay>> getEvents(
            @Parameter(description = "Filter uniquement sur les events live") @RequestParam(value = "isLive", required = false) Boolean isLive) {
        final List<EventDisplay> eventList = eventService.getEvents(isLive);
        return new ResponseEntity<>(eventList, HttpStatus.OK);
    }

    @Operation(summary = "Récupère toutes les sélections d'un événément")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EventDisplay.class)))}),
            @ApiResponse(responseCode = "204", description = "No result", content = {@Content}),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = {@Content}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {@Content}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content})
    })
    @GetMapping(Urls.SELECTIONS)
    public ResponseEntity<List<SelectionDisplay>> getSelectionByEvent(
            @Parameter(description = "Id de l'évenement") @PathVariable(value = "id") Long id,
            @Parameter(description = "Statut de la sélection") @RequestParam(value = "state", required = false) SelectionState state) {
        final List<SelectionDisplay> selections = selectionService.getSelections(id, state);
        if (selections.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(selections, HttpStatus.OK);
    }
}
