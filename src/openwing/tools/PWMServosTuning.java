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
package openwing.tools;

import openwing.core.Macro;
import openwing.core.Parameter;
import openwing.core.Variable;
import openwing.core.components.Component;
import openwing.core.components.connection.Connection;
import openwing.core.components.connection.Pin;
import java.util.ArrayList;
import java.util.List;
import openwing.Main;

public class PWMServosTuning {
    
    String program;
    
    public void generate() {
        
        List<Component> components = Main.dataBundle.getComponents("servos");
        Component servos = null;
        for(Component component : components) {
            if(component.getId().equals("pwmservos")) {
                servos = component;
                break;
            }
        }
        
        String code =   "#include <Arduino.h>\n" +
                        "#include <Servo.h>\n" +
                        "#include <stdint.h>\n\n";
        
        ////---------------------------------MACROS----------------------------------
        //list macros
        List<Macro> macros = new ArrayList<>();
        List<Connection> connections = servos.getConnections();
        for (Connection connection : connections) {
            String name = connection.getName();
            int value = (int)Math.round(((Pin)connection).getPinNumber());
            macros.add(new Macro(name, "#define " + name + " " + value));
        }
        
        //add macros
        for(Macro macro : macros) {
            code += macro.getDefinition().replace("\n", "\\\n") + "\n";
        }
        
        code += "\n";
        
        ////--------------------------------VARIABLES---------------------------------
        //list variables
        List<Variable> variables = new ArrayList<>();
        
        List<Parameter> parameters = servos.getParameters();
        for (Parameter parameter : parameters) {
            String name = parameter.getName();
            int value = (int)Math.round(parameter.getValue());
            variables.add(new Variable(name, "int " + name + " = " + value + ";"));
        }
        
        //add variables
        for(Variable variable : variables) {
            code += variable.getDeclaration() + "\n";
        }
        
        code += "Servo servoLeft;\n" +
                "Servo servoRight;\n" +
                "\n" +
                "String readUntil(char, uint64_t);\n" +
                "void setServosPosition(int, int);\n" +
                "void elevonMixing();\n" +
                "void loop();\n" +
                "void setup();\n" +
                "\n" +
                "void setup() {\n" +
                "	Serial.begin(112500);\n" +
                "\n" +
                "	servoLeft.attach(LEFT);\n" +
                "	servoRight.attach(RIGHT);\n" +
                "}\n" +
                "\n" +
                "void loop() {\n" +
                "    while(!Serial.available());\n" +
                "    String in = readUntil('\\n', 100000000);\n" +
                "    char command = in.charAt(0);\n" +
                "    switch(command) {\n" +
                "        case 'l':\n" +
                "            {\n" +
                "                int16_t rServoPos = in.substring(2).toInt();\n" +
                "                servoLeft.writeMicroseconds(rServoPos);\n" +
                "                Serial.print(\"Left servo moved to: \");\n" +
                "                Serial.println(rServoPos);\n" +
                "            } break;\n" +
                "        case 'r':\n" +
                "           {\n" +
                "                int16_t lServoPos = in.substring(2).toInt();\n" +
                "                servoRight.writeMicroseconds(lServoPos);\n" +
                "                Serial.print(\"Right servo moved to: \");\n" +
                "                Serial.println(lServoPos);\n" +
                "            } break;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "String readUntil(char until, uint64_t timeout) {\n" +
                "    uint64_t endTime = millis() + timeout;\n" +
                "    String s = \"\";\n" +
                "    char ch;\n" +
                "    while(!Serial.available() && endTime > millis());\n" +
                "    ch = Serial.read();\n" +
                "    Serial.print(ch);\n" +
                "    if(ch != until) {\n" +
                "        s += ch;\n" +
                "    }\n" +
                "    while(ch != until && endTime > millis()) {\n" +
                "        if(Serial.available() && (ch = Serial.read()) != until) {\n" +
                "            s += ch;\n" +
                "            Serial.print(ch);\n" +
                "            if(ch == '\\b') {\n" +
                "                s = s.substring(0, s.length() - 2);\n" +
                "                Serial.print(\" \\b\");\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    Serial.print(ch);\n" +
                "    return s;\n" +
                "}\n";
        
        program = code;
    }
    
    public String getCode() {
        return program;
    }
}
