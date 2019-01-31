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

import openwing.core.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Regulator {
    
    boolean isAdded = false;
    
    String name;
    String label;
    String sourceName;
    String sourceType;
    List<RegulatorSetting> settings = new ArrayList<>();
    
    public Regulator(String name, String label, String sourceName, String sourceType) {
        this.label = label;
        this.name = name;
        this.sourceName = sourceName;
        this.sourceType = sourceType;
    }

    public String getLabel() {
        return label;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSourceName() {
        return sourceName;
    }
    
    public String getSourceType() {
        return sourceType;
    }
    
    public abstract String getType();
    
    public List<RegulatorSetting> getSettings() {
        return settings;
    }
    
    public abstract List<Double> getSetOptions();
    
    public abstract void set(double value, int id);

    public abstract List<Variable> getVariables();
    public abstract Map<String, String> getCode();
    public abstract List<String> getLibraries();

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean isAdded) {
        this.isAdded = isAdded;
    }
}
