package com.kindredgroup.unibetlivetest.repository.selection;

import com.kindredgroup.unibetlivetest.model.entity.Selection;
import com.kindredgroup.unibetlivetest.model.types.SelectionResult;
import com.kindredgroup.unibetlivetest.model.types.SelectionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SelectionRepository extends SelectionRepositoryCustom, JpaRepository<Selection, Long> {

    List<Selection> getSelectionByStateEquals(SelectionState state);

    List<Selection> getSelectionByStateEqualsAndResultEquals(SelectionState state, SelectionResult won);

}
