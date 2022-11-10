package com.kindredgroup.unibetlivetest.model.entity;

import com.kindredgroup.unibetlivetest.model.types.SelectionResult;
import com.kindredgroup.unibetlivetest.model.types.SelectionState;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;

@Table(name = "selection")
@Entity
@Data
@Accessors(chain = true)
public class Selection {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "current_odd")
    BigDecimal currentOdd;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    SelectionState state;

    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    SelectionResult result;

    @ManyToOne
    @JoinColumn(name = "market_id")
    Market market;

}
