package tr.rimerun.linden.engine;

import tr.rimerun.jm.CharacterOrientedBaseParser;
import tr.rimerun.jm.LinkedInputStream;
import tr.rimerun.jm.Predicate;
import tr.rimerun.jm.SimpleFn;

import java.util.List;

public class LindenParser extends CharacterOrientedBaseParser {
    public LindenParser(LinkedInputStream input) {
        super(input);
    }

    // rawNum = rawNum:n digit:d -> (((Integer) n) * 10 + Character.digit((Character) d, 10)
    //        | digit:d          -> (Character.digit((Character) d, 10)
    private Object rawNum() {
        return _or(new SimpleFn() {
                       public Object call() {
                           Object n = apply("rawNum");
                           Object d = apply("digit");

                           return ((Integer) n) * 10 + Character.digit((Character) d, 10);
                       }
                   }, new SimpleFn() {
                       public Object call() {
                           Object d = apply("digit");
                           return Character.digit((Character) d, 10);
                       }
                   }
        );
    }

    // letter = chr:d ?(Character.isLetter(d)) -> d
    private Object letter() {
        return applyWithPred("chr", new Predicate<Character>() {
            public boolean eval(Character d) {
                return Character.isLetter(d);
            }
        });
    }

    // num = spaces '-'?:minus rawNum:n -> (minus == null ? n : -1 * n)
    private Object num() {
        apply("spaces");

        Object minus = _opt(new SimpleFn() {
            public Object call() {
                return applyWithArgs("exactly", '-');
            }
        });

        Integer n = (Integer) apply("rawNum");

        return minus == null ? n : -1 * n;
    }

    // linden = "a" "=" num:a spaces steps:init (spaces production)+:p -> new Linden(a, i, p);
    private Object linden() {
        applyWithArgs("token", "a");
        applyWithArgs("token", "=");

        int a = (Integer) apply("num");
        apply("spaces");
        String init = (String) apply("steps");

        List<Object> p = _many1(new SimpleFn() {
            public Object call() {
                apply("spaces");
                return apply("production");
            }
        });

        return new LSystem(a, init, p);
    }

    // production = letter:name spaces "->" spaces steps:s -> new Production(name, s)
    private Object production() {
        String name = apply("letter").toString();
        apply("spaces");
        applyWithArgs("token", "->");
        apply("spaces");

        String s = (String) apply("steps");
        return new Production(name, s);
    }

    // steps = step*:s -> charListToString(s)
    private Object steps() {
        List<Object> stepsChars = _many("step");
        return charListToString(stepsChars);
    }

    // step = chr:c ? (isLetter(c) || c == '+' || c == '-' || c == '[' || c == ']')
    private Object step() {
        return applyWithPred("chr", new Predicate() {
            public boolean eval(Object o) {
                Character c = (Character) o;
                return Character.isLetter(c) || c == '+' || c == '-' || c == '[' || c == ']';
            }
        });
    }

    private String charListToString(List<Object> chars) {
        StringBuilder sb = new StringBuilder();

        for (Object o : chars) {
            sb.append(o);
        }

        return sb.toString();
    }
}
