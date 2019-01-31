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

import openwing.core.components.Component;
import openwing.core.components.PWMReceiver;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import openwing.Main;

/**
 * FXML Controller class
 */
public class ReceiverPaneController implements Initializable {

    Component receiver;
    
    @FXML
    ScrollPane receiverContent;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        receiver = new PWMReceiver(rb);
        Main.dataBundle.addComponent(receiver);
        receiverContent.setContent(receiver.getSettingsPane());
    }
    
    public Component getReceiver() {
        return receiver;
    }
}
