package com.kindredgroup.unibetlivetest.api.body;

import java.math.BigDecimal;

public record AddBetBody(String selectionId, BigDecimal cote, BigDecimal mise) {
}
