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

import openwing.util.WholeNumbersSet;

public class Serial {
    String name;
    
    int rx;
    int tx;
    
    int type;
    
    WholeNumbersSet rxRestrictions;
    WholeNumbersSet txRestrictions;
    
    String optionsString;
    
    //type 0-usb 1-hw 2-sw
    public Serial(String name, WholeNumbersSet rxRestrictions, WholeNumbersSet txRestrictions, int rx, int tx, int type, String optionsString) {
        this.name = name;
        this.rxRestrictions = rxRestrictions;
        this.txRestrictions = txRestrictions;
        this.rx = rx;
        this.tx = tx;
        this.type = type;
        this.optionsString = optionsString;
    }
    public String getName() {
        return name;
    }
    
    public int getType() {
        return type;
    }
    
    public WholeNumbersSet getRXRestrictions() {
        return rxRestrictions;
    }
    
    public WholeNumbersSet getTXRestrictions() {
        return txRestrictions;
    }
}
