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

import openwing.core.components.Component;
import openwing.core.flightmodes.FlightMode;
import java.util.ArrayList;
import java.util.List;

public class Program {
    
    List<FlightMode> flightModes;
    List<Component> components;
    
    String program;
    
    /*
     * TODO: failsafe
     */
    
    public Program(List<FlightMode> flightModes, List<Component> components) {
        this.components = components;
        this.flightModes = flightModes;
    }
    
    public void generate() {
        
        List<Buildable> buildables = new ArrayList<>();
        
        for(Component component : components) {
            if(component!=null) {
                buildables.add(component);
            }
        }
        
        for(FlightMode flightMode : flightModes) {
            if(flightMode!=null) {
                buildables.add(flightMode);
            }
        }
        
        for(Buildable buildable : buildables) {
            if(buildable!=null) {
                buildable.complete();
            }
        }
        
        String code = "";
        //---------------------------------LIBRARIES----------------------------------
        //list libraries
        List<String> libraries = new ArrayList<>();
        libraries.add("Arduino.h");
        for(Buildable buildable : buildables) {
            List<String> libs = buildable.getLibraries();
            for(String lib : libs) {
                if(!libraries.contains(lib)) {
                    libraries.add(lib);
                }
            }
        }
        
        //add libraries
        for(String library : libraries) {
            code += "#include <" + library + ">\n";
        }
        
        code += "\n";
        
        ////---------------------------------MACROS----------------------------------
        //list macros
        List<Macro> macros = new ArrayList<>();
        for(Buildable buildable : buildables) {
            List<Macro> ms = buildable.getFinalMacros();
            for(Macro macro : ms) {
                macros.add(macro);
            }
        }
        
        macros.add(new Macro("CYCLE_PERIOD", "#define CYCLE_PERIOD 50000"));
        
        int switchStep = 1000/flightModes.size();
        for(int i = 0; i < flightModes.size(); i++) {
            macros.add(new Macro("rx_range_m" + i + "_low", "#define rx_range_m" + i + "_low " + (1000 + switchStep * i)));
            macros.add(new Macro("rx_range_m" + i + "_high", "#define rx_range_m" + i + "_high " + (1000 + switchStep * (i + 1))));
        }
        
        //add macros
        for(Macro macro : macros) {
            code += macro.getDefinition().replace("\n", "\\\n") + "\n";
        }
        
        code += "\n";
        
        ////--------------------------------FUNCTIONS - declarations---------------------------------
        //list functions
        
        String init = "";
        String loopStart = "";
        String loopEnd = "";
        String setupFn = "";
        String loopFn = "";
        String delay = "while(time > micros()) {\n";
        
        List<Function> functions = new ArrayList<>();
        for(Buildable buildable : buildables) {
            List<Function> fs = buildable.getFinalFunctions();
            for(Function function : fs) {
                functions.add(function);
            }
            setupFn += buildable.getFinalCode().containsKey("setup") ? buildable.getFinalCode().get("setup") + "\n" : "";
        }
        
        
        loopFn = "rx_update();\nrx_getChannels();\n";
        int modeIndex = 0;
        loopFn += "if(inRange(rx_channels[rx_channel_mode_switch], rx_range_m" + modeIndex + "_low, rx_range_m" + modeIndex + "_high)) {\n";
        loopFn += "fmode" + modeIndex + "();\n}\n";
        for(modeIndex++; modeIndex < flightModes.size(); modeIndex++) {
            loopFn += "else if(inRange(rx_channels[rx_channel_mode_switch], rx_range_m" + modeIndex + "_low, rx_range_m" + modeIndex + "_high)) {\n";
            loopFn += "fmode" + modeIndex + "();\n}\n";
        }
        
        for(Component component : components) {
            if(component != null) {
                init += component.getFinalCode().containsKey("init") ? component.getFinalCode().get("init") + "\n" : "";
                loopStart += component.getFinalCode().containsKey("loopStart") ? component.getFinalCode().get("loopStart") + "\n" : "";
                loopEnd += component.getFinalCode().containsKey("loopEnd") ? component.getFinalCode().get("loopEnd") + "\n" : "";
                delay += component.getFinalCode().containsKey("delay") ? component.getFinalCode().get("delay") + "\n" : "";
            }
        }
        
        for(int i = 0; i < flightModes.size(); i++) {
            FlightMode flightMode = flightModes.get(i);
            if(flightMode != null) {
                String function = "//" + flightMode.getName();
                function += init + (flightMode.getFinalCode().containsKey("init") ? (flightMode.getFinalCode().get("init") + "\n") : "") + "\n";
                function += "while(inRange(rx_channels[rx_channel_mode_switch], rx_range_m" + i + "_low, rx_range_m" + i + "_high) && !reloadMode && !fail) {\n"
                        + "cycleStart = micros();\n";
                function += loopStart + (flightMode.getFinalCode().containsKey("loopStart") ? flightMode.getFinalCode().get("loopStart") + "\n" : "") + "\n";
                function += flightMode.getFinalCode().containsKey("loop") ? flightMode.getFinalCode().get("loop") + "\n" : "";
                function += loopEnd + (flightMode.getFinalCode().containsKey("loopEnd") ? flightMode.getFinalCode().get("loopEnd") + "\n" : "") + "\n";
                function += "delayMicrosecondsUntil(cycleStart + CYCLE_PERIOD);\n}\n";
                functions.add(new Function("fmode" + i, "void fmode" + i + "();", "void fmode" + i + "() {\n" + function + "}\n"));
            }
        }
        
        functions.add(new Function("setup", "void setup();", "void setup() {\n" + setupFn + "}"));
        functions.add(new Function("loop", "void loop();", "void loop() {\n" + loopFn + "}"));
        functions.add(new Function("delayMicrosecondsUntil", "void delayMicrosecondsUntil(uint64_t time);", "void delayMicrosecondsUntil(uint64_t time) {\n" + delay + "}\n}\n"));
        
        //add functions
        for(Function function : functions) {
            code += function.getDeclaration() + "\n";
        }
        
        code += "\n";
        
        ////--------------------------------VARIABLES---------------------------------
        //list variables
        List<Variable> variables = new ArrayList<>();
        for(Buildable buildable : buildables) {
            List<Variable> vars = buildable.getFinalVariables();
            for(Variable variable : vars) {
                variables.add(variable);
            }
        }
        variables.add(new Variable("cycleStart", "int32_t cycleStart = 0;"));
        variables.add(new Variable("cyclePeriod", "int32_t cyclePeriod = CYCLE_PERIOD;"));
        variables.add(new Variable("fail", "bool fail = false;"));
        variables.add(new Variable("reloadMode", "bool reloadMode = false;"));
        
        //add variables
        for(Variable variable : variables) {
            code += variable.getDeclaration() + "\n";
        }
        
        code += "\n";
        
        ////--------------------------------FUNCTIONS - definitions---------------------------------
        //add functions
        for(Function function : functions) {
            code += function.getDefinition() + "\n";
        }
        
        code += "\n";
        
        program = code;
    }
    
    public String getCode() {
        return program;
    }
}
