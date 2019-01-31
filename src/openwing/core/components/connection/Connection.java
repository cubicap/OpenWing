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

import openwing.core.Macro;
import java.util.ArrayList;
import java.util.List;

public abstract class Connection {
    
    String name;
    List<ConnectionSetting> settings = new ArrayList<>();
    
    public Connection(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public List<ConnectionSetting> getSettings() {
        return settings;
    }
    
    public abstract List<Integer> getSetOptions();
    
    public abstract void set(int value, int id);

    public abstract List<Macro> getMacros();
}
