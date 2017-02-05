package rme.linden.gui;

import rme.linden.engine.LinenParserRules;
import rme.jm.ParseFailure;
import rme.jm.Parser;
import rme.linden.engine.LSystem;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.awt.GridBagConstraints.*;

public class FormMain extends JFrame {
    public static final Insets INSETS2 = new Insets(2, 2, 2, 2);

    private final JComboBox<ComboBoxFileItem> comboBoxInput;
    private final JSpinner spinnerDepth;
    private final LSystemDrawingPanel panelDisplay;
    private final JSlider sliderStepSize;
    private final JTextArea textAreaCode;

    public FormMain() {
        setTitle("L-System");
        setLayout(new GridBagLayout());

        JLabel labelInput = new JLabel("Input:");

        comboBoxInput = new JComboBox<>();
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

        final JButton buttonDraw = new JButton("Draw");

        buttonDraw.setEnabled(false);
        buttonDraw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                draw();
            }
        });

        JLabel labelCode = new JLabel("Code:");
        textAreaCode = new JTextArea();
        textAreaCode.setRows(10);
        textAreaCode.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));

        textAreaCode.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                buttonDraw.setEnabled(!textAreaCode.getText().trim().isEmpty());
            }

            public void removeUpdate(DocumentEvent e) {
                buttonDraw.setEnabled(!textAreaCode.getText().trim().isEmpty());
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });

        JLabel labelStepSize = new JLabel("Step Size:");

        sliderStepSize = new JSlider(0, 2000);
        sliderStepSize.setValue(5 * 100);
        sliderStepSize.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                draw();
            }
        });

        JLabel labelDepth = new JLabel("Depth:");
        spinnerDepth = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));

        spinnerDepth.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                draw();
            }
        });
        panelDisplay = new LSystemDrawingPanel();

        add(labelInput, new GridBagConstraints(0, 0, 1, 1, 0, 0, EAST, NONE, INSETS2, 0, 0));
        add(comboBoxInput, new GridBagConstraints(1, 0, 1, 1, 1, 0, CENTER, HORIZONTAL, INSETS2, 0, 0));
        add(buttonChangeSourceFolder, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, INSETS2, 0, 0));

        add(labelCode, new GridBagConstraints(0, 1, 1, 1, 0, 0, EAST, NONE, INSETS2, 0, 0));
        add(new JScrollPane(textAreaCode), new GridBagConstraints(1, 1, 1, 1, 1, 0, CENTER, BOTH, INSETS2, 0, 0));

        add(labelStepSize, new GridBagConstraints(0, 2, 1, 1, 0, 0, EAST, NONE, INSETS2, 0, 0));
        add(sliderStepSize, new GridBagConstraints(1, 2, 1, 1, 1, 0, CENTER, HORIZONTAL, INSETS2, 0, 0));

        add(labelDepth, new GridBagConstraints(0, 3, 1, 1, 0, 0, EAST, NONE, INSETS2, 0, 0));
        add(spinnerDepth, new GridBagConstraints(1, 3, 1, 1, 1, 0, CENTER, HORIZONTAL, INSETS2, 0, 0));
        add(buttonDraw, new GridBagConstraints(2, 3, 1, 1, 0, 0, CENTER, HORIZONTAL, INSETS2, 0, 0));

        panelDisplay.setBorder(new EtchedBorder());
        add(panelDisplay, new GridBagConstraints(0, 4, 3, 1, 1, 1, CENTER, BOTH, INSETS2, 0, 0));

        setSize(800, 600);
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
        int depth = ((Number) spinnerDepth.getValue()).intValue();

        if(textAreaCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please load or enter code before trying to draw.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            Parser parser = new Parser(LinenParserRules.streamFromString(textAreaCode.getText()));

            try {
                LSystem ls = (LSystem) parser.apply(LinenParserRules.linden);

                panelDisplay.setParams(ls, stepSize, depth);
                panelDisplay.repaint();
            } catch(ParseFailure ex) {
                JOptionPane.showMessageDialog(this, "Invalid code: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
        try {
            return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (IOException ex) {
            throw new RuntimeException("an error occured while reading file", ex);
        }
    }
}
