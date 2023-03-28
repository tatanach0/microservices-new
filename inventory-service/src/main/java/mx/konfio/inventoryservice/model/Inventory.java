package mx.konfio.inventoryservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "t_inventory")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String skuCode;
    private Integer quantity;
}
