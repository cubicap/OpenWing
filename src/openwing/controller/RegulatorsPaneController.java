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
import openwing.core.flightmodes.regulator.Regulator;
import openwing.core.flightmodes.regulator.RegulatorSetting;
import openwing.guiutils.DecimalNumberField;
import openwing.guiutils.WholeNumberField;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 */
public class RegulatorsPaneController implements Initializable {
    
    @FXML
    ScrollPane scrollPane;
    @FXML
    VBox vBox;
    
    static VBox box;
    
    //seznam hodnot
    static boolean initialized = false;
    
    public static List<Regulator> regulators = new ArrayList<>();
    
    int length = 0;
    
    public static Map<String, VBox> tables = new HashMap<>();
    
    ResourceBundle rb;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        
        vBox.setFillWidth(true);
        
        box = vBox;
        
        for (int i = 0; i < regulators.size(); i++) {
            addUI(regulators.get(i));
            length++;
        }
        initialized = true;
        
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                while(regulators.size() > length) {
                    if(!regulators.get(length).isAdded()) {
                        addUI(regulators.get(length));
                        regulators.get(length).setAdded(true);
                    }
                    length++;
                }
            }
        }, 1000);
    }
    
    public static void addRegulator(Regulator regulator) {
        regulators.add(regulator);
    }
    
    public void addUI(Regulator regulator) {
        if(!tables.containsKey(regulator.getType())) {
            VBox table = new VBox();
            HBox header = new HBox();
            VBox labelBox = new VBox();
            labelBox.setPrefWidth(200);
            Label sourceLabel = new Label(rb.getString("reg_source"));
            Label nameLabel = new Label(rb.getString("reg_name"));
            nameLabel.setWrapText(true);
            labelBox.getChildren().addAll(sourceLabel, new Separator(), nameLabel);
            HBox.setMargin(labelBox, new Insets(0, 15, 0, 15));
            header.getChildren().add(labelBox);
            for(RegulatorSetting rs : regulator.getSettings()) {
                Label columnLabel = new Label(rb.containsKey(rs.getLabel()) ? rb.getString(rs.getLabel()) : rs.getLabel());
                columnLabel.setPrefWidth(150);
                HBox.setMargin(columnLabel, new Insets(0, 15, 0, 15));
                header.getChildren().add(columnLabel);
            }

            VBox.setMargin(header, new Insets(20, 0, 20, 0));
            table.getChildren().add(header);
            box.getChildren().add(table);
            
            tables.put(regulator.getType(), table);
        }
        
        HBox row = new HBox();
        VBox labelBox = new VBox();
        labelBox.setPrefWidth(200);
        Label sourceLabel = new Label(regulator.getSourceName() + " (" + regulator.getSourceType() + ")");
        Label nameLabel = new Label(regulator.getLabel().length() > 0 ? (regulator.getLabel() + ":") : "");
        nameLabel.setWrapText(true);
        labelBox.getChildren().addAll(sourceLabel, new Separator(), nameLabel);
        HBox.setMargin(labelBox, new Insets(0, 15, 0, 15));
        row.getChildren().add(labelBox);
        
        List<RegulatorSetting> settings = regulator.getSettings();
        
        for(int i = 0; i < settings.size(); i++)  {
            int id = i;
            RegulatorSetting setting = settings.get(i);
            TextField tf;
            switch(setting.getType()) {
                case Parameter.DECIMAL:
                    tf = new DecimalNumberField(Double.parseDouble(setting.getValue().get()), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                    break;
                case Parameter.WHOLE:
                    tf = new WholeNumberField((int)Math.round(Double.parseDouble(setting.getValue().get())), Integer.MIN_VALUE, Integer.MAX_VALUE);
                    break;
                default:
                    tf = new TextField(setting.getValue() + "");
                    break;
            }
            tf.textProperty().bindBidirectional(setting.getValue());

            tf.setPrefWidth(150);
            HBox.setMargin(tf, new Insets(0, 15, 0, 15));
            
            row.getChildren().add(tf);
        }
        row.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(row, new Insets(20, 0, 20, 0));
        
        tables.get(regulator.getType()).getChildren().add(row);
    }
}