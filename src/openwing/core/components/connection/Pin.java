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
package openwing.core.components.connection;

import openwing.core.Macro;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import openwing.util.WholeNumber;
import openwing.util.WholeRange;
import openwing.util.WholeNumbersSet;


public class Pin extends Connection {
    
    WholeNumbersSet restriction;
    
    List<Integer> values = new ArrayList<>();
    
    ObservableList<String> options = FXCollections.observableArrayList();
    
    StringProperty pin = new SimpleStringProperty();
    
    /**
     *
     * @param type must be one of the following:<br>
     *      pwm, interrupt, any, analog, cs
     * @param label name of the pin
     */
    public Pin(String type, String label) {
        super(label);
        restriction = new WholeNumbersSet();
        switch(type) {
            case "pwm":
                restriction.add(new WholeRange(3, 6, ""));
                restriction.add(new WholeRange(9, 10, ""));
                restriction.add(new WholeRange(20, 23, ""));
                restriction.add(new WholeNumber(25, ""));
                restriction.add(new WholeNumber(32, ""));
                break;
            case "interrupt":
            case "any":
                restriction.add(new WholeRange(0, 33, ""));
                break;
            case "analog":
                restriction.add(new WholeRange(0, 20, ""));
            case "cs":
                restriction.add(new WholeNumber(9, ""));
                restriction.add(new WholeNumber(10, ""));
                restriction.add(new WholeNumber(15, ""));
                restriction.add(new WholeNumber(20, ""));
                restriction.add(new WholeNumber(31, ""));
                break;/*
            case "dac":
                restriction.add(new WholeNumber(14, ""));
                break;*/
        }
        
        values.addAll(restriction.getValues());
        
        for(Integer i : restriction.getValues()) {
            options.add(i.toString());
        }
        pin.setValue(options.get(0));
        settings.add(new ConnectionSetting(label, options, "The pin", pin));
    }
    
    public Pin(WholeNumbersSet restriction, String label) {
        super(label);
        this.restriction = restriction;
        ObservableList<String> values = FXCollections.observableArrayList();
        for(Integer i : restriction.getValues()) {
            values.add(i.toString());
        }
        pin.setValue(options.get(0));
        settings.add(new ConnectionSetting(label, values, "The pin", pin));
    }

    @Override
    public void set(int value, int id) {
        pin.setValue(options.get(value));
    }
    
    @Override
    public List<Macro> getMacros() {
        List<Macro> macros = new ArrayList<>();
        
        macros.add(new Macro("" + getName(), "#define " + getName() + " " + pin.get()));
        
        return macros;
    }
    
    public int getPinNumber() {
        return Integer.parseInt(pin.getValue());
    }

    @Override
    public List<Integer> getSetOptions() {
        List<Integer> options = new ArrayList<>();
        options.add(this.options.indexOf(pin.getValue()));
        return options;
    }
}
