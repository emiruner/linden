package tr.rimerun.linden.gui;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        FormMain formMain = new FormMain();

        formMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        formMain.setVisible(true);
    }
}
