package com.labsafety.system.cuisine;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ChefCuisineId implements Serializable {
    private Long chefId;
    private Long cuisineId;
}