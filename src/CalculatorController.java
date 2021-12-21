import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import java.io.*;
import java.net.URL;
import java.util.*;


public class CalculatorController implements Initializable {

    private static final String INPUT_FILE_NAME = "dinChart.txt";
    private static final int HEIGHT_EQUALIZER = 7;
    private double[][] dinChart;
    @FXML
    private ChoiceBox<String> heightBox;
    private final String[] heightOptions = {"Less than 4'10\" / 148 cm", "4'11\" - 5'1\" / 149 - 157 cm",
            "5'2\" - 5'5\" / 158 - 166 cm", "5'6\" - 5'10\" / 167 - 178 cm",
            "5'11\" - 6'4\" / 179 - 194 cm", "6'5\"+ / 195+ cm"};
    private final HashMap<String, Integer> heightDict = new HashMap<>();

    @FXML
    private ChoiceBox<String> weightBox;
    private final String[] weightOptions = {"22 - 29 lbs / 10 - 13 kg", "30 - 38 lbs / 14 - 17 kg",
            "39 - 47 lbs / 18 - 21 kg", "48 - 56 lbs / 22 - 25 kg",
            "57 - 66 lbs / 26 - 30 kg", "67 - 78 lbs / 31 - 35 kg",
            "79 - 91 lbs / 36 - 41 kg", "92 - 107 lbs / 42 - 48 kg",
            "108 - 125 lbs / 49 - 57 kg", "126 - 147 lbs / 58 - 66 kg",
            "148 - 174 lbs / 67 - 78 kg", "175 - 209 lbs / 79 - 94 kg",
            "210+ lbs / 95+ kg"};
    private final HashMap<String, Integer> weightDict = new HashMap<>();
    @FXML
    private ChoiceBox<String> lengthBox;
    private final String[] lengthOptions = {"Less than 250 mm", "251 - 270 mm",
            "271 - 290 mm", "291 - 310 mm", "311 - 330 mm", "331 - 350 mm", "351+ mm"};
    private final HashMap<String, Integer> lengthDict = new HashMap<>();
    @FXML
    private ChoiceBox<String> ageBox;
    private final String[] ageOptions = {"9 years or younger", "10 - 49 years", "50 years or older"};
    private final HashMap<String, Integer> ageDict = new HashMap<>();
    @FXML
    private ChoiceBox<String> typeBox;
    private final String[] typeOptions = {"I : Beginner", "II : Intermediate",
                                            "III : Expert"};
    private final HashMap<String, Integer> typeDict = new HashMap<>();
    @FXML
    private Button calcButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        heightBox.getItems().setAll(heightOptions);
        weightBox.getItems().setAll(weightOptions);
        lengthBox.getItems().setAll(lengthOptions);
        ageBox.getItems().setAll(ageOptions);
        typeBox.getItems().setAll(typeOptions);

        calcButton.setDisable(true);

        heightBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldText, newText) -> update());
        weightBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldText, newText) -> update());
        lengthBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldText, newText) -> update());
        ageBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldText, newText) -> update());
        typeBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldText, newText) -> update());

        dinChart = createDinChart();

        for (int i = 0; i < weightOptions.length; i++) {
            weightDict.put(weightOptions[i], i);
        }
        for (int i = 0; i < heightOptions.length; i++) {
            heightDict.put(heightOptions[i], i + HEIGHT_EQUALIZER);
        }
        for (int i = 0; i < lengthOptions.length; i++) {
            lengthDict.put(lengthOptions[i], i);
        }
        for (int i = 0; i < ageOptions.length; i++) {
            ageDict.put(ageOptions[i], i);
        }
        for (int i = 0; i < typeOptions.length; i++) {
            typeDict.put(typeOptions[i], i);
        }
    }

    private static double[][] createDinChart() {

        double[][] dinChart;
        int rows;
        int cols;

        try (Scanner sc = new Scanner(new File(INPUT_FILE_NAME))) {
            List<String[]> list = new ArrayList<>();

            while(sc.hasNextLine()) {
                String[] arr = sc.nextLine().split(",");
                list.add(arr);
            }

            rows = list.size();
            cols = list.get(0).length;
            dinChart = new double[rows][cols];
            for(int row = 0; row < rows; row++) {
                for(int col = 0; col < cols; col++) {
                    dinChart[row][col] = Double.parseDouble(list.get(row)[col]);
                }
            }
            return dinChart;

        } catch (InputMismatchException | FileNotFoundException e) {

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("CSV Failed to load");
            a.setContentText(e.getMessage());
            a.showAndWait();
        }


        return null;
    }

    @FXML
    private void update() {
        calcButton.setDisable(heightBox.getSelectionModel().isEmpty()
                || weightBox.getSelectionModel().isEmpty()
                || lengthBox.getSelectionModel().isEmpty()
                || ageBox.getSelectionModel().isEmpty()
                || typeBox.getSelectionModel().isEmpty());

    }

    @FXML
    private void displayDIN() {

        double din = calculateDIN();

        Alert fin = new Alert(Alert.AlertType.INFORMATION);
        fin.setTitle("DIN");
        fin.setHeaderText("Your DIN is:");
        fin.setContentText(Double.toString(din));
        fin.show();
    }

    private double calculateDIN() {
        String weight = weightBox.getValue();
        String height = heightBox.getValue();
        String age = ageBox.getValue();
        String type = typeBox.getValue();

        int lengthNumber = lengthDict.get(lengthBox.getValue());
        int numericSkierCode = getYValue(weight, height, age, type);

        return dinChart[numericSkierCode][lengthNumber];
    }

    private int getYValue(String weight, String height, String age, String type) {
        int finalNumber;
        int weightNumber = weightDict.get(weight);
        int heightNumber = heightDict.get(height);
        int ageNumber = ageDict.get(age);
        int typeNumber = typeDict.get(type);

        if (weightNumber > heightNumber) {
            finalNumber = heightNumber;
        } else if (weightNumber < heightNumber) {
            finalNumber = weightNumber;
        } else {
            finalNumber = weightNumber;
        }

        if ((ageNumber == 0 || ageNumber == 2) && finalNumber != 0) {
            --finalNumber;
        }

        finalNumber += typeNumber;

        return finalNumber;
    }
}
