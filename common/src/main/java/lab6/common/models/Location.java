package lab6.common.models;

import java.io.Serializable;

public class Location implements Serializable {
    private final float x;

    private final Long y; //Поле не может быть null

    private final Long z; //Поле не может быть null

    private final String name; //Поле не может быть null

    public Location(float x, Long y, Long z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    @Override
    public String toString() {
        return x + ", " + y + ", " + z + ", " + name;
    }

}
