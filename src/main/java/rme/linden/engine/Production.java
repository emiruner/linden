package rme.linden.engine;

public class Production {
    private final String name;
    private final String steps;

    public Production(String name, String steps) {
        this.name = name;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public String getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return name + " -> " + steps;
    }
}
