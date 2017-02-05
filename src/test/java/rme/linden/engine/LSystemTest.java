package rme.linden.engine;

import org.junit.Test;
import rme.jm.Parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LSystemTest {
    @Test
    public void testSimple() {
        String input = "a = 90\n" +
                "F\n" +
                "F -> F+F";

        Parser parser = new Parser(LinenParserRules.streamFromString(input));

        LSystem ls = (LSystem) parser.apply(LinenParserRules.linden);
        TestTracer tracer = new TestTracer();

        LSystemExecutor executor = new LSystemExecutor(ls, 2, 10, tracer);
        executor.execute();

        assertEquals(4, tracer.getLineCount());

        assertLineEquals(new double[]{0, 0, 0, 10}, tracer.getLine(0));
        assertLineEquals(new double[]{0, 10, 10, 10}, tracer.getLine(1));
        assertLineEquals(new double[]{10, 10, 10, 0}, tracer.getLine(2));
        assertLineEquals(new double[]{10, 0, 0, 0}, tracer.getLine(3));
    }

    private void assertLineEquals(double[] expected, double[] actual) {
        for (int i = 0; i < expected.length; ++i) {
            if ((expected[i] - actual[i]) > 0.00000001) {
                fail("lines not same, expected: " + lineToStr(expected) + ", actual: " + lineToStr(actual));
            }
        }
    }

    private String lineToStr(double[] line) {
        return "(" + line[0] + "," + line[1] + ")---(" + line[2] + "," + line[3] + ")";
    }
}
