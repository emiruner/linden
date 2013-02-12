package tr.rimerun.linden.gui;

import tr.rimerun.jm.BasicLinkedInputStream;
import tr.rimerun.linden.engine.LSystem;
import tr.rimerun.linden.engine.LindenParser;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;

import static java.awt.GridBagConstraints.*;

public class FormMain extends JFrame {
    public static final Insets INSETS2 = new Insets(2, 2, 2, 2);

    private final JComboBox<ComboBoxFileItem> comboBoxInput;
    private final JTextField textFieldDepth;
    private final LSystemDrawingPanel panelDisplay;
    private final JSlider sliderStepSize;
    private final JTextArea textAreaCode;

    public FormMain() {
        setTitle("L-System");
        setLayout(new GridBagLayout());

        JLabel labelInput = new JLabel("Input:");

        comboBoxInput = new JComboBox<ComboBoxFileItem>();
        comboBoxInput.addItem(new ComboBoxFileItem(null));

        comboBoxInput.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                comboBoxInputItemStateChanged();
            }
        });

        JButton buttonChangeSourceFolder = new JButton("Change Source Folder");
        buttonChangeSourceFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonChangeSourceFolderOnClick();
            }
        });

        JLabel labelCode = new JLabel("Code:");
        textAreaCode = new JTextArea();
        textAreaCode.setRows(5);

        JLabel labelStepSize = new JLabel("Step Size:");

        sliderStepSize = new JSlider(0, 2000);
        sliderStepSize.setValue(5 * 100);
        sliderStepSize.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                draw();
            }
        });

        JButton buttonDraw = new JButton("Draw");
        buttonDraw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                draw();
            }
        });

        JLabel labelDepth = new JLabel("Depth:");
        textFieldDepth = new JTextField("5");

        panelDisplay = new LSystemDrawingPanel();

        add(labelInput, new GridBagConstraints(0, 0, 1, 1, 0, 0, EAST, NONE, INSETS2, 0, 0));
        add(comboBoxInput, new GridBagConstraints(1, 0, 1, 1, 1, 0, CENTER, HORIZONTAL, INSETS2, 0, 0));
        add(buttonChangeSourceFolder, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, INSETS2, 0, 0));

        add(labelCode, new GridBagConstraints(0, 1, 1, 1, 0, 0, EAST, NONE, INSETS2, 0, 0));
        add(new JScrollPane(textAreaCode), new GridBagConstraints(1, 1, 1, 1, 1, 0, CENTER, BOTH, INSETS2, 0, 0));

        add(labelStepSize, new GridBagConstraints(0, 2, 1, 1, 0, 0, EAST, NONE, INSETS2, 0, 0));
        add(sliderStepSize, new GridBagConstraints(1, 2, 1, 1, 1, 0, CENTER, HORIZONTAL, INSETS2, 0, 0));

        add(labelDepth, new GridBagConstraints(0, 3, 1, 1, 0, 0, EAST, NONE, INSETS2, 0, 0));
        add(textFieldDepth, new GridBagConstraints(1, 3, 1, 1, 1, 0, CENTER, HORIZONTAL, INSETS2, 0, 0));
        add(buttonDraw, new GridBagConstraints(2, 3, 1, 1, 0, 0, CENTER, HORIZONTAL, INSETS2, 0, 0));

        panelDisplay.setBorder(new LineBorder(Color.BLUE));
        add(panelDisplay, new GridBagConstraints(0, 4, 3, 1, 1, 1, CENTER, BOTH, INSETS2, 0, 0));

        setSize(640, 480);
    }

    private void comboBoxInputItemStateChanged() {
        ComboBoxFileItem selectedItem = (ComboBoxFileItem) comboBoxInput.getSelectedItem();

        if (selectedItem == null || selectedItem.getFile() == null) {
            return;
        }

        String code = readAll(selectedItem.getFile());
        textAreaCode.setText(code);
        textAreaCode.setCaretPosition(0);
    }

    private void draw() {
        double stepSize = sliderStepSize.getValue() / 100.0;
        int depth;

        try {
            depth = Integer.parseInt(textFieldDepth.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid depth: " + textFieldDepth.getText(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LindenParser parser = new LindenParser(BasicLinkedInputStream.fromString(textAreaCode.getText()));
        LSystem ls = (LSystem) parser.apply("linden");

        panelDisplay.setParams(ls, stepSize, depth);
        panelDisplay.repaint();
    }

    private void buttonChangeSourceFolderOnClick() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Please Select Source Folder");


        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File sourceFolder = fileChooser.getSelectedFile();

            comboBoxInput.removeAllItems();
            comboBoxInput.addItem(new ComboBoxFileItem(null));

            for (File inputFile : sourceFolder.listFiles(new LindenFileFilter())) {
                if (inputFile.isFile()) {
                    comboBoxInput.addItem(new ComboBoxFileItem(inputFile));
                }
            }
        }
    }

    public static class ComboBoxFileItem {
        private final File file;

        public ComboBoxFileItem(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        @Override
        public String toString() {
            if (file == null) {
                return "Please select...";
            }

            String name = file.getName();
            return name.substring(0, name.length() - 2);
        }
    }

    private String readAll(File file) {
        FileInputStream in = null;
        StringBuilder sb = new StringBuilder();
        try {
            in = new FileInputStream(file);

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException ex) {
            throw new RuntimeException("an error occured while reading file", ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }
}
