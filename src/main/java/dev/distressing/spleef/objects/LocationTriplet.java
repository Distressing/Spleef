package dev.distressing.spleef.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationTriplet {
    private final int x;
    private final int y;
    private final int z;

    public LocationTriplet(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
