package com.kindredgroup.unibetlivetest.mapper;

import com.kindredgroup.unibetlivetest.api.display.SelectionDisplay;
import com.kindredgroup.unibetlivetest.model.entity.Selection;

import java.util.List;

public class SelectionMapper {

    public static List<SelectionDisplay> entitiesToDisplay(List<Selection> selections) {
        return selections.stream()
                .map(s -> new SelectionDisplay(s.getId(), s.getName(), s.getCurrentOdd(), s.getState(), s.getResult()))
                .toList();
    }

}
