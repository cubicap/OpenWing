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

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class ParameterGroup {
    String name;
    
    List<Parameter> parameters = new ArrayList<>();
    List<Pane> panes = new ArrayList<>();
    
    public ParameterGroup(String name) {
        this.name = name;
    }
    
    public void add(Parameter parameter) {
        parameters.add(parameter);
        for(Pane pane : panes) {
            parameter.add((GridPane)pane, parameters.size());
        }
    }
    
    public void removeParameter(int index) {
        parameters.remove(index);
    }
    
    public int getSize() {
        return parameters.size();
    }
    
    public Pane getPane() {
        if(parameters.size() > 0) {
            GridPane grid = new GridPane();
            grid.setGridLinesVisible(true);
            ColumnConstraints cc1 = new ColumnConstraints();
            cc1.setMinWidth(50);
            cc1.setPrefWidth(50);
            cc1.setMaxWidth(200);
            cc1.setHgrow(Priority.ALWAYS);
            ColumnConstraints cc2 = new ColumnConstraints();
            cc2.setMinWidth(50);
            cc2.setPrefWidth(100);
            cc2.setMaxWidth(100);
            cc2.setHgrow(Priority.ALWAYS);
            ColumnConstraints cc3 = new ColumnConstraints();
            cc3.setMinWidth(50);
            cc3.setPrefWidth(50);
            cc3.setMaxWidth(70);
            cc3.setHgrow(Priority.ALWAYS);
            ColumnConstraints cc4 = new ColumnConstraints();
            cc4.setMinWidth(100);
            cc4.setPrefWidth(100);
            cc4.setMaxWidth(300);
            cc4.setHgrow(Priority.ALWAYS);
            ColumnConstraints cc5 = new ColumnConstraints();
            cc5.setMinWidth(100);
            cc5.setPrefWidth(100);
            cc5.setMaxWidth(Double.MAX_VALUE);
            cc5.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(cc1, cc2, cc3, cc4, cc5);
            Label title = new Label(name);
            GridPane.setMargin(title, new Insets(2, 5, 2, 5));
            title.setFont(Font.font("default", FontWeight.BOLD, FontPosture.REGULAR, 16));
            grid.add(title, 0, 0, 5, 1);
            for(int i = 0; i < parameters.size(); i++) {
                parameters.get(i).add((GridPane)grid, i + 1);
            }
            panes.add(grid);
            return grid;
        }
        else {
            return null;
        }
    }
}
