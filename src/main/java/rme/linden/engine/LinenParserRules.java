package rme.linden.engine;

import rme.jm.LinkedInputStream;
import rme.jm.Parser;
import rme.jm.ReaderBackedLinkedInputStream;
import rme.jm.Rule;

import java.io.StringReader;
import java.util.List;

import static rme.jm.TextRules.*;

public final class LinenParserRules {
    // step = chr:c ? (isLetter(c) || c == '+' || c == '-' || c == '[' || c == ']')
    public static final Rule step = new Rule() {
        public Object execute(Parser parser) {
            Character c = (Character) parser.apply(chr);
            parser.ensure(Character.isLetter(c) || c == '+' || c == '-' || c == '[' || c == ']');
            return c;
        }
    };

    private static String charListToString(List<Object> chars) {
        StringBuilder sb = new StringBuilder();

        for (Object o : chars) {
            sb.append(o);
        }

        return sb.toString();
    }

    // steps = step*:s -> charListToString(s)
    private static final Rule steps = new Rule() {
        public Object execute(Parser parser) {
            List<Object> stepsChars = parser._many(step);
            return charListToString(stepsChars);
        }
    };

    // production = letter:name spaces "->" spaces steps:s -> new Production(name, s)
    private static final Rule production = new Rule() {
        public Object execute(Parser parser) {
            String name = parser.apply(letter).toString();
            parser.apply(spaces);
            parser.applyWithArgs(token, "->");
            parser.apply(spaces);

            String s = (String) parser.apply(steps);
            return new Production(name, s);
        }
    };

    // linden = "a" "=" num:a spaces steps:init (spaces production)+:p -> new Linden(a, i, p);
    public static final Rule linden = new Rule() {
        public Object execute(Parser parser) {
            parser.applyWithArgs(token, "a");
            parser.applyWithArgs(token, "=");

            int a = (Integer) parser.apply(num);
            parser.apply(spaces);
            String init = (String) parser.apply(steps);

            List<Object> p = parser._many1(new Rule() {
                public Object execute(Parser parser) {
                    parser.apply(spaces);
                    return parser.apply(production);
                }
            });

            return new LSystem(a, init, p);
        }
    };

    public static LinkedInputStream streamFromString(String str) {
        return new ReaderBackedLinkedInputStream(new StringReader(str));
    }
}
