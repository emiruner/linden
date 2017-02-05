package rme.linden.engine;

public class Turtle implements Cloneable {
    private double direction;
    private double x;
    private double y;

    public Turtle() {
        this.direction = 90;
        this.x = 0;
        this.y = 0;
    }

    public void turnBy(int angle) {
        direction += angle;

        while (direction > 360) {
            direction -= 360.0;
        }

        while (direction < 0) {
            direction += 360.0;
        }
    }

    public double[] getPos() {
        return new double[]{x, y};
    }

    public void forward(double length) {
        if (direction == 90) {
            y += length;
        } else {
            x += length * Math.cos(Math.toRadians(direction));
            y += length * Math.sin(Math.toRadians(direction));
        }
    }

    @Override
    final public Turtle clone() {
        try {
            return (Turtle) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
