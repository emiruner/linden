package rme.linden.gui;

import java.io.File;
import java.io.FilenameFilter;

public class LindenFileFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".l");
    }
}
