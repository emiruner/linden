package tr.rimerun.linden.engine;

import org.junit.Test;
import tr.rimerun.jm.BasicLinkedInputStream;

import static org.junit.Assert.assertEquals;

public class LindenParserTest {
    @Test
    public void testParse() {
        String input = "a = 90\n" +
                "F-F-F-F\n" +
                "F -> F-F+F+[FF]-f-F+F\n" +
                "f -> fFf\n\n\n";

        LindenParser parser = new LindenParser(BasicLinkedInputStream.fromString(input));

        LSystem ls = (LSystem) parser.apply("linden");

        assertEquals(90, ls.getAngleIncrement());
        assertEquals("F-F-F-F", ls.getInit());
        assertEquals(2, ls.getProductions().size());

        Production p0 = ls.getProductions().get(0);

        assertEquals("F", p0.getName());
        assertEquals("F-F+F+[FF]-f-F+F", p0.getSteps());

        Production p1 = ls.getProductions().get(1);

        assertEquals("f", p1.getName());
        assertEquals("fFf", p1.getSteps());
    }
}
