package dev.distressing.spleef.utils;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public class RandomMat {
    private final Material material;
    private final Double lower;
    private final Double higher;

    public RandomMat(Material material, Double lower, Double higher) {
        this.lower = lower;
        this.higher = higher;
        this.material = material;
    }

}
