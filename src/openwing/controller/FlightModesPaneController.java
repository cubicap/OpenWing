/* 
 * Copyright (C) 2019 Petr Kubica
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package openwing.controller;

import openwing.core.flightmodes.FlightMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import openwing.Main;
import openwing.core.SaveData;
import openwing.core.SaveLoadAble;

/**
 * FXML Controller class
 */
public class FlightModesPaneController implements Initializable, SaveLoadAble {
    
    @FXML
    private VBox vBox;
    
    ObservableList<FlightMode> flightModesList;
    
    Spinner numFlightModesSpinner;
    int shownRows = 0;
    List<FlightModeRow> rowList = new ArrayList<>();
    
    ResourceBundle rb;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        flightModesList = (ObservableList<FlightMode>)Main.dataBundle.getFlightModes();
        
        HBox numFlightModes = new HBox();
        numFlightModes.setAlignment(Pos.CENTER_LEFT);
        numFlightModes.setFillHeight(true);
        Label numChannelsLabel = new Label(rb.getString("num_fmodes") + ":");
        numFlightModesSpinner = new Spinner(1, 20, 5, 1);
        HBox.setMargin(numChannelsLabel, new Insets(0, 15, 0, 0));
        HBox.setMargin(numFlightModesSpinner, new Insets(0, 0, 0, 15));
        numFlightModes.getChildren().add(numChannelsLabel);
        numFlightModes.getChildren().add(numFlightModesSpinner);
        
        VBox.setMargin(numFlightModes, new Insets(30, 0, 20, 20));
        vBox.getChildren().add(numFlightModes);
        
        numFlightModesSpinner.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            while(rowList.size() < (Integer)newValue) {
                addFlightMode(rb.getString("flight_mode") + " " + (rowList.size() + 1) + ":");
                shownRows++;
            }
            for(int i = 0; i < rowList.size() && shownRows < (Integer)newValue; i++) {
                if(!rowList.get(i).isVisible()) {
                    rowList.get(i).setVisible(true);
                    shownRows++;
                }
            }
            for(int i = rowList.size() - 1; i > 0 && shownRows > (Integer)newValue; i--) {
                if(rowList.get(i).isVisible()) {
                    rowList.get(i).setVisible(false);
                    shownRows--;
                }
            }
        });
        while(rowList.size() < (Integer)numFlightModesSpinner.getValue()) {
                addFlightMode(rb.getString("flight_mode") + " " + (rowList.size() + 1) + ":");
                shownRows++;
            }
    }
    
    public List<FlightMode> getFlightModes() {
        List<FlightMode> flightModes = new ArrayList<>();
        for(FlightModeRow row : rowList) {
            if(row.isVisible()) {
                flightModes.add(row.getFlightMode());
            }
        }
        return flightModes;
    }
    
    public void addFlightMode(String title) {
        FlightModeRow row = new FlightModeRow(flightModesList, title, rb);
        VBox.setMargin(row.getPane(), new Insets(20, 20, 20, 20));
        vBox.getChildren().add(row.getPane());
        rowList.add(row);
    }

    @Override
    public void save(SaveData data) {
        data.add("fm_num", shownRows + "");
        for(int i = 0; i < shownRows; i++) {
            data.add("fm" + i, rowList.get(i).getFlightMode().getId());
        }
    }

    @Override
    public void load(SaveData data) {
        String d = data.get("fm_num");
        if(d != null) {
            int showRows = Integer.parseInt(d);
            numFlightModesSpinner.getValueFactory().setValue(showRows);
        }
        for(int i = 0; i < shownRows; i++) {
            rowList.get(i).select(data.get("fm" + i));
        }
    }
}

class FlightModeRow {
    ComboBox<FlightMode> flightModeSelection;
    
    String label;
    VBox box;
    Accordion content;
    TitledPane parameters;
    TitledPane description;
    TitledPane requirements;
    
    boolean visible = true;
    
    public FlightModeRow(ObservableList<FlightMode> flightModesList, String label, ResourceBundle rb) {
        this.label = label;
        
        box = new VBox();
        box.setFillWidth(true);
        box.setStyle(
            "    -fx-border-color: #000;\n" +
            "    -fx-fx-border-insets: 5;\n" +
            "    -fx-border-radius: 5;\n" +
            "    -fx-border-width: 2;");
        box.setFillWidth(true);
        
        GridPane header = new GridPane();
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(50);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setHalignment(HPos.RIGHT);
        cc2.setPercentWidth(50);
        header.getColumnConstraints().addAll(cc1, cc2);
        
        Label title = new Label(label);
        GridPane.setMargin(title, new Insets(0, 0, 0, 3));
        title.setFont(Font.font("default", FontWeight.BOLD, FontPosture.REGULAR, 20));
        header.add(title, 0, 0);
        flightModeSelection = new ComboBox<>((ObservableList<FlightMode>)Main.dataBundle.getFlightModes());
        
        header.add(flightModeSelection, 1, 0);
        
        box.getChildren().add(header);
        
        content = new Accordion();
        box.getChildren().add(content);
        
        description = new TitledPane();
        description.setText(rb.getString("description"));
        description.setExpanded(false);
        
        requirements = new TitledPane();
        requirements.setText(rb.getString("requirements"));
        requirements.setExpanded(false);
        
        parameters = new TitledPane();
        parameters.setText(rb.getString("parameters"));
        parameters.setExpanded(false);
        
        flightModeSelection.valueProperty().addListener((observable, oldValue, newValue) -> {
            String desc = flightModeSelection.getSelectionModel().getSelectedItem().getDescription();
            if(!desc.equals("")) {
                VBox descriptionPane = new VBox();
                Label descriptionLabel = new Label(desc);
                descriptionLabel.setWrapText(true);
                VBox.setMargin(descriptionLabel, new Insets(5, 5, 5, 5));
                descriptionPane.getChildren().add(descriptionLabel);
                description.setContent(descriptionPane);
                description.setVisible(true);
                description.setManaged(true);
            }
            else {
                description.setVisible(false);
                description.setManaged(false);
            }
            VBox requirementsPane = new VBox();
            if(flightModeSelection.getSelectionModel().getSelectedItem().getRequirements().size() > 0) {
                for(String r : flightModeSelection.getSelectionModel().getSelectedItem().getRequirements())     {
                    String text = rb.containsKey(r) ? rb.getString(r) : ("Other - " + r);
                    Label requirementLabel = new Label(text);
                    VBox.setMargin(requirementLabel, new Insets(5, 5, 5, 5));
                    requirementsPane.getChildren().add(requirementLabel);
                }
            }
            else {
                Label requirementLabel = new Label(rb.getString("none"));
                VBox.setMargin(requirementLabel, new Insets(5, 5, 5, 5));
                requirementsPane.getChildren().add(requirementLabel);
            }
            requirements.setContent(requirementsPane);
            Pane parametersPane = flightModeSelection.getSelectionModel().getSelectedItem().getParameterGroup().getPane();
            if(parametersPane != null) {
                parameters.setContent(parametersPane);
                parameters.setVisible(true);
                parameters.setManaged(true);
            }
            else {
                parameters.setVisible(false);
                parameters.setManaged(false);
            }
        });
        
        box.getChildren().add(description);
        box.getChildren().add(requirements);
        box.getChildren().add(parameters);
        
        flightModeSelection.getSelectionModel().selectFirst();
    }
    
    public Pane getPane() {
        return box;
    }

    public FlightMode getFlightMode() {
        return flightModeSelection.getSelectionModel().getSelectedItem();
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
        box.setVisible(visible);
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public boolean select(String id) {
        for(FlightMode flightMode : flightModeSelection.getItems()) {
            if(flightMode.getId().equals(id)) {
                flightModeSelection.getSelectionModel().select(flightMode);
                return true;
            }
        }
        return false;
    }
}