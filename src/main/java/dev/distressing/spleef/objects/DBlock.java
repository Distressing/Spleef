package dev.distressing.spleef.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@Setter
public class DBlock {
    private final Material material;
    private final DBlockPosition Location;

    public DBlock(DBlockPosition dBlockPosition, Material material) {
        this.material = material;
        this.Location = dBlockPosition;
    }
}
