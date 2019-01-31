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

import javafx.scene.control.Tab;

public class TerminalTab {
    
    Tab tab;
    String title;
    String key;
    TerminalPaneController controller;
    boolean added = false;
    
    public TerminalTab(Tab tab, TerminalPaneController controller, String title, String key) {
        this.controller = controller;
        this.tab = tab;
        this.title = title;
        this.key = key;
    }
    
    public TerminalPaneController getController() {
        return controller;
    }

    public Tab getTab() {
        return tab;
    }
}
