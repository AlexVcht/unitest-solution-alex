package com.kindredgroup.unibetlivetest.model.entity;

import com.kindredgroup.unibetlivetest.model.types.BetState;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "bet")
@Entity
@Data
@Accessors(chain = true)
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "mise")
    private BigDecimal mise;

    @Column(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "selection_id", nullable = false)
    private Selection selection;

    @Column(name = "state")
    private BetState betState;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;

}
