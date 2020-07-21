package dev.distressing.spleef.utils;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Randomiser {
    private final HashSet<RandomMat> selection = new HashSet<>();
    private Double higherBounds;

    public Randomiser(List<Map.Entry<Material, Double>> materials) {
        higherBounds = 0D;
        materials.forEach(materialDoubleEntry -> {
            selection.add(new RandomMat(materialDoubleEntry.getKey(), higherBounds, higherBounds + materialDoubleEntry.getValue()));
            higherBounds += materialDoubleEntry.getValue();
        });

    }

    public Material getMaterial() {
        Double target = ThreadLocalRandom.current().nextDouble(higherBounds);

        Optional<RandomMat> material = selection.stream().filter(select -> select.getLower() < target && select.getHigher() >= target).findFirst();

        if (!material.isPresent())
            return Material.AIR;

        return material.get().getMaterial();
    }
}

