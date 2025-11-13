import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class SmartFitnessTracker extends JFrame implements ActionListener {
    JTextField heightField, weightField, ageField, bmiField, bmrField, calorieField;
    JComboBox<String> genderBox, activityBox;
    JButton calcButton, clearButton, saveButton, viewHistoryButton;
    ArrayList<String> history = new ArrayList<>();

    public SmartFitnessTracker() {
        setTitle("Smart Fitness Tracker");
        setSize(550, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(10, 2, 10, 10));
        setLocationRelativeTo(null);

        add(new JLabel("Height (m):"));
        heightField = new JTextField();
        add(heightField);

        add(new JLabel("Weight (kg):"));
        weightField = new JTextField();
        add(weightField);

        add(new JLabel("Age:"));
        ageField = new JTextField();
        add(ageField);

        add(new JLabel("Gender:"));
        genderBox = new JComboBox<>(new String[] { "Male", "Female" });
        add(genderBox);

        add(new JLabel("Activity Level:"));
        activityBox = new JComboBox<>(new String[] {
                "Sedentary (little/no exercise)",
                "Lightly Active (1-3 days/week)",
                "Moderately Active (3-5 days/week)",
                "Very Active (6-7 days/week)"
        });
        add(activityBox);

        add(new JLabel("BMI:"));
        bmiField = new JTextField();
        bmiField.setEditable(false);
        add(bmiField);

        add(new JLabel("BMR (Calories/day):"));
        bmrField = new JTextField();
        bmrField.setEditable(false);
        add(bmrField);

        add(new JLabel("Recommended Calories:"));
        calorieField = new JTextField();
        calorieField.setEditable(false);
        add(calorieField);

        calcButton = new JButton("Calculate");
        clearButton = new JButton("Clear");
        saveButton = new JButton("Save Progress");
        viewHistoryButton = new JButton("View History");

        add(calcButton);
        add(clearButton);
        add(saveButton);
        add(viewHistoryButton);

        calcButton.addActionListener(this);
        clearButton.addActionListener(this);
        saveButton.addActionListener(this);
        viewHistoryButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == calcButton) {
            calculateStats();
        } else if (e.getSource() == clearButton) {
            clearFields();
        } else if (e.getSource() == saveButton) {
            saveProgress();
        } else if (e.getSource() == viewHistoryButton) {
            showHistory();
        }
    }

    private void calculateStats() {
        try {
            double height = Double.parseDouble(heightField.getText());
            double weight = Double.parseDouble(weightField.getText());
            int age = Integer.parseInt(ageField.getText());
            String gender = (String) genderBox.getSelectedItem();
            String activity = (String) activityBox.getSelectedItem();

            double bmi = weight / (height * height);
            bmiField.setText(String.format("%.2f", bmi));

            double bmr;
            if (gender.equals("Male")) {
                bmr = 88.36 + (13.4 * weight) + (4.8 * (height * 100)) - (5.7 * age);
            } else {
                bmr = 447.6 + (9.2 * weight) + (3.1 * (height * 100)) - (4.3 * age);
            }
            bmrField.setText(String.format("%.2f", bmr));

            double multiplier = switch (activity) {
                case "Sedentary (little/no exercise)" -> 1.2;
                case "Lightly Active (1-3 days/week)" -> 1.375;
                case "Moderately Active (3-5 days/week)" -> 1.55;
                case "Very Active (6-7 days/week)" -> 1.725;
                default -> 1.2;
            };

            double calories = bmr * multiplier;
            calorieField.setText(String.format("%.2f", calories));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
        }
    }

    private void clearFields() {
        heightField.setText("");
        weightField.setText("");
        ageField.setText("");
        bmiField.setText("");
        bmrField.setText("");
        calorieField.setText("");
    }

    private void saveProgress() {
        try {
            String record = LocalDate.now() + " | " +
                    "Height: " + heightField.getText() + " m, " +
                    "Weight: " + weightField.getText() + " kg, " +
                    "BMI: " + bmiField.getText() + ", " +
                    "Calories: " + calorieField.getText() + "\n";

            FileWriter fw = new FileWriter("fitness_data.txt", true);
            fw.write(record);
            fw.close();

            history.add(record);
            JOptionPane.showMessageDialog(this, "Progress saved!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving progress!");
        }
    }

    private void showHistory() {
        try {
            File file = new File("fitness_data.txt");
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "No history yet!");
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            br.close();

            JTextArea textArea = new JTextArea(content.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(450, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Fitness History", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading history!");
        }
    }

    public static void main(String[] args) {
        new SmartFitnessTracker();
    }
}
