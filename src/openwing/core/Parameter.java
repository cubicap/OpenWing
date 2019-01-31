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
package openwing.core;

import openwing.guiutils.DecimalNumberField;
import openwing.guiutils.WholeNumberField;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import openwing.util.DecimalNumbersSet;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class Parameter {
    public static final int DECIMAL = 0;
    public static final int WHOLE = 1;
    
    String name;
    double value;
    String units;
    DecimalNumbersSet values;
    String description;
    boolean added;
    int type;
    
    List<ParameterGUI> guis = new ArrayList<>();
    
    public Parameter(String name, double value, String units, DecimalNumbersSet values, String description, int type) {
        this.name = name;
        this.value = value;
        this.units = units;
        this.values = values;
        this.description = description;
        this.type = type;
    }

    public void setValue(String newValue) {
        switch(type) {
            case DECIMAL:
                guis.get(0).input.setText("" + Double.parseDouble(newValue));
                break;
            case WHOLE:
                guis.get(0).input.setText("" + Math.round(Double.parseDouble(newValue)));
                break;
            default:
                guis.get(0).input.setText("" + newValue);
                break;
        }
    }
    
    public void add(GridPane grid, int row) {
        ParameterGUI gui = new ParameterGUI();
        
        gui.nameLabel = new Label(name);
        switch(type) {
            case DECIMAL:
                gui.input = new DecimalNumberField(value, values);
                break;
            case WHOLE:
                gui.input = new WholeNumberField((int)Math.round(value), values);
                break;
            default:
                gui.input = new TextField("" + value);
                break;
        }
        
        gui.unitsLabel = new Label(units);
        gui.valuesLabel = new Label(values.getLabel());
        gui.descriptionLabel = new Label(description);
        
        gui.nameLabel.setWrapText(true);
        gui.nameLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setMargin(gui.nameLabel, new Insets(2, 5, 2, 5));
        gui.unitsLabel.setWrapText(true);
        gui.unitsLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setMargin(gui.unitsLabel, new Insets(2, 5, 2, 5));
        gui.valuesLabel.setWrapText(true);
        gui.valuesLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setMargin(gui.valuesLabel, new Insets(2, 5, 2, 5));
        gui.descriptionLabel.setWrapText(true);
        gui.descriptionLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setMargin(gui.descriptionLabel, new Insets(2, 5, 2, 5));
        
        grid.add(gui.nameLabel, 0, row);
        grid.add(gui.input, 1, row);
        grid.add(gui.unitsLabel, 2, row);
        grid.add(gui.valuesLabel, 3, row);
        grid.add(gui.descriptionLabel, 4, row);
        
        for(ParameterGUI g : guis) {
            gui.input.textProperty().bindBidirectional(g.input.textProperty());
        }
        
        guis.add(gui);
    }

    public String getName() {
        return name;
    }
    
    public double getValue() {
        switch(type) {
            case DECIMAL:
                return Double.parseDouble(guis.get(0).input.getText());
            case WHOLE:
                return Integer.parseInt(guis.get(0).input.getText());
            default:
                return Double.parseDouble(guis.get(0).input.getText());
        }
    }
}

class ParameterGUI {
    Label nameLabel;
    TextField input;
    Label unitsLabel;
    Label valuesLabel;
    Label descriptionLabel;
}