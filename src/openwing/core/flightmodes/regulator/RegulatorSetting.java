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
package openwing.core.flightmodes.regulator;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RegulatorSetting {
    
    public static final int DECIMAL = 0;
    public static final int WHOLE = 1;
    
    String label;
    int type;
    String note;
    
    StringProperty value = new SimpleStringProperty();
    
    public RegulatorSetting(String label, int type, String note, StringProperty value) {
        this.label = label;
        this.type = type;
        this.note = note;
        this.value.bindBidirectional(value);
    }
    
    public String getLabel() {
        return label;
    }
    
    public int getType() {
        return type;
    }
    
    public StringProperty getValue() {
        return value;
    }
    
    public String getNote() {
        return note;
    }
}
