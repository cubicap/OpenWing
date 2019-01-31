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

public class WholeNumber extends WholeValue {

    int num;
    String label;

    public WholeNumber(int number, String label) {
        num = number;
        this.label = label;
    }

    public WholeNumber(int number) {
        num = number;
        this.label = "";
    }

    @Override
    public boolean contains(int val) {
        return num == val;
    }

    @Override
    public String toString() {
        if(label.length() > 0) {
            return String.valueOf(num + ": " + label);
        }
        else {
            return String.valueOf(num);
        }
    }
    
    @Override
    public String getValuesString() {
        return String.valueOf(num);
    }

    @Override
    public int getMin() {
        return num;
    }

    @Override
    public int getMax() {
        return num;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Set<Integer> getValues() {
        Set<Integer> nums = new TreeSet<>();
        nums.add(num);
        return nums;
    }
}

