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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * FXML Controller class
 */
public class TerminalPaneController implements Initializable {
    
    static int NORMAL = 0;
    static int ERR = 1;
    
    @FXML
    TextArea textArea;
    
    @FXML
    Button sendButton;
    
    @FXML
    TextField input;
    
    @FXML
    CheckBox lineWrapCheckbox;
    
    List<String> messages = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sendButton.setOnAction((event) -> {
            messages.add(input.getText() + "\n");
            input.clear();
        });
        
        textArea.wrapTextProperty().bindBidirectional(lineWrapCheckbox.selectedProperty());
        
        input.setOnKeyPressed((event) -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                messages.add(input.getText() + "\n");
                input.clear();
            }
        });
    }
    
    public void write(String text) {
        Platform.runLater(() -> {
            textArea.appendText(text);
        });
    }
    
    public String read() {
        String message = messages.get(0);
        messages.remove(0);
        return message;
    }
    
    public int available() {
        return messages.size();
    }
    
    public void connect(String port, int baudRate) {
        SerialPort serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(baudRate, 
                         SerialPort.DATABITS_8,
                         SerialPort.STOPBITS_1,
                         SerialPort.PARITY_NONE);
            serialPort.addEventListener((serialPortEvent) -> {
                if(serialPortEvent.isRXCHAR()) {
                    try {
                        write(serialPort.readString());
                    } catch (SerialPortException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (SerialPortException ex) {
            ex.printStackTrace();
        }
    }
}
