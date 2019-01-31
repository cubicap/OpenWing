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

import openwing.core.Parameter;
import openwing.core.ParameterGroup;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 */
public class ParametersListPaneController implements Initializable {
    
    @FXML
    ScrollPane scrollPane;
    @FXML
    VBox vBox;
    
    static VBox box;
    
    //seznam hodnot
    static boolean initialized = false;
    
    public static List<ParameterGroup> parameterGroups = new ArrayList<>();
    
    static ParameterGroup unorganizedParameters = new ParameterGroup("Other");
    static int lastGroups = 0;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vBox.setFillWidth(true);
        
        box = vBox;
        Pane unorganizedPane = unorganizedParameters.getPane();
        if(unorganizedPane != null) {
            box.getChildren().add(unorganizedPane);
            lastGroups++;
        }
        for (int i = 0; i < parameterGroups.size(); i++) {
            Pane parameterPane = parameterGroups.get(i).getPane();
            if(parameterPane != null) {
                box.getChildren().add(box.getChildren().size() - lastGroups, parameterPane);
            }
        }
        initialized = true;
    }
    
    public static void addParameter(Parameter parameter) {
        if(initialized) {
            unorganizedParameters.add(parameter);
        }
        else {
            unorganizedParameters.add(parameter);
        }
    }
    
    public static void addParameterGroup(ParameterGroup parameterGroup) {
        if(initialized) {
            parameterGroups.add(parameterGroup);
            Pane parameterPane = parameterGroup.getPane();
            if(parameterPane != null) {
                box.getChildren().add(box.getChildren().size() - lastGroups, parameterPane);
            }
        }
        else {
            parameterGroups.add(parameterGroup);
        }
    }
}