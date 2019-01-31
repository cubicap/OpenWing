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
package openwing.guiutils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.function.UnaryOperator;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextFormatter;
import org.controlsfx.control.textfield.CustomTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import openwing.util.DecimalNumbersSet;

public class DecimalNumberField extends CustomTextField {
    
    FontAwesomeIconView excl;
    
    public DecimalNumberField(double val, double min, double max) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if(change.getControlNewText().matches("-?[0-9]*([\\.\\,][0-9]*)?(E[0-9]*)?")) {/*
                if(change.getControlNewText().replaceFirst("-", "").indexOf("0") == 0 && !change.getControlNewText().matches("-?0(.[0-9][0-9]*)?")) {
                    change.selectRange(change.getCaretPosition() - 1, change.getCaretPosition() - 1);
                    setText(change.getControlNewText().replaceFirst("0", ""));
                    change.setText("");
                    return change;
                }*/
                return change;
            }
            return null;
        };
        setTextFormatter(new TextFormatter<>(filter));
        
        excl = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
        
        setRight(excl);
        excl.setVisible(true);
        textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                double newNum = Double.parseDouble(newValue);
                if(newNum >= min && newNum <= max) {
                    if(excl.isVisible()) {
                        excl.setVisible(false);
                    }
                }
                else {
                    if(!excl.isVisible()) {
                        excl.setVisible(true);
                    }
                }
            } catch (Exception e) {
                if(!excl.isVisible()) {
                    excl.setVisible(true);
                }
            }
        });
        
        if(val >= min && val <= max) {
            if(excl.isVisible()) {
                excl.setVisible(false);
            }
        }
        else {
            if(!excl.isVisible()) {
                excl.setVisible(true);
            }
        }
        setText(val + "");
    }
    
    public DecimalNumberField(double val, DecimalNumbersSet values) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if(change.getControlNewText().matches("-?[0-9]*([\\.\\,][0-9]*)?(E[0-9]*)?")) {/*
                if(change.getControlNewText().replaceFirst("-", "").indexOf("0") == 0 && !change.getControlNewText().matches("-?0(.[0-9][0-9]*)?")) {
                    change.selectRange(change.getCaretPosition() - 1, change.getCaretPosition() - 1);
                    setText(change.getControlNewText().replaceFirst("0", ""));
                    change.setText("");
                    return change;
                }*/
                return change;
            }
            return null;
        };
        setTextFormatter(new TextFormatter<>(filter));
        
        excl = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
        
        setRight(excl);
        excl.setVisible(true);
        textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                double newNum = Double.parseDouble(newValue);
                if(values.contains(newNum)) {
                    if(excl.isVisible()) {
                        excl.setVisible(false);
                    }
                }
                else {
                    if(!excl.isVisible()) {
                        excl.setVisible(true);
                    }
                }
            } catch (Exception e) {
                if(!excl.isVisible()) {
                    excl.setVisible(true);
                }
            }
        });
        
        if(values.contains(val)) {
            if(excl.isVisible()) {
                excl.setVisible(false);
            }
        }
        else {
            if(!excl.isVisible()) {
                excl.setVisible(true);
            }
        }
        setText(val + "");
    }
}
