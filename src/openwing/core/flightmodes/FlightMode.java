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
package openwing.core.flightmodes;

import openwing.core.Buildable;
import openwing.core.Function;
import openwing.core.Macro;
import openwing.core.Parameter;
import openwing.core.Variable;
import openwing.core.flightmodes.regulator.Regulator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import openwing.core.SaveData;
import openwing.core.SaveLoadAble;
import java.util.ResourceBundle;

public class FlightMode extends Buildable implements SaveLoadAble {
    
    List<String> requirements = new ArrayList<>();
    List<Regulator> regulators = new ArrayList<>();
    
    ResourceBundle rb;
    
    public FlightMode(ResourceBundle rb) {
        this.rb = rb;
    }
    
    public boolean addRequiredComponent(String name) {
        if (!requirements.contains(name)) {
            requirements.add(name);
            return true;
        }
        return false;
    }
    
    public boolean addRegulator(Regulator regulator) {
        boolean contains = false;
        for(Regulator reg : regulators) {
            contains |= reg.getName().equals(regulator.getName());
        }
        if(!contains) {
            regulators.add(regulator);
            return true;
        }
        return false;
    }

    public List<Regulator> getRegulators() {
        return regulators;
    }
    
    @Override
    public void complete() {
        getFinalVariables().clear();
        getFinalFunctions().clear();
        getFinalMacros().clear();
        getFinalCode().clear();
        
        for(Parameter parameter : getParameters()) {
            getFinalMacros().add(new Macro(parameter.getName(), "#define " + parameter.getName() + " " + parameter.getValue()));
        }
        
        getFinalVariables().addAll(getVariables());
        getFinalFunctions().addAll(getFunctions());
        getFinalMacros().addAll(getMacros());
        getFinalCode().putAll(getCode());
        
        for(Regulator regulator : getRegulators()) {
            getFinalVariables().addAll(regulator.getVariables());
            for(Map.Entry<String, String> part : regulator.getCode().entrySet()) {
                getFinalCode().put(part.getKey(), getFinalCode().containsKey(part.getKey()) ? 
                        (getFinalCode().get(part.getKey()) + "\n" + part.getValue()) : part.getValue());
            }
        //todo tady nekde
        }
        
        for(Parameter parameter : getParameters()) {
            for(Variable variable : getFinalVariables()) {
                variable.setDeclaration(variable.getDeclaration().replaceAll("\\b" + parameter.getName() + "\\b", getId() + parameter.getName()));
            }
            
            for(Function function : getFinalFunctions()) {
                function.setDeclaration(function.getDeclaration().replaceAll("\\b" + parameter.getName() + "\\b", getId() + parameter.getName()));
            }
            
            for(Macro macro : getFinalMacros()) {
                macro.setDefinition(macro.getDefinition().replaceAll("\\b" + parameter.getName() + "\\b", getId() + parameter.getName()));
            }
            for (Map.Entry<String, String> part : getFinalCode().entrySet()) {
                part.setValue(part.getValue().replaceAll("\\b" + parameter.getName() + "\\b", getId() + parameter.getName()));
            }
        }
        
        for(Regulator regulator : getRegulators()) {
            List<Variable> vars = regulator.getVariables();
            for(Variable var : vars) {
                for(Variable variable : getFinalVariables()) {
                    variable.setDeclaration(variable.getDeclaration().replaceAll("\\b" + var.getName() + "\\b", getId() + var.getName()));
                }

                for(Function function : getFinalFunctions()) {
                    function.setDeclaration(function.getDeclaration().replaceAll("\\b" + var.getName() + "\\b", getId() + var.getName()));
                    function.setDefinition(function.getDefinition().replaceAll("\\b" + var.getName() + "\\b", getId() + var.getName()));
                }

                for(Macro macro : getFinalMacros()) {
                    macro.setDefinition(macro.getDefinition().replaceAll("\\b" + var.getName() + "\\b", getId() + var.getName()));
                }
            
                for (Map.Entry<String, String> part : getFinalCode().entrySet()) {
                    part.setValue(part.getValue().replaceAll("\\b" + var.getName() + "\\b", getId() + var.getName()));
                }
            }
            for(Variable variable : getFinalVariables()) {
                variable.setDeclaration(variable.getDeclaration().replaceAll("\\b" + regulator.getName() + "\\b", getId() + regulator.getName()));
            }
            
            for(Function function : getFinalFunctions()) {
                function.setDeclaration(function.getDeclaration().replaceAll("\\b" + regulator.getName() + "\\b", getId() + regulator.getName()));
            }
            
            for(Macro macro : getFinalMacros()) {
                macro.setDefinition(macro.getDefinition().replaceAll("\\b" + regulator.getName() + "\\b", getId() + regulator.getName()));
            }
            for (Map.Entry<String, String> part : getFinalCode().entrySet()) {
                part.setValue(part.getValue().replaceAll("\\b" + regulator.getName() + "\\b", getId() + regulator.getName()));
            }
        }
    }
    
    public List<String> getRequirements() {
        return requirements;
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void save(SaveData data) {
        for(Parameter parameter : getParameters()) {
            data.add(getId() + "_" + parameter.getName(), parameter.getValue() + "");
        }

        for(Regulator regulator : getRegulators()) {
            List<Double> setOptions = regulator.getSetOptions();
            for(int i = 0; i < setOptions.size(); i++) {
                data.add(getId() + "_" + regulator.getName() + "_" + i, setOptions.get(i) + "");
            }
        }
    }

    @Override
    public void load(SaveData data) {
        for(Parameter parameter : getParameters()) {
            String d = data.get(getId() + "_" + parameter.getName());
            if(d != null) {
                parameter.setValue(d);
            }
        }

        for(Regulator regulator : getRegulators()) {
            for(int i = 0; i < regulator.getSettings().size(); i++) {
                String d = data.get(getId() + "_" + regulator.getName() + "_" + i);
                if(d != null) {
                    regulator.set(Double.parseDouble(d), i);
                }
            }
        }
    }
}
