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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Buildable {
    
    String name;
    String id;
    String description;
    
    List<String> libraries = new ArrayList<>();
    List<Parameter> parameters = new ArrayList<>();
    
    Map<String, String> code = new HashMap<>();
    List<Function> functions = new ArrayList<>();
    List<Variable> variables = new ArrayList<>();
    List<Macro> macros = new ArrayList<>();
    
    List<Function> finalFunctions = new ArrayList<>();
    List<Variable> finalVariables = new ArrayList<>();
    List<Macro> finalMacros = new ArrayList<>();
    Map<String, String> finalCode = new HashMap<>();
    
    URL source = null;
    
    public boolean addParameter(Parameter parameter) {
        boolean contains = false;
        for(Parameter param : parameters) {
            contains |= param.getName().equals(parameter.getName());
        }
        if(!contains) {
            parameters.add(parameter);
            return true;
        }
        return false;
    }
    
    public boolean addLibrary(String name) {
        if (!libraries.contains(name)) {
            libraries.add(name);
            return true;
        }
        return false;
    }

    public void addMacro(Macro macro) {
        macros.add(macro);
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public void addVariable(Variable variable) {
        variables.add(variable);
    }

    public void addCode(String key, String value) {
        code.put(key, value);
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<String> getLibraries() {
        return libraries;
    }

    public List<Macro> getMacros() {
        return macros;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public List<Macro> getFinalMacros() {
        return finalMacros;
    }

    public List<Function> getFinalFunctions() {
        return finalFunctions;
    }

    public List<Variable> getFinalVariables() {
        return finalVariables;
    }

    public Map<String, String> getFinalCode() {
        return finalCode;
    }

    public Map<String, String> getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    ParameterGroup params;
    boolean generatedParams = false;
        
    public ParameterGroup getParameterGroup() {
        if(!generatedParams) {
            params = new ParameterGroup(name);
            for(Parameter parameter : parameters) {
                params.add(parameter);
            }
        }
        return params;
    }

    public URL getSource() {
        return source;
    }

    public void setSource(URL source) {
        this.source = source;
    }
    
    public void complete() {
        finalFunctions.clear();
        finalFunctions.addAll(functions);
        finalMacros.clear();
        finalMacros.addAll(macros);
        finalVariables.clear();
        finalVariables.addAll(variables);
    }
}
