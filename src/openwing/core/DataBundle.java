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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataBundle {

    List<FlightMode> flightModesBacklList = new ArrayList<>();
    List<Component> componentsBackList = new ArrayList<>();

    ObservableList<FlightMode> flightModes;
    ObservableList<Component> components;

    Map<String, ObservableList<Component>> componentGroups = new HashMap<>();
    List<String> libraries = new ArrayList<>();

    ResourceBundle rb;

    public DataBundle(ResourceBundle rb) {
        this.rb = rb;
        flightModes = FXCollections.observableArrayList(flightModesBacklList);
        components = FXCollections.observableArrayList(componentsBackList);
        libraries.add("Servo.h");
        libraries.add("GY953.h");
        libraries.add("MS5611.h");
        libraries.add("BatteryMonitor.h");
        libraries.add("LittleMaths.h");
        libraries.add("PIDlib.h");
        libraries.add("TinyGPS++.h");
        libraries.add("Arduino.h");
    }

    public void addFlightMode(FlightMode flightMode) {
        flightModes.add(flightMode);
    }

    public void addFlightModes(List<FlightMode> flightModesList) {
        flightModes.addAll(flightModesList);
    }

    public void addComponent(Component component) {

        components.add(component);

        String type = component.getType();
        if (!componentGroups.containsKey(type)) {
            List<Component> compGroupBackList = new ArrayList<>();
            ObservableList<Component> compGroup = FXCollections.observableArrayList(compGroupBackList);
            Component nullComponent = new Component(rb);
            nullComponent.setType(type);
            nullComponent.setName(rb.getString("none"));
            nullComponent.setId("null_" + type);
            nullComponent.setDescription(rb.getString("nullcomponent_desc"));
            compGroup.add(nullComponent);
            componentGroups.put(type, compGroup);
        }

        componentGroups.get(type).add(component);
    }

    public void addComponents(List<Component> componentsList) {
        components.addAll(componentsList);

        for (Component component : componentsList) {
            String type = component.getType();
            if (!componentGroups.containsKey(type)) {
                List<Component> compGroupBackList = new ArrayList<>();
                ObservableList<Component> compGroup = FXCollections.observableArrayList(compGroupBackList);
                Component nullComponent = new Component(rb);
                nullComponent.setType(type);
                nullComponent.setName(rb.getString("none"));
                nullComponent.setId("null_" + type);
                nullComponent.setDescription(rb.getString("nullcomponent_desc"));
                compGroup.add(nullComponent);
                componentGroups.put(type, compGroup);
            }

            componentGroups.get(type).add(component);
        }
    }

    public List<FlightMode> getFlightModes() {
        return flightModes;
    }

    public List<Component> getComponents() {
        return components;
    }

    public List<Component> getComponents(String type) {
        if (!componentGroups.containsKey(type)) {
            List<Component> compGroupBackList = new ArrayList<>();
            ObservableList<Component> compGroup = FXCollections.observableArrayList(compGroupBackList);
            Component nullComponent = new Component(rb);
            nullComponent.setType(type);
            nullComponent.setName(rb.getString("none"));
            nullComponent.setId("null_" + type);
            nullComponent.setDescription(rb.getString("nullcomponent_desc"));
            compGroup.add(nullComponent);
            componentGroups.put(type, compGroup);
            System.out.println("get");
        }
        return componentGroups.get(type);
    }
    
    public boolean hasLibrary(String library) {
        return libraries.contains(library);
    }
}
