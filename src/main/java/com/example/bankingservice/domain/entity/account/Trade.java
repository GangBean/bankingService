package com.example.bankingservice.domain.entity.account;


import com.example.bankingservice.domain.entity.BaseEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Trade extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Account account;

    @Column(nullable = false)
    private LocalDateTime tradeDateTime;

    @Column(nullable = false)
    @Min(0)
    private Long tradeAmount;

    @Column(nullable = false)
    @Enumerated
    private TradeType tradeType;

    public enum TradeType {
        DEPOSIT, WITHDRAWAL
    }
}
