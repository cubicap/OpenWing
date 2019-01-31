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
package openwing.core.components;

import openwing.core.Buildable;
import openwing.core.Function;
import openwing.core.Macro;
import openwing.core.Parameter;
import openwing.core.Variable;
import openwing.core.components.connection.Connection;
import openwing.core.components.connection.ConnectionSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import openwing.core.SaveData;
import openwing.core.SaveLoadAble;
import java.util.ResourceBundle;

public class Component extends Buildable implements SaveLoadAble {
    
    String type;
    
    List<Connection> connections = new ArrayList<>();
    
    Pane settingsPane;
    boolean generated = false;
    
    ResourceBundle rb;
    
    public Component(ResourceBundle rb) {
        this.rb = rb;
    }
    
    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    public List<Connection> getConnections() {
        return connections;
    }
    
    public Pane getSettingsPane() {
        if(connections.size() > 0) {
            if(!generated) {
                GridPane grid = new GridPane();
                grid.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

                ColumnConstraints cc1 = new ColumnConstraints();
                cc1.setPercentWidth(50);
                ColumnConstraints cc2 = new ColumnConstraints();
                cc2.setHalignment(HPos.RIGHT);
                cc2.setPercentWidth(50);
                grid.getColumnConstraints().addAll(cc1, cc2);

                int row = 0; 
                for(int i = 0; i < connections.size(); i++) {
                    Connection connection = connections.get(i);
                    List<ConnectionSetting> settings = connection.getSettings();
                    for(int ii = 0; ii < settings.size(); ii++) {
                        ConnectionSetting set = settings.get(ii);

                        Label label = new Label(set.getLabel());

                        ComboBox<String> cb = new ComboBox(set.getOptions());
                        cb.valueProperty().bindBidirectional(set.getValue());
                        if(cb.getSelectionModel().getSelectedIndex() == -1) {
                            cb.getSelectionModel().selectFirst();
                        }
                        cb.setMaxWidth(Double.POSITIVE_INFINITY);
                        GridPane.setMargin(cb, new Insets(2, 0, 2, 0));

                        grid.add(label, 0, row);
                        grid.add(cb, 1, row++);
                    }
                }
                settingsPane = grid;
            }
            return settingsPane;
        }
        else {
            return null;
        }
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public void complete() {
        getFinalVariables().clear();
        getFinalMacros().clear();
        getFinalFunctions().clear();
        getFinalCode().clear();
        
        for(Parameter parameter : getParameters()) {
            getFinalMacros().add(new Macro(parameter.getName(), "#define " + parameter.getName() + " " + parameter.getValue()));
        }
        for(Connection connection : connections) {
            getFinalMacros().addAll(connection.getMacros());
        }
        
        getFinalVariables().addAll(getVariables());
        getFinalFunctions().addAll(getFunctions());
        getFinalMacros().addAll(getMacros());
        getFinalCode().putAll(getCode());
        
        for(Parameter parameter : getParameters()) {
            
            for(Variable variable : getFinalVariables()) {
                variable.setDeclaration(variable.getDeclaration().replaceAll("\\b" + parameter.getName() + "\\b", getId() + parameter.getName()));
            }
            
            for(Function function : getFinalFunctions()) {
                function.setDeclaration(function.getDeclaration().replaceAll("\\b" + parameter.getName() + "\\b", getId() + parameter.getName()));
                function.setDefinition(function.getDefinition().replaceAll("\\b" + parameter.getName() + "\\b", getId() + parameter.getName()));
            }
            
            for(Macro macro : getFinalMacros()) {
                macro.setDefinition(macro.getDefinition().replaceAll("\\b" + parameter.getName() + "\\b", getId() + parameter.getName()));
            }
            
            for (Map.Entry<String, String> part : getFinalCode().entrySet()) {
                String value = part.getValue();
                part.setValue(part.getValue().replaceAll("\\b" + parameter.getName() + "\\b", getId() + parameter.getName()));
            }
        }
        
        for(Connection connection : connections) {
            List<Macro> ms = connection.getMacros();
            for(Macro m : ms) {
                for(Variable variable : getFinalVariables()) {
                    variable.setDeclaration(variable.getDeclaration().replaceAll("\\b" + m.getName() + "\\b", getId() + m.getName()));
                }

                for(Function function : getFinalFunctions()) {
                    function.setDeclaration(function.getDeclaration().replaceAll("\\b" + m.getName() + "\\b", getId() + m.getName()));
                    function.setDefinition(function.getDefinition().replaceAll("\\b" + m.getName() + "\\b", getId() + m.getName()));
                }

                for(Macro macro : getFinalMacros()) {
                    macro.setDefinition(macro.getDefinition().replaceAll("\\b" + m.getName() + "\\b", getId() + m.getName()));
                }
            
                for (Map.Entry<String, String> part : getFinalCode().entrySet()) {
                    String value = part.getValue();
                    part.setValue(part.getValue().replaceAll("\\b" + m.getName() + "\\b", getId() + m.getName()));
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void save(SaveData data) {        
        for(Parameter parameter : getParameters()) {
            data.add(getId() + "_" + parameter.getName(), parameter.getValue() + "");
        }

        for(Connection connection : getConnections()) {
            List<Integer> setOptions = connection.getSetOptions();
            for(int i = 0; i < setOptions.size(); i++) {
                data.add(getId() + "_" + connection.getName() + "_" + i, setOptions.get(i) + "");
            }
        }
    }

    @Override
    public void load(SaveData data) {
        for(Parameter parameter : getParameters()) {
            String d = data.get(getId() + "_" + parameter.getName());
            if(d != null) {
                parameter.setValue(d);
            }
        }

        for(Connection connection : getConnections()) {
            for(int i = 0; i < connection.getSettings().size(); i++) {
                String d = data.get(getId() + "_" + connection.getName() + "_" + i);
                if(d != null) {
                    connection.set(Integer.parseInt(d), i);
                }
            }
        }
    }
}