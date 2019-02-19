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

import openwing.core.components.ComponentBuilder;
import openwing.core.flightmodes.FlightModeBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import openwing.Main;
import openwing.core.components.Component;
import openwing.core.flightmodes.FlightMode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLLoader {
    
    URL source; 
    ResourceBundle rb;
    
    public XMLLoader(URL source, ResourceBundle rb) {
        this.source = source;
        this.rb = rb;
    }
    
    public Buildable load() {
        Buildable b = null;
        try {
            InputStream sourceInputStream = source.openStream();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dBuilder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    exception.printStackTrace();
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    exception.printStackTrace();
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    exception.printStackTrace();
                }
            });
            
            Document doc = dBuilder.parse(sourceInputStream);
            
            sourceInputStream.close();

            Element rootElement = doc.getDocumentElement();
            String type = rootElement.getTagName();
            String name = (rootElement.getAttribute("name"));
            String id = (rootElement.getAttribute("id"));
            String version = (rootElement.getAttribute("version"));
            String schemaVersion = (rootElement.getAttribute("schemaversion"));
            
            if (schemaVersion.equals("1")) {
                switch(type) {
                    case "component":
                        ComponentBuilder cb = new ComponentBuilder(rb);
                        b = cb.parse(source);
                        b.setSource(source);
                        Main.dataBundle.addComponent((Component)b);
                        break;
                    case "flightmode":
                        FlightModeBuilder fmb = new FlightModeBuilder(rb);
                        b = fmb.parse(source);
                        b.setSource(source);
                        Main.dataBundle.addFlightMode((FlightMode)b);
                        break;
                }
            }
            else {
                Alert a = new Alert(Alert.AlertType.ERROR, rb.getString("schema_version_error"), ButtonType.OK);
                a.showAndWait();
            }
            
            System.out.println(type);
            System.out.println(name);
            System.out.println(id);
            System.out.println(version);
            System.out.println(schemaVersion);
            
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            return null;
        }
        return b;
    }
}
