import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class TagExtractor extends JFrame {
    private JTextArea displayArea;
    private Map<String, Integer> wordFrequencies = new HashMap<>();
    private Set<String> stopWords = new TreeSet<>();

    public TagExtractor() {
        setTitle("Tag Extractor");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        JButton loadFileButton = new JButton("Load Text File");
        loadFileButton.addActionListener(e -> loadTextFile());

        JButton loadStopWordsButton = new JButton("Load Stop Words File");
        loadStopWordsButton.addActionListener(e -> loadStopWordsFile());

        JButton saveButton = new JButton("Save Results");
        saveButton.addActionListener(e -> saveResults());

        JPanel panel = new JPanel();
        panel.add(loadFileButton);
        panel.add(loadStopWordsButton);
        panel.add(saveButton);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
    }

    private void loadStopWordsFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            stopWords.clear();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stopWords.add(line.trim().toLowerCase());
                }
                JOptionPane.showMessageDialog(this, "Stop words loaded successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading stop words file: " + e.getMessage(),
                        "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTextFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            wordFrequencies.clear();
            displayArea.setText("");
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.toLowerCase().split("\\W+");
                    for (String word : words) {
                        if (!stopWords.contains(word) && !word.isEmpty()) {
                            wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
                        }
                    }
                }
                displayResults();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading text file: " + e.getMessage(),
                        "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayResults() {
        StringBuilder results = new StringBuilder();
        wordFrequencies.forEach((word, frequency) -> results.append(word).append(": ").append(frequency).append("\n"));
        displayArea.setText(results.toString());
    }

    private void saveResults() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
                for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
                    writer.write(entry.getKey() + ": " + entry.getValue());
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this, "Results saved successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving results: " + e.getMessage(),
                        "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TagExtractor().setVisible(true);
        });
    }
}
