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
package openwing.platformio;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlatformIO {

    static String PYTHON_PATH = "";
    static String PIO_PATH = "";
    
    public static Process runCommand(File workingDirectory, String ... args) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(PIO_PATH + "platformio");
        command.addAll(Arrays.asList(args));
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workingDirectory);
        Process p = pb.start();
        return p;
    }
    
    public static boolean checkPython() throws IOException {
        String command = PYTHON_PATH + "python";
        String args = "--version";
        ProcessBuilder pb = new ProcessBuilder(command, args);
        Process p = pb.start();
        long start = System.currentTimeMillis();
        BufferedReader brErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        BufferedReader brOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String output = "";
        String line;
        while(p.isAlive() && System.currentTimeMillis() < (start + 1000) && !output.contains("Python 2.7")) {
            while ((line = brOut.readLine()) != null) {
                output += line;
            }
            while ((line = brErr.readLine()) != null) {
                output += line;
            }
        }
        while ((line = brOut.readLine()) != null) {
            output += line;
        }
        while ((line = brErr.readLine()) != null) {
            output += line;
        }
        p.destroy();
        
        return output.contains("Python 2.7");
    }
    
    public static boolean checkPIO() throws IOException {
        return checkPIO(PIO_PATH);
    }
    
    public static boolean checkPIO(String path) {
        String command = path + "platformio";
        String args = "--version";
        ProcessBuilder pb = new ProcessBuilder(command, args);
        
        try {
            Process p = pb.start();
            long start = System.currentTimeMillis();
            BufferedReader brErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            BufferedReader brOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String output = "";
            String line;
            while(p.isAlive() && System.currentTimeMillis() < (start + 1000) && !output.contains("PlatformIO")) {
                while ((line = brOut.readLine()) != null) {
                    output += line;
                }
                while ((line = brErr.readLine()) != null) {
                    output += line;
                }
            }

            while ((line = brOut.readLine()) != null) {
                output += line;
            }
            while ((line = brErr.readLine()) != null) {
                output += line;
            }
            p.destroy();

            return output.contains("PlatformIO");
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static void setPythonPath(String path) {
        PlatformIO.PYTHON_PATH = path;
    }

    public static void setPlatformioPath(String path) {
        PlatformIO.PIO_PATH = path;
    }

    public static String getPlatformioPath() {
        return PIO_PATH;
    }
}
