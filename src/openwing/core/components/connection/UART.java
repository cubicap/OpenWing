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
import openwing.util.WholeNumbersSet;
import openwing.util.WholeRange;

public class UART extends Connection {

    List<Serial> serials = new ArrayList<>();
    
    ObservableList<String> names = FXCollections.observableArrayList();
    ObservableList<String> rxs = FXCollections.observableArrayList();
    ObservableList<String> txs = FXCollections.observableArrayList();
    
    StringProperty rx = new SimpleStringProperty();
    StringProperty tx = new SimpleStringProperty();
    
    StringProperty serial = new SimpleStringProperty();
    int serialId = 0;

    public UART(boolean allowUSBSerial) {

        super("SERIAL");
        if(allowUSBSerial) {
            serials.add(new Serial("USB Serial", new WholeNumbersSet(), new WholeNumbersSet(), -1, -1, 0, "no"));
        }
        WholeNumbersSet serial1RxRestrictions = new WholeNumbersSet();
        serial1RxRestrictions.add(new WholeNumber(0));
        serial1RxRestrictions.add(new WholeNumber(5));
        WholeNumbersSet serial1TxRestrictions = new WholeNumbersSet();
        serial1TxRestrictions.add(new WholeNumber(1));
        serial1TxRestrictions.add(new WholeNumber(21));
        serials.add(new Serial("Serial1", serial1RxRestrictions, serial1TxRestrictions, -1, -1, 1, "no"));
        
        WholeNumbersSet serial2RxRestrictions = new WholeNumbersSet();
        serial2RxRestrictions.add(new WholeNumber(9));
        serial2RxRestrictions.add(new WholeNumber(26));
        WholeNumbersSet serial2TxRestrictions = new WholeNumbersSet();
        serial2TxRestrictions.add(new WholeNumber(10));
        serial2TxRestrictions.add(new WholeNumber(31));
        serials.add(new Serial("Serial2", serial2RxRestrictions, serial2TxRestrictions, -1, -1, 1, "no"));
        
        WholeNumbersSet serial3RxRestrictions = new WholeNumbersSet();
        serial3RxRestrictions.add(new WholeNumber(7));
        WholeNumbersSet serial3TxRestrictions = new WholeNumbersSet();
        serial3TxRestrictions.add(new WholeNumber(8));
        serials.add(new Serial("Serial3", serial3RxRestrictions, serial3TxRestrictions, -1, -1, 1, "no"));
        
        WholeNumbersSet swSerialRestrictions = new WholeNumbersSet();
        swSerialRestrictions.add(new WholeRange(0, 33));
        serials.add(new Serial("SoftwareSerial", swSerialRestrictions, swSerialRestrictions, -1, -1, 2, "no"));
        
        for(Serial s : serials){
            names.add(s.getName());
        }
        for(Integer i : serials.get(serialId).getRXRestrictions().getValues()) {
            rxs.add(i.toString());
        }
        for(Integer i : serials.get(serialId).getTXRestrictions().getValues()) {
            txs.add(i.toString());
        }
        
        serial.addListener((observable, oldValue, newValue) -> {
            serialId = names.indexOf(newValue);
            List<String> rxList = new ArrayList<>();
            for(Integer i : serials.get(serialId).getRXRestrictions().getValues()) {
                rxList.add(i.toString());
            }
            rxs.setAll(rxList);
            List<String> txList = new ArrayList<>();
            for(Integer i : serials.get(serialId).getTXRestrictions().getValues()) {
                txList.add(i.toString());
            }
            txs.setAll(txList);
        });
        
        settings.add(new ConnectionSetting("Serial", names, "Serial connected to the component", serial));
        settings.add(new ConnectionSetting("RX", rxs, "RX pin", rx));
        settings.add(new ConnectionSetting("TX", txs, "TX pin", tx));
    }
        
    @Override
    public void set(int value, int id) {
        switch(id) {
            case 0:
                serial.setValue(names.get(value));
                break;
            case 1:
                rx.setValue(rxs.get(value));
                break;
            case 2:
                tx.setValue(txs.get(value));
                break;
        }
    }
    
    @Override
    public List<Macro> getMacros() {
        List<Macro> macros = new ArrayList<>();
        
        macros.add(new Macro(getName(), "#define " + getName() + " " + serial.getValue()));
        macros.add(new Macro("RX", "#define RX " + rx.get()));
        macros.add(new Macro("TX", "#define TX " + tx.get()));
        
        return macros;
    }

    @Override
    public List<Integer> getSetOptions() {
        List<Integer> options = new ArrayList<>();
        options.add(rxs.indexOf(rx.getValue()));
        options.add(txs.indexOf(tx.getValue()));
        options.add(names.indexOf(serial.getValue()));
        return options;
    }
}