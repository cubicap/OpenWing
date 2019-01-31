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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class ConnectionSetting {
    String label;
    ObservableList<String> options;
    String note;
    StringProperty value = new SimpleStringProperty();
    
    public ConnectionSetting(String label, ObservableList<String> options, String note, StringProperty value) {
        this.label = label;
        this.options = options;
        this.note = note;
        this.value.bindBidirectional(value);
    }
    
    public String getLabel() {
        return label;
    }
    
    public ObservableList<String> getOptions() {
        return options;
    }
    
    public String getNote() {
        return note;
    }
    
    public StringProperty getValue() {
        return value;
    }
}
