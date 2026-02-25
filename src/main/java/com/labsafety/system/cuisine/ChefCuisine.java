package com.labsafety.system.cuisine;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chef_cuisines")
@IdClass(ChefCuisineId.class)
@Getter
@Setter
public class ChefCuisine {

    @Id
    @Column(name = "chef_id", nullable = false)
    private Long chefId;

    @Id
    @Column(name = "cuisine_id", nullable = false)
    private Long cuisineId;

    public ChefCuisine() {}

    public ChefCuisine(Long chefId, Long cuisineId) {
        this.chefId = chefId;
        this.cuisineId = cuisineId;
    }
}