package com.kindredgroup.unibetlivetest.api.display;

import com.kindredgroup.unibetlivetest.model.types.SelectionResult;
import com.kindredgroup.unibetlivetest.model.types.SelectionState;

import java.math.BigDecimal;

public record SelectionDisplay(Long id, String name, BigDecimal currentOdd, SelectionState state, SelectionResult result) {
}
