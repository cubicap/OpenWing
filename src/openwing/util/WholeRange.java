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

public class WholeRange extends WholeValue {

    int min;
    int max;
    String label;

    public WholeRange(int min, int max, String label) {
        this.min = min;
        this.max = max;
        this.label = label;
    }

    public WholeRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean contains(int val) {
        return val >= min && val <= max || val <= min && val >= max;
    }

    @Override
    public String toString() {
        if(label.length() > 0) {
            return String.valueOf("<" + min + "; " + max + ">" + ": " + label);
        }
        else {
            return String.valueOf("<" + min + "; " + max + ">");
        }
    }
    
    @Override
    public String getValuesString() {
        return String.valueOf("<" + min + "; " + max + ">");
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Set<Integer> getValues() {
        Set<Integer> nums = new TreeSet<>();
        for(int i = min; i <= max + 1; i++) {
            nums.add(i);
        }
        return nums;
    }
}