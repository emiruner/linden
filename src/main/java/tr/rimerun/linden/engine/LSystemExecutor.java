package tr.rimerun.linden.engine;

import java.util.Stack;

public class LSystemExecutor {
    private final LSystem sys;
    private final double stepLength;
    private int depth;
    private LSystemTracer tracer;
    private Turtle turtle;
    private Stack<Turtle> turtleStack;

    public LSystemExecutor(LSystem sys, int depth, double stepLength, LSystemTracer tracer) {
        this.sys = sys;
        this.depth = depth;
        this.stepLength = stepLength;
        this.tracer = tracer;
        this.turtle = new Turtle();
        this.turtleStack = new Stack<Turtle>();
    }

    public void execute() {
        execute(sys.getInit());
    }

    public void execute(String steps) {
        for (char step : steps.toCharArray()) {
            if (step == '+') {
                turtle.turnBy(-sys.getAngleIncrement());
            } else if (step == '-') {
                turtle.turnBy(sys.getAngleIncrement());
            } else if (step == '[') {
                turtleStack.push(turtle.clone());
            } else if (step == ']') {
                turtle = turtleStack.pop();
            } else if (Character.isUpperCase(step)) {
                if (depth == 0) {
                    double[] currentPos = turtle.getPos();
                    turtle.forward(stepLength);
                    double[] newPos = turtle.getPos();

                    tracer.draw(currentPos[0], currentPos[1], newPos[0], newPos[1]);
                } else {
                    callProduction(step);
                }
            } else if (Character.isLowerCase(step)) {
                if (depth == 0) {
                    turtle.forward(stepLength);
                } else {
                    callProduction(step);
                }
            } else {
                throw new RuntimeException("unexpected step: " + step);
            }
        }
    }

    private void callProduction(char name) {
        --depth;

        final String newSteps = getProduction(name);

        if (newSteps == null) {
            throw new RuntimeException("no production found for " + name);
        } else {
            execute(newSteps);
        }

        ++depth;
    }

    private String getProduction(char nameChar) {
        String name = Character.toString(nameChar);

        for (Production production : sys.getProductions()) {
            if (production.getName().equals(name)) {
                return production.getSteps();
            }
        }

        return null;
    }
}
