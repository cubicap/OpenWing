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

public abstract class DecimalValue implements Comparable<DecimalValue> {

    public abstract boolean contains(double val);
    public abstract double getMin();
    public abstract double getMax();
    public abstract String getLabel();
    @Override
    public abstract String toString();
    public abstract String getValuesString();

    @Override
    public int compareTo(DecimalValue o) {
        return Double.compare(getMin(), o.getMin());
    }
}
