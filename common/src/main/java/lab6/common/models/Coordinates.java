package lab6.common.models;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private final int x; //Значение поля должно быть больше -517

    private final int y; //Значение поля должно быть больше -652

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int sumCoordinates() {
        return x + y;
    }

    public void validate() throws IllegalStateException {
        if (x <= -517) throw new IllegalStateException("coordinate x can't be lower or equal -517");
        if (y <= -652) throw new IllegalStateException("coordinate y can't be lower or equal -652");
    }

    @Override
    public String toString() {
        return x + ";" + y;
    }

}
