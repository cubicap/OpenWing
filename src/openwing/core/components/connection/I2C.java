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

public class I2C extends Connection {

    List<String> sdaOptionsBackList = new ArrayList<>();
    List<String>  sclOptionsBackList = new ArrayList<>();
    
    ObservableList<String> sdaOptions = FXCollections.observableArrayList(sdaOptionsBackList);
    ObservableList<String>  sclOptions = FXCollections.observableArrayList(sclOptionsBackList);
    
    StringProperty sda = new SimpleStringProperty();
    StringProperty scl = new SimpleStringProperty();
    
    public I2C() {
        super("I2C");
        
        sdaOptions.add(18 + "");
        sdaOptions.add(17 + "");
        
        sclOptions.add(19 + "");
        sclOptions.add(16 + "");
        
        sda.setValue(sdaOptions.get(0));
        scl.setValue(sclOptions.get(0));
        
        settings.add(new ConnectionSetting("SDA", sdaOptions, "Pin connected to the SDA on the component", sda));
        settings.add(new ConnectionSetting("SCL", sclOptions, "Pin connected to the SCL on the component", scl));
    }
    
    @Override
    public void set(int value, int id) {
        switch(id) {
            case 0:
                sda.set(sdaOptions.get(value));
                break;
            case 1:
                scl.set(sclOptions.get(value));
                break;
        }
    }
    
    @Override
    public List<Macro> getMacros() {
        List<Macro> macros = new ArrayList<>();
        
        macros.add(new Macro("SDA", "#define " + "SDA " + sda.get()));
        macros.add(new Macro("SCL", "#define " + "SCL " + scl.get()));
        
        return macros;
    }

    @Override
    public List<Integer> getSetOptions() {
        List<Integer> options = new ArrayList<>();
        options.add(sdaOptions.indexOf(sda.getValue()));
        options.add(sclOptions.indexOf(scl.getValue()));
        return options;
    }
}
