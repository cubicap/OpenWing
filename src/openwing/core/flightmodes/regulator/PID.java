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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PID extends Regulator {

    Map<String, String> code = new HashMap<>();
    List<Variable> variables = new ArrayList<>();
    List<String> libraries = new ArrayList<>();
    
    StringProperty p = new SimpleStringProperty();
    StringProperty i = new SimpleStringProperty();
    StringProperty d = new SimpleStringProperty();
    StringProperty min = new SimpleStringProperty();
    StringProperty max = new SimpleStringProperty();
    StringProperty period = new SimpleStringProperty();
    
    
    
    public PID(String name, String label, double p, double i, double d, double min, double max, double period, String sourceName, String sourceType) {
        super(name, label, sourceName, sourceType);
        
        this.p.set(p + "");
        this.i.set(i + "");
        this.d.set(d + "");
        this.min.set(min + "");
        this.max.set(max + "");
        this.period.set(period + "");
        
        libraries.add("pid.h");
        
        settings.add(new RegulatorSetting("p_val", RegulatorSetting.DECIMAL, "Proportional multiplier", this.p));
        settings.add(new RegulatorSetting("i_val", RegulatorSetting.DECIMAL, "Integral multiplier", this.i));
        settings.add(new RegulatorSetting("d_val", RegulatorSetting.DECIMAL, "Derivative multiplier", this.d));
        settings.add(new RegulatorSetting("min_out", RegulatorSetting.DECIMAL, "Minimal output", this.min));
        settings.add(new RegulatorSetting("max_out", RegulatorSetting.DECIMAL, "Maximal output", this.max));
        settings.add(new RegulatorSetting("cycle_period", RegulatorSetting.DECIMAL, "Cycle period", this.period));
    }

    @Override
    public void set(double value, int id) {
        switch(id) {
            case 0:
                p.set(value + "");
                break;
            case 1:
                i.set(value + "");
                break;
            case 2:
                d.set(value + "");
                break;
            case 3:
                min.set(value + "");
                break;
            case 4:
                max.set(value + "");
                break;
            case 5:
                period.set(value + "");
                break;
        }
    }

    @Override
    public List<Variable> getVariables() {
        variables.clear();
        variables.add(new Variable(getName() + "_constants", "const float " + getName() + "_constants[6] = {" + p.get() + ", " + i.get() + ", " + d.get() + ", " + min.get() + ", " + max.get() + ", " + period.get() + "};"));
        return variables;
    }

    @Override
    public Map<String, String> getCode() {
        code.clear();
        code.put("init", "PID " + getName() + " = " + "PID(" + getName() + ");");
        return code;
    }

    @Override
    public List<String> getLibraries() {
        return libraries;
    }

    @Override
    public String getType() {
        return "pid";
    }

    @Override
    public List<Double> getSetOptions() {
        List<Double> options = new ArrayList<>();
        options.add(Double.parseDouble(p.get()));
        options.add(Double.parseDouble(i.get()));
        options.add(Double.parseDouble(d.get()));
        options.add(Double.parseDouble(min.get()));
        options.add(Double.parseDouble(max.get()));
        options.add(Double.parseDouble(period.get()));
        return options;
    }
}
