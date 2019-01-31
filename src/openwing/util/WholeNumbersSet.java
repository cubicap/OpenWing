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

public class WholeNumbersSet {
    Set<WholeValue> set = new TreeSet<>();

    public void add(WholeValue val) {
        set.add(val);
    }

    public boolean remove(WholeValue val) {
        return set.remove(val);
    }

    public void clear() {
        set.clear();
    }

    public boolean contains(int value) {
        boolean contains = false;
        for (WholeValue val : set) {
            contains |= val.contains(value);
        }
        return contains;
    }

    public String getLabel() {
        String label = "";
        for(WholeValue v : set) {
            label += v.toString() + '\n';
        }
        return label;
    }
    
    @Override
    public String toString() {
        String text = "";
        boolean first = true;
        for (WholeValue val : set) {
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
    
    public int getMin() {
        int num = Integer.MAX_VALUE;
        for(WholeValue v : set) {
            num = num < v.getMin() ? num : v.getMin();
        }
        return num;
    }
    
    public Set<Integer> getValues() {
        Set<Integer> nums = new TreeSet<>();
        for(WholeValue val : set) {
            nums.addAll(val.getValues());
        }
        return nums;
    }
}