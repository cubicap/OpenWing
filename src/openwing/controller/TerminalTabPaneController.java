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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 */
public class TerminalTabPaneController implements Initializable {

    Map<String, TerminalTab> tabs = new HashMap<>();
    
    ResourceBundle rb;
    
    @FXML
    TabPane tabPane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
    }
    
    public void addTerminal(String title, String key) {
        try {
            Pane content;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/openwing/view/TerminalPane.fxml"), rb);
            content = loader.load();
            TerminalPaneController controller = loader.getController();

            Tab tab = new Tab(title, content);
            
            TerminalTab tt = new TerminalTab(tab, controller, title, key);
            tabs.put(key, tt);
            
            tabPane.getTabs().add(tab);
            tt.added = true;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public TerminalTab getTerminal(String key) {
        return tabs.get(key);
    }

    public TabPane getTabPane() {
        return tabPane;
    }
}