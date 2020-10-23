package dependencies.Collection;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private long x;
    private long y;

    public Coordinates (long x, long y){
        this.x = x;
        this.y = y;
    }

    public long getX() { return this.x;}
    public long getY() { return this.y;}

    @Override
    public String toString() {
        return String.format("(%s; %s)", this.x, this.y);
    }
}
