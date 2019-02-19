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
package openwing.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SaveData {
    
    Map<String, String> data = new TreeMap<>();
    
    public boolean add(String key, String value) {
        System.out.println("add - " + key + ": " + value);
        if(!data.containsKey(key)) {
            data.put(key, value);
            return true;
        }
        else {
            return false;
        }
    }
    
    public String get(String key) {
        System.out.println("load - " + key + ": " + data.get(key));
        return data.get(key);
    }
    
    public Set<String> getKeys() {
        return data.keySet();
    }
    
    public boolean saveFile(File target) throws IOException {
        String text = "";
        for (Map.Entry<String, String> entry : data.entrySet()) {
            text += entry.getKey() + "=" + entry.getValue() + "\r\n";
        }
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(target));
        bw.write(text);
        bw.close();
        return true;
    }
    
    public boolean loadFile(File source) throws IOException {
        data = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(source));
        String line;
        while((line = br.readLine()) != null) {
            data.put(line.split("=")[0], line.split("=")[1]);
            System.out.println(line.split("=")[0] + "; " + line.split("=")[1]);
        }
        br.close();
        return true;
    }
}
