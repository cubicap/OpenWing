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
package openwing.util;

import java.util.Set;
import java.util.TreeSet;

public class DecimalNumbersSet {
    Set<DecimalValue> set = new TreeSet<>();

    public void add(DecimalValue val) {
        set.add(val);
    }

    public boolean remove(DecimalValue val) {
        return set.remove(val);
    }

    public void clear() {
        set.clear();
    }

    public boolean contains(double value) {
        boolean contains = false;
        for (DecimalValue val : set) {
            contains |= val.contains(value);
        }
        return contains;
    }

    public String getLabel() {
        String label = "";
        for(DecimalValue v : set) {
            label += v.toString() + '\n';
        }
        return label;
    }
    
    @Override
    public String toString() {
        String text = "";
        boolean first = true;
        for (DecimalValue val : set) {
            if(!first) {
                text += "; " + val.getValuesString();
            }
            else {
                text += val.getValuesString();
                first = false;
            }
        }
        return text;
    }
    
    public double getMin() {
        double num = Double.POSITIVE_INFINITY;
        for(DecimalValue v : set) {
            num = num < v.getMin() ? num : v.getMin();
        }
        return num;
    }
    
    public double getMax() {
        double num = Double.NEGATIVE_INFINITY;
        for(DecimalValue v : set) {
            num = num > v.getMax() ? num : v.getMax();
        }
        return num;
    }
}