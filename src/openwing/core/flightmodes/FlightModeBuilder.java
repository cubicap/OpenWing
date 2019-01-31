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

import openwing.controller.ParametersListPaneController;
import openwing.controller.RegulatorsPaneController;
import openwing.core.Function;
import openwing.core.Macro;
import openwing.util.DecimalNumber;
import openwing.core.Parameter;
import openwing.core.Variable;
import openwing.core.flightmodes.regulator.PID;
import openwing.util.DecimalRange;
import openwing.util.DecimalNumbersSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class FlightModeBuilder {
    
    static String flightModesPackageName = "/openwing/core/flightmodes/";
    static String flightModesListName = "flightModes.list";
    
    ResourceBundle rb;

    public FlightModeBuilder(ResourceBundle rb) {
        this.rb = rb;
    }
    
    /**
     * Parses given File as FlightMode instance
     * @param sourceFile the File to parse
     * @return FlightMode result from parsing the source File
     */
    public FlightMode parse(File sourceFile) {
        try {
            InputStream is = new FileInputStream(sourceFile);
            return parse(is);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public FlightMode parse(URL source) throws IOException {
        try {
            InputStream is = source.openStream();
            return parse(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Parses given InputStream as FlightMode instance
     * @param sourceInputStream the InputStream to parse
     * @return FlightMode result from parsing the source InputStream
     */
    public FlightMode parse(InputStream sourceInputStream) {
        try {
            FlightMode flightMode = new FlightMode(rb);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new StreamSource(getClass().getResourceAsStream(flightModesPackageName + "xml/FlightModeSchema.xsd")));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setIgnoringElementContentWhitespace(true);
            dbFactory.setSchema(schema);
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
            
            Element rootElement = doc.getDocumentElement();
            
            flightMode.setName(localizeString(rootElement.getAttribute("name")));
            flightMode.setId(rootElement.getAttribute("id"));
            
            NodeList children = rootElement.getChildNodes();
            
            for(int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if(child.getNodeType() == Node.COMMENT_NODE) {
                    continue;
                }
                switch(child.getNodeName()) {
                    case "description":
                        flightMode.setDescription(localizeString(child.getTextContent().trim()));
                        break;
                    case "requirements":
                        Element requirements = (Element) child;
                        NodeList requirementNodes = requirements.getChildNodes();
                        for(int ii = 0; ii < requirementNodes.getLength(); ii++) {
                            if(requirementNodes.item(ii).getNodeType() == Node.COMMENT_NODE) {
                                continue;
                            }
                            Node component = requirementNodes.item(ii);
                            if(component.getNodeName().equals("component")) {
                                flightMode.addRequiredComponent(component.getTextContent());
                            }
                        }
                        break;
                    case "parameters":
                        Element parameters = (Element) child;
                        NodeList parameterNodes = parameters.getChildNodes();
                        for(int ii = 0; ii < parameterNodes.getLength(); ii++) {
                            if(parameterNodes.item(ii).getNodeType() == Node.COMMENT_NODE) {
                                continue;
                            }
                            Element parameter = (Element)parameterNodes.item(ii);
                            if(parameter.getNodeName().equals("parameter")) {
                                String paramName = parameter.getAttribute("name");
                                double defaultValue = Double.parseDouble(parameter.getAttribute("defaultvalue"));
                                int type;
                                switch(parameter.getAttribute("type")) {
                                    case "integer":
                                        type = Parameter.WHOLE;
                                        break;
                                    case "decimal":
                                        type = Parameter.DECIMAL;
                                        break;
                                    default:
                                        type = Parameter.DECIMAL;
                                }
                                String units = localizeString(parameter.getAttribute("units"));
                                String description = localizeString(parameter.getElementsByTagName("description").item(0).getTextContent().trim());
                                DecimalNumbersSet values = new DecimalNumbersSet();
                                NodeList valueOptions = parameter.getElementsByTagName("options").item(0).getChildNodes();
                                for(int iii = 0; iii < valueOptions.getLength(); iii++) {
                                    if(valueOptions.item(iii).getNodeType() == Node.COMMENT_NODE) {
                                        continue;
                                    }
                                    Element val = (Element) valueOptions.item(iii);
                                    switch(val.getNodeName()) {
                                        case "number":
                                            values.add(new DecimalNumber(Double.parseDouble(val.getAttribute("value")), localizeString(val.getAttribute("label"))));
                                            break;
                                        case "range":
                                            values.add(new DecimalRange(Double.parseDouble(val.getAttribute("minvalue")), Double.parseDouble(val.getAttribute("maxvalue")), localizeString(val.getAttribute("label"))));
                                            break;
                                    }
                                }
                                Parameter param = new Parameter(paramName, defaultValue, units, values, description, type);
                                flightMode.addParameter(param);
                            }
                        }
                        break;
                    case "libraries":
                        Element libraries = (Element) child;
                        NodeList libraryNodes = libraries.getChildNodes();
                        for(int ii = 0; ii < libraryNodes.getLength(); ii++) {
                            if(libraryNodes.item(ii).getNodeType() == Node.COMMENT_NODE) {
                                continue;
                            }
                            Element library = (Element)libraryNodes.item(ii);
                            if(library.getNodeName().equals("library")) {
                                flightMode.addLibrary(library.getTextContent());
                            }
                        }
                        break;
                    case "regulator":
                        Element constantses = (Element) child;
                        NodeList constNodes = constantses.getChildNodes();
                        for(int ii = 0; ii < constNodes.getLength(); ii++) {
                            if(constNodes.item(ii).getNodeType() == Node.COMMENT_NODE) {
                                continue;
                            }
                            Element constants = (Element)constNodes.item(ii);
                            if(constants.getNodeName().equals("pid")) {
                                PID regulator = new PID(localizeString(constants.getAttribute("name")),
                                        localizeString(constants.getTextContent()),
                                        Double.parseDouble(constants.getAttribute("pval")), 
                                        Double.parseDouble(constants.getAttribute("ival")), 
                                        Double.parseDouble(constants.getAttribute("dval")), 
                                        Double.parseDouble(constants.getAttribute("minoutput")),
                                        Double.parseDouble(constants.getAttribute("maxoutput")),
                                        Double.parseDouble(constants.getAttribute("period")),
                                        flightMode.getName(),
                                        "fmode");
                                flightMode.addRegulator(regulator);
                                RegulatorsPaneController.addRegulator(regulator);
                            }
                        }
                        break;
                    case "macros":
                        Element macros = (Element) child;
                        NodeList macroNodes = macros.getChildNodes();
                        for(int ii = 0; ii < macroNodes.getLength(); ii++) {
                            if(macroNodes.item(ii).getNodeType() == Node.COMMENT_NODE) {
                                continue;
                            }
                            Element macro = (Element)macroNodes.item(ii);
                            if(macro.getNodeName().equals("macro")) {
                                flightMode.addMacro(new Macro(macro.getAttribute("name"), macro.getTextContent()));
                            }
                        }
                        break;
                    case "globalvariables":
                        Element globalvariables = (Element) child;
                        NodeList variableNodes = globalvariables.getChildNodes();
                        for(int ii = 0; ii < variableNodes.getLength(); ii++) {
                            if(variableNodes.item(ii).getNodeType() == Node.COMMENT_NODE) {
                                continue;
                            }
                            Element variable = (Element)variableNodes.item(ii);
                            if(variable.getNodeName().equals("variable")) {
                                flightMode.addVariable(new Variable(variable.getAttribute("name"), variable.getTextContent()));
                            }
                        }
                        break;
                    case "functions":
                        Element functions = (Element) child;
                        NodeList functionNodes = functions.getChildNodes();
                        for(int ii = 0; ii < functionNodes.getLength(); ii++) {
                            if(functionNodes.item(ii).getNodeType() == Node.COMMENT_NODE) {
                                continue;
                            }
                            Element function = (Element)functionNodes.item(ii);
                            if(function.getNodeName().equals("function")) {
                                flightMode.addFunction(new Function(function.getAttribute("name"), 
                                        ((Element)function.getElementsByTagName("declaration").item(0)).getTextContent(), 
                                        ((Element)function.getElementsByTagName("definition").item(0)).getTextContent()));
                            }
                        }
                        break;
                    case "code":
                        Element code = (Element) child;
                        NodeList codeNodes = code.getChildNodes();
                        for(int ii = 0; ii < codeNodes.getLength(); ii++) {
                            if(codeNodes.item(ii).getNodeType() == Node.COMMENT_NODE) {
                                continue;
                            }
                            Element part = (Element) codeNodes.item(ii);
                            flightMode.addCode(part.getAttribute("name"), part.getTextContent());
                        }
                        break;
                }
            }
            ParametersListPaneController.addParameterGroup(flightMode.getParameterGroup());
            
            return flightMode;
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR, "Error parsing file: " + ex.getLocalizedMessage(), ButtonType.OK);
            a.show();
            return null;
        }
    }
    
    public String localizeString(String input) {
        Set<String> keys = new HashSet<>();
        Matcher m = Pattern.compile("%.+?\\b").matcher(input);
        while(m.find()) {
            keys.add(m.group());
        }
        for (String key : keys) {
            if(rb.containsKey(key.substring(1))) {
                input = input.replaceAll(key + "\\b", rb.getString(key.substring(1)));
            }
        }
        return input;
    }
    
    
    /**
     * Parses all files present in given folder as FlightMode and saves them in internal ObservableMap
     * @return  List of parsed FlightMode instances
     */
    public List<FlightMode> parseAllClasspath() {
        List<String> resources = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(flightModesPackageName + "xml/" + flightModesListName)));
            String line;
            while((line = br.readLine()) != null) {
                if(line.contains(".xml")) {
                    resources.add(line);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        List<FlightMode> flightModes = new ArrayList<>();
        for (String file : resources) {
            System.out.println(file);
            FlightMode fm = parse(getClass().getResourceAsStream(flightModesPackageName + "xml/" + file));
            if(fm != null) {
                flightModes.add(fm);
            }
        }
        return flightModes;
    }
    
    /**
     * Parses all files present in given folder as FlightMode and saves them in internal ObservableMap
     * @param sourceFolder the folder to search the files in 
     * @return  Map of parsed FlightMode instances
     */
    public Map<String, FlightMode> parseAll(File sourceFolder) {
        Map<String, FlightMode> flightModes = new HashMap<>();
        File toParse[] = sourceFolder.listFiles();
        for (File file : toParse) {
            FlightMode fm = parse(file);
            if(fm != null) {
                flightModes.put(fm.getId(), fm);
            }
        }
        return flightModes;
    }
}
