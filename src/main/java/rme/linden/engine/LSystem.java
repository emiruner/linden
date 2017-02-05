package rme.linden.engine;

import java.util.ArrayList;
import java.util.List;

public class LSystem {
    private final int angleIncrement;
    private final String init;
    private final List<Production> productions;

    public LSystem(int angleIncrement, String init, List<Object> productions) {
        this.angleIncrement = angleIncrement;
        this.init = init;
        this.productions = new ArrayList<Production>();

        for (Object o : productions) {
            this.productions.add((Production) o);
        }
    }

    public int getAngleIncrement() {
        return angleIncrement;
    }

    public String getInit() {
        return init;
    }

    public List<Production> getProductions() {
        return productions;
    }
}
