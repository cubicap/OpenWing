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

import openwing.core.Function;
import openwing.core.Macro;
import openwing.core.Variable;
import openwing.core.components.connection.Pin;
import openwing.core.components.connection.ConnectionSetting;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import openwing.guiutils.WholeNumberField;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import openwing.core.SaveData;
import java.util.ResourceBundle;

public class PWMReceiver extends Component {
    
    Spinner numChannelsSpinner;
    int shownRows = 0;
    ObservableList<String> axisList;
    List<Channel> channelList = new ArrayList<>();
    
    public PWMReceiver(ResourceBundle rb) {
        super(rb);
        type = "receiver";
        
        setName("PWM Receiver");
        setId("PWMReceiver");
        
        addFunction(new Function("rx_update", "void rx_update();", (
        "void rx_update() {\n" +
                "    if(digitalRead(rxPins[0]))\n" +
                "        while(digitalRead(rxPins[0]));\n" +
                "    while(!digitalRead(rxPins[0]));\n" +
                "    long timer = micros();\n" +
                "    long timeout = micros() + 5000;\n" +
                "    memcpy(rxRead, rxReadPre, sizeof(rxRead));\n" +
                "    int8_t i = rx_channels_num;\n" +
                "    while(i>0 && timeout < micros()) {\n" +
                "        for(uint8_t j = 0; j < sizeof(rxPins)/sizeof(int); j++) {\n" +
                "            if(!rxRead[j] && !digitalRead(rxPins[j])) {\n" +
                "                rxRead[j] = true;\n" +
                "                rxTime[j] = micros() - timer;\n" +
                "                if(rx_inRange(rxTime[j], 1500 - rxCalibration[j][2], 1500 + rxCalibration[j][2]) {\n" + 
                "                    rx_channels[j] = map(rxTime[j], rxCalibration[j][0], rxCalibration[j][1], -1000, 1000);\n" +
                "                }" +
                "                i--;\n" +
                "           }\n" +
                "        }\n" +
                "    }\n" +
                "}"
        )));
        addMacro(new Macro("rx_inRange", "#define rx_inRange(x, low, high) (((high) > (x)) && ((x) > (low)))"));
        addMacro(new Macro("rx_getChannels", "#define rx_getChannels() {}"));
    }
    
    @Override
    public void complete() {
        getFinalVariables().clear();
        getFinalVariables().addAll(getVariables());
        getFinalFunctions().clear();
        getFinalFunctions().addAll(getFunctions());
        getFinalMacros().clear();
        getFinalMacros().addAll(getMacros());
        String rxTimeStartVals = "1500";
        String rxChannelsStartVals = "0";
        String rxReadStartVals = "true";
        String pinsString = channelList.get(0).getPin().getPinNumber() + "";
        String calibrationString = "{" + channelList.get(0).getMin() + ", " + channelList.get(0).getMax() + ", " + 
                                          channelList.get(0).getDeadZone() + "}";
        for(int i = 1; i < (Integer)numChannelsSpinner.getValue(); i++) {
            rxTimeStartVals += ", 1500";
            rxChannelsStartVals += ", 0";
            rxReadStartVals += ", true";
            pinsString += ", " + channelList.get(i).getPin().getPinNumber();
            calibrationString += ", {" + channelList.get(i).getMin() + ", " + channelList.get(i).getMax() + ", " + 
                                        channelList.get(i).getDeadZone() + "}";
        }
        
        for(int i = 0; i < (Integer)numChannelsSpinner.getValue(); i++) {
            getFinalMacros().add(new Macro("rx_channel_" + channelList.get(i).getName().toLowerCase().replaceAll(" ", "_"), "#define rx_channel_" + channelList.get(i).getName().toLowerCase().replaceAll(" ", "_") + " " + i));
        }
        
        getFinalMacros().add(new Macro("rx_channels_num", "#define rx_channels_num " + (Integer)numChannelsSpinner.getValue()));
        getFinalVariables().add(new Variable("rxTime", "int32_t rxTime[" + (Integer)numChannelsSpinner.getValue() + "] = {" + rxTimeStartVals + "};"));
        getFinalVariables().add(new Variable("rx_channels", "int32_t rx_channels[" + (Integer)numChannelsSpinner.getValue() + "] = {" + rxChannelsStartVals + "};"));
        getFinalVariables().add(new Variable("rxRead", "bool rxRead[" + (Integer)numChannelsSpinner.getValue() + "] = {" + rxReadStartVals + "};"));
        getFinalVariables().add(new Variable("rxReadPre", "bool rxReadPre[" + (Integer)numChannelsSpinner.getValue() + "] = {" + rxReadStartVals + "};"));
        getFinalVariables().add(new Variable("rxPins", "const int rxPins[] = {" + pinsString + "};"));
        getFinalVariables().add(new Variable("rxCalibration", "const int rxCalibration[" + (Integer)numChannelsSpinner.getValue() + "][3] = {" + calibrationString + "};"));
    }
    
    @Override
    public Pane getSettingsPane() {
        VBox layout = new VBox();
        
        HBox numChannels = new HBox();
        numChannels.setAlignment(Pos.CENTER_LEFT);
        Label numChannelsLabel = new Label(rb.getString("num_channels"));
        numChannelsSpinner = new Spinner(5, 20, 5, 1);
        HBox.setMargin(numChannelsLabel, new Insets(0, 15, 0, 15));
        HBox.setMargin(numChannelsSpinner, new Insets(0, 15, 0, 15));
        numChannels.getChildren().add(numChannelsLabel);
        numChannels.getChildren().add(numChannelsSpinner);
        
        VBox.setMargin(numChannels, new Insets(30, 0, 0, 0));
        layout.getChildren().add(numChannels);
        
        HBox header = new HBox();
        Label nameLabel = new Label(rb.getString("channel_label"));
        Label minLabel = new Label(rb.getString("min_pwm_len"));
        Label maxLabel = new Label(rb.getString("max_pwm_len"));
        Label deadZoneLabel = new Label(rb.getString("deadzone"));
        Label pinLabel = new Label(rb.getString("pin"));
        nameLabel.setPrefWidth(130);
        minLabel.setPrefWidth(200);
        maxLabel.setPrefWidth(200);
        deadZoneLabel.setPrefWidth(200);
        pinLabel.setPrefWidth(80);
        HBox.setMargin(nameLabel, new Insets(0, 15, 0, 15));
        HBox.setMargin(minLabel, new Insets(0, 15, 0, 15));
        HBox.setMargin(maxLabel, new Insets(0, 15, 0, 15));
        HBox.setMargin(deadZoneLabel, new Insets(0, 15, 0, 15));
        HBox.setMargin(pinLabel, new Insets(0, 15, 0, 15));
        header.getChildren().add(nameLabel);
        header.getChildren().add(minLabel);
        header.getChildren().add(maxLabel);
        header.getChildren().add(deadZoneLabel);
        header.getChildren().add(pinLabel);
        
        VBox.setMargin(header, new Insets(30, 0, 0, 0));
        layout.getChildren().add(header);
        
        numChannelsSpinner.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            while(channelList.size() < (Integer)newValue) {
                channelList.add(new Channel("aux" + (channelList.size() - 3), rb.getString("aux") + (channelList.size() - 3)));
                channelList.get(channelList.size() - 1).add(layout);
                shownRows++;
            }
            for(int i = 0; i < channelList.size() && shownRows < (Integer)newValue; i++) {
                if(!channelList.get(i).isVisible()) {
                    channelList.get(i).setVisible(true);
                    shownRows++;
                }
            }
            for(int i = channelList.size() - 1; i > 0 && shownRows > (Integer)newValue; i--) {
                if(channelList.get(i).isVisible()) {
                    channelList.get(i).setVisible(false);
                    shownRows--;
                }
            }
        });
        channelList.add(new Channel("pitch", rb.getString("pitch")));
        channelList.get(channelList.size() - 1).add(layout);
        channelList.add(new Channel("roll", rb.getString("roll")));
        channelList.get(channelList.size() - 1).add(layout);
        channelList.add(new Channel("throttle", rb.getString("throttle")));
        channelList.get(channelList.size() - 1).add(layout);
        channelList.add(new Channel("yaw", rb.getString("yaw")));
        channelList.get(channelList.size() - 1).add(layout);
        channelList.add(new Channel("mode_switch", rb.getString("mode_switch")));
        channelList.get(channelList.size() - 1).add(layout);
        shownRows = 5;
        while(channelList.size() < 5) {
            channelList.add(new Channel("aux" + (channelList.size() - 3), rb.getString("aux") + (channelList.size() - 3)));
            channelList.get(channelList.size() - 1).add(layout);
            shownRows++;
        }
        return layout;
    }
    
    @Override
    public void save(SaveData data) {
        data.add(getId() + "_num", shownRows + "");
        for(int i = 0; i < shownRows; i++) {
            data.add(getId() + "_" + i + "_min", channelList.get(i).getMin() + "");
            data.add(getId() + "_" + i + "_max", channelList.get(i).getMax()+ "");
            data.add(getId() + "_" + i + "_pin", channelList.get(i).getPin().getSetOptions().get(0) + "");
        }
    }
    
    @Override
    public void load(SaveData data) {
        String d = data.get(getId() + "_num");
        if(d != null) {
            int showRows = Integer.parseInt(d);
            numChannelsSpinner.getValueFactory().setValue(showRows);
        }
        for(int i = 0; i < shownRows; i++) {
            d = data.get(getId() + "_" + i + "_min");
            if(d != null) {
                channelList.get(i).setMin(Integer.parseInt(d));
            }
            d = data.get(getId() + "_" + i + "_max");
            if(d != null) {
                channelList.get(i).setMax(Integer.parseInt(d));
            }
            d = data.get(getId() + "_" + i + "_pin");
            if(d != null) {
                channelList.get(i).setPin(Integer.parseInt(d));
            }
        }
    }
}

class Channel {
    WholeNumberField min;
    WholeNumberField max;
    WholeNumberField middle;
    WholeNumberField deadZone;
    Pin pin;
    HBox rowBox;
    String name;
    boolean visible = true;
    
    public Channel(String name, String label) {
        this.name = name;
        
        Label nameLabel = new Label(label + ":");
        min = new WholeNumberField(1000, 500, 1500);
        max = new WholeNumberField(2000, 1500, 2500);
        deadZone = new WholeNumberField(0, 0, 200);
        
        pin = new Pin("any", name + "_PIN");
        List<ConnectionSetting> settings = pin.getSettings();
        ConnectionSetting set = settings.get(0);

        ComboBox<String> cb = new ComboBox(set.getOptions());
        cb.getSelectionModel().selectFirst();
        cb.setMaxWidth(Double.POSITIVE_INFINITY);
        GridPane.setMargin(cb, new Insets(2, 0, 2, 0));
        cb.valueProperty().bindBidirectional(set.getValue());
        
        nameLabel.setPrefWidth(130);
        min.setPrefWidth(200);
        max.setPrefWidth(200);
        deadZone.setPrefWidth(200);
        cb.setPrefWidth(80);
        HBox.setMargin(nameLabel, new Insets(0, 15, 0, 15));
        HBox.setMargin(min, new Insets(0, 15, 0, 15));
        HBox.setMargin(max, new Insets(0, 15, 0, 15));
        HBox.setMargin(deadZone, new Insets(0, 15, 0, 15));
        HBox.setMargin(cb, new Insets(0, 15, 0, 15));
        rowBox = new HBox();
        rowBox.getChildren().add(nameLabel);
        rowBox.getChildren().add(min);
        rowBox.getChildren().add(max);
        rowBox.getChildren().add(deadZone);
        rowBox.getChildren().add(cb);
        rowBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox.setMargin(rowBox, new Insets(20, 0, 20, 0));
    }
    
    public void add(VBox layout) {
        layout.getChildren().add(rowBox);
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
        rowBox.setVisible(visible);
        rowBox.setManaged(visible);
    }
    
    public boolean isVisible() {
        return visible;
    }

    public Pin getPin() {
        return pin;
    }
    
    public int getMin() {
        return Integer.parseInt(min.getText());
    }
    
    public int getMax() {
        return Integer.parseInt(max.getText());
    }
    
    public int getDeadZone() {
        return Integer.parseInt(deadZone.getText());
    }
    
    public String getName() {
        return name;
    }
    
    public void setMin(int min) {
        this.min.setText(min + "");
    }
    
    public void setMax(int max) {
        this.max.setText(max + "");
    }
    
    public void setPin(int pin) {
        this.pin.set(pin, 0);
    }
}
