package com.kindredgroup.unibetlivetest.api;

import com.kindredgroup.unibetlivetest.api.body.AddBetBody;
import com.kindredgroup.unibetlivetest.service.BetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Urls.BASE_PATH)
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BetApi {

    private final BetService betService;

    @Operation(summary = "Enregistre un pari")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bet saved", content = {@Content}),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = {@Content}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {@Content}),
            @ApiResponse(responseCode = "409", description = "Conflict, bet already placed", content = {@Content}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content}),
            @ApiResponse(responseCode = "600", description = "Not enough balance", content = {@Content}),
            @ApiResponse(responseCode = "600", description = "Not enough balance", content = {@Content}),
            @ApiResponse(responseCode = "601", description = "Odd updated", content = {@Content}),
            @ApiResponse(responseCode = "602", description = "Selection closed", content = {@Content}),
    })
    @PostMapping(Urls.ADD_BET)
    public ResponseEntity<Long> addBet(@RequestBody AddBetBody addBetBody) {
        Long betId = betService.addBet(addBetBody);
        return new ResponseEntity<>(betId, HttpStatus.OK);
    }

}
