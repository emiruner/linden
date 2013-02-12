package tr.rimerun.linden.engine;

import java.util.ArrayList;
import java.util.List;

public class TestTracer implements LSystemTracer {
    private List<double[]> lines = new ArrayList<double[]>();

    @Override
    public void draw(double x0, double y0, double x1, double y1) {
        lines.add(new double[]{x0, y0, x1, y1});
    }

    public double[] getLine(int idx) {
        return lines.get(idx);
    }

    public int getLineCount() {
        return lines.size();
    }
}
