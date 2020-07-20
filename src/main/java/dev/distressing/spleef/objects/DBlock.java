package dev.distressing.spleef.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

@Getter
@Setter
public class DBlock {
    private Material material;
    private DBlockPosition Location;

    public DBlock(DBlockPosition dBlockPosition, Material material) {
        this.material = material;
        this.Location = dBlockPosition;
    }
}
