package org.example.stock.persist.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.stock.model.Company;

import javax.persistence.*;

@Entity(name = "COMPANY")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticker;

    private String name;

    public CompanyEntity(Company company){
        this.ticker = company.getTicker();
        this.name = company.getName();
    }
}
