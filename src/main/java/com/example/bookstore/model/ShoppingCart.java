package com.example.bookstore.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Setter
@Getter
@ToString
@SQLDelete(sql = "UPDATE shopping_carts SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted=false")
@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //when I am changing as suggested to:
    //    @Id
    //    private Long id;
    //    @MapsId
    //    @OneToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "user_id", nullable = false)
    // i am getting SQLIntegrityConstraintViolationException
    // Cannot add or update a child row: a foreign key constraint fails (`bookstore_app`.`
    // cart_items`,CONSTRAINT `fk_cart_items_shopping_carts` FOREIGN KEY (`shopping_cart_id`)
    // REFERENCES `shopping_carts` (`id`))

    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();
    @Column(nullable = false)
    private boolean isDeleted = false;
}
