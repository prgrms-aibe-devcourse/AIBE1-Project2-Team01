package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioUrl {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioUrlId;

    private String url;
//    private String description;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    public PortfolioUrl(String url, Portfolio portfolio) {
        this.url = url;
        this.portfolio = portfolio;
    }

}
