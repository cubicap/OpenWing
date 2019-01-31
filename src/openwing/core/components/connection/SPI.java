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

public class SPI extends Connection{

    List<String> dinOptionsBackList = new ArrayList<>();
    List<String> doutOptionsBackList = new ArrayList<>();
    List<String> sckOptionsBackList = new ArrayList<>();
    List<String> csOptionsBackList = new ArrayList<>();

    ObservableList<String> dinOptions = FXCollections.observableArrayList(dinOptionsBackList);
    ObservableList<String> doutOptions = FXCollections.observableArrayList(doutOptionsBackList);
    ObservableList<String> sckOptions = FXCollections.observableArrayList(sckOptionsBackList);
    ObservableList<String> csOptions = FXCollections.observableArrayList(csOptionsBackList);
    
    StringProperty din = new SimpleStringProperty();
    StringProperty dout = new SimpleStringProperty();
    StringProperty sck = new SimpleStringProperty();
    StringProperty cs = new SimpleStringProperty();
    
    public SPI() {
        super("SPI");
        
        dinOptions.add(12 + "");
        dinOptions.add(8 + "");
        
        doutOptions.add(11 + "");
        doutOptions.add(7 + "");
        
        sckOptions.add(13 + "");
        sckOptions.add(14 + "");
        
        csOptions.add(9 + "");
        csOptions.add(15 + "");
        csOptions.add(20 + "");
        csOptions.add(21 + "");
        
        din.setValue(dinOptions.get(0));
        dout.setValue(doutOptions.get(0));
        sck.setValue(sckOptions.get(0));
        cs.setValue(csOptions.get(0));
        
        settings.add(new ConnectionSetting("DIN", dinOptions, "Pin connected to the DOUT on the component", din));
        settings.add(new ConnectionSetting("DOUT", doutOptions, "Pin connected to the DIN on the component", dout));
        settings.add(new ConnectionSetting("SCK", sckOptions, "Pin connected to the SCK on the component", sck));
        settings.add(new ConnectionSetting("CS", csOptions, "Pin connected to the CS on the component", cs));
    }

    @Override
    public void set(int value, int id) {
        switch(id) {
            case 0:
                din.setValue(dinOptions.get(value));
                break;
            case 1:
                dout.setValue(doutOptions.get(value));
                break;
            case 2:
                sck.setValue(sckOptions.get(value));
                break;
            case 3:
                cs.setValue(csOptions.get(value));
                break;
        }
    }
    
    @Override
    public List<Macro> getMacros() {
        List<Macro> macros = new ArrayList<>();
        
        macros.add(new Macro("DIN", "#define DIN " + din.get()));
        macros.add(new Macro("DOUT", "#define DOUT " + dout.get()));
        macros.add(new Macro("CS", "#define CS " + cs.get()));
        macros.add(new Macro("SCK", "#define SCK " + sck.get()));
        
        return macros;
    }

    @Override
    public List<Integer> getSetOptions() {
        List<Integer> options = new ArrayList<>();
        options.add(dinOptions.indexOf(din.getValue()));
        options.add(doutOptions.indexOf(dout.getValue()));
        options.add(sckOptions.indexOf(sck.getValue()));
        options.add(csOptions.indexOf(cs.getValue()));
        return options;
    }
}
