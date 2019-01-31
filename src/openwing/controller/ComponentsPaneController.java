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

import openwing.core.components.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
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
public class ComponentsPaneController implements Initializable, SaveLoadAble {
    
    @FXML
    VBox vBox;
    
    static Pane pane;
    
    List<ComponentSelection> componentSelecions = new ArrayList<>();

    ResourceBundle rb;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        
        pane = vBox;
        addComponent("servos");
        addComponent("motor");
        addComponent("gyro");
        addComponent("baro");
        //addComponent("volt");
        addComponent("gps");
    }
    
    public static void addComponent(Pane componentPane) {
        pane.getChildren().add(componentPane);
    }
    
    public void addComponent(String type) {
        ComponentSelection component = new ComponentSelection(type, rb.getString(type), rb);
        VBox.setMargin(component.getPane(), new Insets(20, 20, 20, 20));
        vBox.getChildren().add(component.getPane());
        componentSelecions.add(component);
    }
    
    public List<Component> getComponents() {
        List<Component> components = new ArrayList<>();
        for(ComponentSelection componentSelection : componentSelecions) {
            components.add(componentSelection.getComponent());
        }
        return components;
    }

    @Override
    public void save(SaveData data) {
        for(ComponentSelection componentSelection : componentSelecions) {
            data.add("comp_" + componentSelection.getType(), componentSelection.getComponent().getId());
        }
    }

    @Override
    public void load(SaveData data) {
        System.out.println("load parts");
        for(ComponentSelection componentSelection : componentSelecions) {
            componentSelection.select(data.get("comp_" + componentSelection.getType()));
        }
    }
}

class ComponentSelection {
    
    String type;
    String label;
    VBox box;
    Accordion content;
    TitledPane parameters;
    TitledPane description;
    TitledPane connections;
    ComboBox<Component> cb;
    
    public ComponentSelection(String type, String label, ResourceBundle rb) {
        this.type = type;
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
        cb = new ComboBox<>((ObservableList<Component>)Main.dataBundle.getComponents(type));
        cb.setPrefWidth(200);
        
        header.add(cb, 1, 0);
        
        box.getChildren().add(header);
        
        content = new Accordion();
        box.getChildren().add(content);
        
        description = new TitledPane();
        description.setText(rb.getString("description"));
        description.setExpanded(false);
        
        connections = new TitledPane();
        connections.setText(rb.getString("connections"));
        connections.setExpanded(false);
        
        parameters = new TitledPane();
        parameters.setText(rb.getString("parameters"));
        parameters.setExpanded(false);
        
        cb.valueProperty().addListener((observable, oldValue, newValue) -> {
            String desc = cb.getSelectionModel().getSelectedItem().getDescription();
            if(!desc.equals("")) {
                VBox pane = new VBox();
                Label descriptionLabel = new Label(desc);
                descriptionLabel.setWrapText(true);
                VBox.setMargin(descriptionLabel, new Insets(5, 5, 5, 5));
                pane.getChildren().add(descriptionLabel);
                description.setContent(pane);
                description.setVisible(true);
                description.setManaged(true);
            }
            else {
                description.setVisible(false);
                description.setManaged(false);
            }
            Pane connectionsPane = cb.getSelectionModel().getSelectedItem().getSettingsPane();
            if(connectionsPane != null) {
                connections.setContent(connectionsPane);
                connections.setVisible(true);
                connections.setManaged(true);
            }
            else {
                connections.setVisible(false);
                connections.setManaged(false);
            }
            Pane parametersPane = cb.getSelectionModel().getSelectedItem().getParameterGroup().getPane();
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
        box.getChildren().add(connections);
        box.getChildren().add(parameters);
        
        cb.getSelectionModel().selectFirst();
    }
    
    public Pane getPane() {
        return box;
    }
    
    public Component getComponent() {
        return cb.getSelectionModel().getSelectedItem();
    }
    
    public String getType() {
        return type;
    }
    
    public boolean select(String id) {
        for(Component component : cb.getItems()) {
            if(component.getId().equals(id)) {
                cb.getSelectionModel().select(component);
                return true;
            }
        }
        return false;
    }
}