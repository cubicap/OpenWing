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
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.function.UnaryOperator;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import org.controlsfx.control.textfield.CustomTextField;
import openwing.util.DecimalNumbersSet;
import openwing.util.WholeNumbersSet;

public class WholeNumberField extends CustomTextField {
    
    FontAwesomeIconView excl;
    
    public WholeNumberField(int val, int min, int max) {
        UnaryOperator<Change> filter = change -> {
            if(change.getControlNewText().matches("-?([0-9]*)?")) {/*
                if(change.getControlNewText().replaceFirst("-", "").indexOf("0") == 0 && !change.getControlNewText().matches("-?0")) {
                    setText(change.getControlNewText().replaceFirst("0", ""));
                    change.setText("");
                    if(change.getCaretPosition()==1) {
                        change.selectRange(0, 0);
                    }
                    else if(change.getControlNewText().contains("-") && change.getCaretPosition() == 2) {
                        change.selectRange(1, 1);
                    }
                    return change;
                }*/
                return change;
            }
            return null;
        };
        setTextFormatter(new TextFormatter<>(filter));
        
        excl = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
        
        setRight(excl);
        excl.setVisible(false);
        
        textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                int newNum = Integer.parseInt((!newValue.equals("")) ? newValue : "0");
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
    
    public WholeNumberField(int val, WholeNumbersSet values) {
        UnaryOperator<Change> filter = change -> {
            if(change.getControlNewText().matches("-?([0-9]*)?")) {/*
                if(change.getControlNewText().replaceFirst("-", "").indexOf("0") == 0 && !change.getControlNewText().matches("-?0")) {
                    setText(change.getControlNewText().replaceFirst("0", ""));
                    change.setText("");
                    if(change.getCaretPosition()==1) {
                        change.selectRange(0, 0);
                    }
                    else if(change.getControlNewText().contains("-") && change.getCaretPosition() == 2) {
                        change.selectRange(1, 1);
                    }
                    return change;
                }*/
                return change;
            }
            return null;
        };
        setTextFormatter(new TextFormatter<>(filter));
        
        excl = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
        
        setRight(excl);
        excl.setVisible(false);
        
        textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                int newNum = Integer.parseInt((!newValue.equals("")) ? newValue : "0");
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
    
    public WholeNumberField(int val, DecimalNumbersSet values) {
        UnaryOperator<Change> filter = change -> {
            if(change.getControlNewText().matches("-?([0-9]*)?")) {/*
                if(change.getControlNewText().replaceFirst("-", "").indexOf("0") == 0 && !change.getControlNewText().matches("-?0")) {
                    setText(change.getControlNewText().replaceFirst("0", ""));
                    change.setText("");
                    if(change.getCaretPosition()==1) {
                        change.selectRange(0, 0);
                    }
                    else if(change.getControlNewText().contains("-") && change.getCaretPosition() == 2) {
                        change.selectRange(1, 1);
                    }
                    return change;
                }*/
                return change;
            }
            return null;
        };
        setTextFormatter(new TextFormatter<>(filter));
        
        excl = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
        
        setRight(excl);
        excl.setVisible(false);
        
        textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                int newNum = Integer.parseInt((!newValue.equals("")) ? newValue : "0");
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
