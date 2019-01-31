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
package openwing.controller;

import openwing.core.Program;
import openwing.core.XMLLoader;
import openwing.core.components.Component;
import openwing.core.flightmodes.FlightMode;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jssc.SerialPortList;
import openwing.Main;
import openwing.core.SaveData;
import openwing.core.SaveLoadAble;
import openwing.tools.PWMServosTuning;
import java.io.FileInputStream;
import java.nio.file.Files;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortException;
import openwing.core.Buildable;
import openwing.platformio.PlatformIO;
import openwing.util.WebBrowserOpener;

/**
 * FXML Controller class
 */
public class MainWindowController implements Initializable, SaveLoadAble {
    
    @FXML
    private BorderPane toolBarPane;
    @FXML
    private StackPane contentPane;
    @FXML
    private HBox controlButtonPane;
    @FXML
    private HBox connectPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private ComboBox<String> portChooser;
    @FXML
    private ComboBox<Integer> baudRateChooser;
    @FXML
    private Button serialButton;
    
    private Map<String, Screen> screens = new HashMap<>();
    private String lastScreenKey = "parts";
    
    private ResourceBundle bundle;
    
    public static File lastDirectory;
    
    static String tempDirectoryPath = System.getProperty("java.io.tmpdir") + "FlightController/";
    
    ObservableList<String> portNames;
    List<String> portNamesBackList = new ArrayList<>();
    Timer portUpdater = new Timer();
    
    File openFile;
    
    SerialPort serialPort;
    boolean connected = false;
    
    public static File libraryFolder = new File(Main.homeFolder, "Libraries");
    
    static FileChooser.ExtensionFilter xmlFilesExtensionFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
    static FileChooser.ExtensionFilter saveFileExtensionFilter = new FileChooser.ExtensionFilter("Settings files (*.fcs)", "*.fcs");
    static FileChooser.ExtensionFilter anyFilesExtensionFilter = new FileChooser.ExtensionFilter("Any files", "*");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
        if(!Main.hasPlatformIO) {
            Alert a = new Alert(AlertType.ERROR, gis("pio_not_present"), ButtonType.OK);
            Platform.runLater(() -> {
                a.showAndWait();
            });
        }
        Menu fileMenu = new Menu(gis("file"));
            MenuItem loadXml = new MenuItem(gis("load_xml"));
            loadXml.setOnAction((ActionEvent event) -> {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(xmlFilesExtensionFilter);
                fc.getExtensionFilters().add(anyFilesExtensionFilter);
                fc.setInitialDirectory(getInitialDirectory());
                File chosen = fc.showOpenDialog(menuBar.getScene().getWindow());
                if(chosen != null && chosen.exists()) {
                    try {
                        URL target = chosen.toURI().toURL();
                        XMLLoader loader = new XMLLoader(target, rb);
                        Buildable b = loader.load();
                        List<String> externalLibs = new ArrayList();
                        for(String lib : b.getLibraries()) {
                            if(!Main.dataBundle.hasLibrary(lib)) {
                                externalLibs.add(lib);
                            }
                        }
                        System.out.println(Main.homeFolder.getAbsolutePath());
                        String type = b.getClass().getSimpleName().toLowerCase();
                        if(externalLibs.isEmpty()) {
                            Alert a = new Alert(AlertType.INFORMATION, gis("import_success").replaceAll("%type", gis(type)), ButtonType.OK);
                            a.showAndWait();
                        }
                        else if(externalLibs.size() == 1) {
                            Alert a = new Alert(AlertType.INFORMATION, gis("external_library_prompt").replaceAll("%type", gis(type)).replaceAll("%libs", externalLibs.get(0)).replaceAll("%home_path", libraryFolder.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\")), ButtonType.OK);
                            a.showAndWait();
                        }
                        else if(externalLibs.size() > 1) {
                            String libsString = "";
                            for(String lib : externalLibs) {
                                libsString += lib + ",\n";
                            }
                            Alert a = new Alert(AlertType.INFORMATION, gis("external_libraries_prompt").replaceAll("%type", gis(type)).replaceAll("%libs", libsString).replaceAll("%home_path", libraryFolder.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\")), ButtonType.OK);
                            a.showAndWait();
                        }
                        setLastDirectory(chosen);
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            MenuItem save = new MenuItem(gis("save"));
            save.setOnAction((ActionEvent event) -> {
                if(openFile != null && openFile.exists()) {
                    try {
                        SaveData data = new SaveData();
                        save(data);
                        data.saveFile(openFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    FileChooser fc = new FileChooser();
                    fc.setInitialDirectory(getInitialDirectory());
                    fc.getExtensionFilters().add(saveFileExtensionFilter);
                    fc.getExtensionFilters().add(anyFilesExtensionFilter);
                    File target = fc.showSaveDialog(menuBar.getScene().getWindow());
                    if(target != null) {
                        try {
                            SaveData data = new SaveData();
                            save(data);
                            data.saveFile(target);
                            ((Stage)menuBar.getScene().getWindow()).setTitle(gis("title") + " - " + target.getName());
                            openFile = target;
                            setLastDirectory(target);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
            MenuItem saveAs = new MenuItem(gis("save_as"));
            saveAs.setOnAction((ActionEvent event) -> {
                FileChooser fc = new FileChooser();
                fc.setInitialDirectory(getInitialDirectory());
                fc.getExtensionFilters().add(saveFileExtensionFilter);
                fc.getExtensionFilters().add(anyFilesExtensionFilter);
                File target = fc.showSaveDialog(menuBar.getScene().getWindow());
                if(target != null) {
                    try {
                        SaveData data = new SaveData();
                        save(data);
                        data.saveFile(target);
                        ((Stage)menuBar.getScene().getWindow()).setTitle(gis("title") + " - " + target.getName());
                        setLastDirectory(target);
                            openFile = target;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            MenuItem open = new MenuItem(gis("open"));
            open.setOnAction((ActionEvent event) -> {
                FileChooser fc = new FileChooser();
                fc.setInitialDirectory(getInitialDirectory());
                fc.getExtensionFilters().add(saveFileExtensionFilter);
                fc.getExtensionFilters().add(anyFilesExtensionFilter);
                File target = fc.showOpenDialog(menuBar.getScene().getWindow());
                if(target != null && target.exists()) {
                    try {
                        SaveData data = new SaveData();
                        data.loadFile(target);
                        load(data);
                        ((Stage)menuBar.getScene().getWindow()).setTitle(gis("title") + " - " + target.getName());
                        setLastDirectory(target);
                        openFile = target;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            fileMenu.getItems().add(open);
            fileMenu.getItems().add(save);
            fileMenu.getItems().add(saveAs);
            fileMenu.getItems().add(loadXml);
        Menu deployMenu = new Menu(gis("deploy"));
            MenuItem buildOption = new MenuItem(gis("build"));
            buildOption.setOnAction((ActionEvent event) -> {
                buildProgram();
            });
            MenuItem uploadOption = new MenuItem(gis("upload"));
            if(Main.hasPlatformIO) {
                uploadOption.setOnAction((ActionEvent event) -> {
                    File buildFolder = new File(tempDirectoryPath + "build");
                    if(buildFolder.exists()) {
                        deleteDirectory(buildFolder);
                    }
                    File savedTo = buildProgram(buildFolder);
                    try {
                        Process p = PlatformIO.runCommand(savedTo, "run", "--target", "upload");
                        ((TerminalTabPaneController)screens.get("terminal").getController()).addTerminal("PIO run", "run");
                        InputStream err = p.getErrorStream();
                        InputStream out = p.getInputStream();
                        TerminalTabPaneController ttpc = (TerminalTabPaneController)screens.get("terminal").getController();
                        TerminalPaneController tpc = ttpc.getTerminal("run").getController();
                        Tab tt = ((TerminalTabPaneController)screens.get("terminal").getController()).getTerminal("run").getTab();

                        OutputStream os = new OutputStream() {
                            @Override
                            public void write(int b) throws IOException {
                                tpc.write(((char)b) + "");
                            }

                            @Override
                            public void write(byte b[]) throws IOException {
                                if (b == null) {
                                    throw new NullPointerException();
                                }
                                tpc.write(new String(b));
                            }

                            @Override
                            public void write(byte b[], int off, int len) throws IOException {
                                if (b == null) {
                                    throw new NullPointerException();
                                } else if ((off < 0) || (off > b.length) || (len < 0) ||
                                           ((off + len) > b.length) || ((off + len) < 0)) {
                                    throw new IndexOutOfBoundsException();
                                } else if (len == 0) {
                                    return;
                                }
                                write(Arrays.copyOfRange(b, off, len));
                            }
                        };

                        Thread t = new Thread(() -> {
                            while(p.isAlive()) {
                                try {
                                    while(err.available() > 0) {
                                        os.write(err.read());
                                    }
                                    while(out.available() > 0) {
                                        os.write(out.read());
                                    }
                                    Thread.sleep(10);
                                } catch (IOException | InterruptedException ex) {
                                    ex.printStackTrace();
                                    return;
                                }
                            }
                        });
                        t.start();

                        tt.setOnCloseRequest((closeEvent) -> {
                            if(p.isAlive()) {
                                Alert a = new Alert(Alert.AlertType.CONFIRMATION, gis("kill_process"), ButtonType.YES, ButtonType.NO);
                                if(a.showAndWait().get() == ButtonType.YES) {
                                    p.destroy();
                                }
                                else {
                                    closeEvent.consume();
                                }
                            }
                        });
                        tt.setClosable(true);
                        setScreen("terminal");
                        ttpc.getTabPane().getSelectionModel().select(tt);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            else {
                uploadOption.setOnAction((event) -> {
                    Alert a = new Alert(AlertType.ERROR, gis("pio_not_present"), ButtonType.OK);
                    a.showAndWait();
                });
            }
            deployMenu.getItems().add(uploadOption);
            deployMenu.getItems().add(buildOption);
        Menu toolsMenu = new Menu(gis("tools"));
            MenuItem pwmServosTuningOption = new MenuItem(gis("pwm_servos_tuning"));
            if(Main.hasPlatformIO) {
                pwmServosTuningOption.setOnAction((ActionEvent event) -> {
                    Alert dialog = new Alert(AlertType.CONFIRMATION, gis("rewrite_settings_confirm"), ButtonType.YES, ButtonType.NO);
                    ButtonType result = dialog.showAndWait().get();
                    if(result == ButtonType.YES) {
                        File buildFolder = new File(tempDirectoryPath + "build");
                        if(buildFolder.exists()) {
                            deleteDirectory(buildFolder);
                        }
                        File savedTo = buildProgram(buildFolder.getParentFile());
                        try {
                            Process p = PlatformIO.runCommand(savedTo, "run", "--target", "upload");
                            ((TerminalTabPaneController)screens.get("terminal").getController()).addTerminal("PIO run", "run");
                            InputStream err = p.getErrorStream();
                            InputStream out = p.getInputStream();
                            TerminalTabPaneController ttpc = (TerminalTabPaneController)screens.get("terminal").getController();
                            TerminalPaneController tpc = ttpc.getTerminal("run").getController();
                            Tab tt = ((TerminalTabPaneController)screens.get("terminal").getController()).getTerminal("run").getTab();

                            OutputStream os = new OutputStream() {
                                @Override
                                public void write(int b) throws IOException {
                                    tpc.write(((char)b) + "");
                                }

                                @Override
                                public void write(byte b[]) throws IOException {
                                    if (b == null) {
                                        throw new NullPointerException();
                                    }
                                    tpc.write(new String(b));
                                }

                                @Override
                                public void write(byte b[], int off, int len) throws IOException {
                                    if (b == null) {
                                        throw new NullPointerException();
                                    } else if ((off < 0) || (off > b.length) || (len < 0) ||
                                               ((off + len) > b.length) || ((off + len) < 0)) {
                                        throw new IndexOutOfBoundsException();
                                    } else if (len == 0) {
                                        return;
                                    }
                                    write(Arrays.copyOfRange(b, off, len));
                                }
                            };

                            Thread t = new Thread(() -> {
                                while(p.isAlive()) {
                                    try {
                                        while(err.available() > 0) {
                                            os.write(err.read());
                                        }
                                        while(out.available() > 0) {
                                            os.write(out.read());
                                        }
                                        Thread.sleep(10);
                                    } catch (IOException | InterruptedException ex) {
                                        ex.printStackTrace();
                                        return;
                                    }
                                }
                            });
                            t.start();

                            tt.setOnCloseRequest((closeEvent) -> {
                                if(p.isAlive()) {
                                    Alert a = new Alert(Alert.AlertType.CONFIRMATION, gis("kill_process"), ButtonType.YES, ButtonType.NO);
                                    if(a.showAndWait().get() == ButtonType.YES) {
                                        p.destroy();
                                    }
                                    else {
                                        closeEvent.consume();
                                    }
                                }
                            });
                            tt.setClosable(true);
                            setScreen("terminal");
                            ttpc.getTabPane().getSelectionModel().select(tt);

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
            else {
                pwmServosTuningOption.setOnAction((event) -> {
                    Alert a = new Alert(AlertType.ERROR, gis("pio_not_present"), ButtonType.OK);
                    a.showAndWait();
                });
            }
            toolsMenu.getItems().add(pwmServosTuningOption);
        Menu optionsMenu = new Menu(gis("options"));
            Menu langSubMenu = new Menu(gis("language"));
                ToggleGroup languageGroup = new ToggleGroup();
                RadioMenuItem enOption = new RadioMenuItem("English");
                enOption.setOnAction((ActionEvent event) -> {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, gis("restart_apply_changes"), ButtonType.OK);
                    a.showAndWait();
                    openwing.Main.preferences.put("locale", "en");
                });
                RadioMenuItem csOption = new RadioMenuItem("Česky");
                csOption.setOnAction((ActionEvent event) -> {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, gis("restart_apply_changes"), ButtonType.OK);
                    a.showAndWait();
                    openwing.Main.preferences.put("locale", "cs");
                });
                languageGroup.getToggles().add(enOption);
                languageGroup.getToggles().add(csOption);
                switch(openwing.Main.locale) {
                    case "cs":
                        languageGroup.selectToggle(csOption);
                        break;
                    case "en":
                        languageGroup.selectToggle(enOption);
                        break;
                }
                langSubMenu.getItems().add(enOption);
                langSubMenu.getItems().add(csOption);
            Menu pioSubMenu = new Menu(gis("platformio"));
                MenuItem pioHome = new MenuItem(gis("pio_settings"));
                pioHome.setOnAction((ActionEvent event) -> {
                    try {
                        Process p = PlatformIO.runCommand(new File("."), "home");
                        Alert a = new Alert(AlertType.INFORMATION, gis("pio_starting_settings"), ButtonType.CANCEL);
                        Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(!p.isAlive()) {
                                    Platform.runLater(() -> {
                                        a.close();
                                        t.cancel();
                                    });
                                }
                            }
                        }, 0, 100);
                        a.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                MenuItem pioPath = new MenuItem(gis("pio_path"));
                if(Main.hasPlatformIO) {
                    pioPath.setOnAction((ActionEvent event) -> {
                        FileChooser fc = new FileChooser();
                        File init = new File(PlatformIO.getPlatformioPath());
                        if(init.exists()) {
                            fc.setInitialDirectory(init);
                        }
                        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PlatformIO executable", "platformio.exe"));
                        File target = fc.showOpenDialog(menuBar.getScene().getWindow());
                        if(target != null && target.getPath().endsWith("platformio.exe")) {
                            String path = target.getParent();
                            if(!path.endsWith("/") && !path.endsWith("\\")) {
                                path += File.separator;
                            }
                            try {
                                boolean result = PlatformIO.checkPIO(path);
                                if(result) {
                                    Alert a = new Alert(AlertType.INFORMATION, gis("success_excl"), ButtonType.OK);
                                    a.showAndWait();
                                    PlatformIO.setPlatformioPath(path);
                                    openwing.Main.preferences.put("pio_path", path);
                                }
                                else {
                                    Alert a = new Alert(AlertType.ERROR, gis("pio_not_present_directory"), ButtonType.OK);
                                    a.showAndWait();
                                }
                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                                Alert a = new Alert(AlertType.INFORMATION, gis("pio_not_present_directory"), ButtonType.OK);
                                a.showAndWait();
                            }
                        }
                    });
                }
                else {
                    pioPath.setOnAction((event) -> {
                        Alert a = new Alert(AlertType.ERROR, gis("pio_not_present"), ButtonType.OK);
                        a.showAndWait();
                    });
                }
                pioSubMenu.getItems().add(pioPath);
                pioSubMenu.getItems().add(pioHome);
                
            optionsMenu.getItems().add(pioSubMenu);
            optionsMenu.getItems().add(langSubMenu);
        Menu helpMenu = new Menu(gis("help"));
            MenuItem wiki = new MenuItem(gis("wiki"));
            wiki.setOnAction((event) -> {
                try {
                    WebBrowserOpener.open(Main.wikiURL);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            MenuItem github = new MenuItem(gis("github_repo"));
            github.setOnAction((event) -> {
                try {
                    WebBrowserOpener.open(Main.githubRepoURL);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            helpMenu.getItems().add(wiki);
            helpMenu.getItems().add(github);
        
        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(deployMenu);
        menuBar.getMenus().add(toolsMenu);
        menuBar.getMenus().add(optionsMenu);
        menuBar.getMenus().add(helpMenu);
        try {
            addScreen(gis("parts"), getClass().getResource("/openwing/view/ComponentsPane.fxml"), bundle, "parts");
            addScreen(gis("receiver"), getClass().getResource("/openwing/view/ReceiverPane.fxml"), bundle, "receiver");
            addScreen(gis("flight_modes"), getClass().getResource("/openwing/view/FlightModesPane.fxml"), bundle, "flight_modes");
            addScreen(gis("regulators"), getClass().getResource("/openwing/view/RegulatorsPane.fxml"), bundle, "regulators");
            addScreen(gis("terminal"), getClass().getResource("/openwing/view/TerminalTabPane.fxml"), bundle, "terminal");
            addScreen(gis("parameters_list"), getClass().getResource("/openwing/view/ParametersListPane.fxml"), bundle, "parameters_list");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        screens.get(lastScreenKey).setVisible(true);
        
        ((TerminalTabPaneController)screens.get("terminal").getController()).addTerminal("Serial", "serial");
        ((TerminalTabPaneController)screens.get("terminal").getController()).getTerminal("serial").getTab().setClosable(false);
        
        portNamesBackList = Arrays.asList(SerialPortList.getPortNames());
        portNames = FXCollections.observableArrayList(portNamesBackList);
        portChooser.setItems(portNames);
        
        List<Integer> baudRateOptionsBackList = Arrays.asList(new Integer[] {110, 300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 38400, 57600, 115200, 128000, 256000});
        ObservableList<Integer> baudRateOptions = FXCollections.observableArrayList(baudRateOptionsBackList);
        baudRateChooser.setItems(baudRateOptions);
        baudRateChooser.getSelectionModel().select(new Integer(38400));
        
        portUpdater.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!(Arrays.equals(SerialPortList.getPortNames(), portNames.toArray()))) {
                    Platform.runLater(() -> {
                        String item = portChooser.getSelectionModel().getSelectedItem();
                        portNames.setAll(Arrays.asList(SerialPortList.getPortNames()));
                        if(portChooser.getItems().contains(item)) {
                            portChooser.getSelectionModel().select(item);
                        }
                    });
                }
            }
        }, 0, 500);
        
        serialButton.setOnAction((event) -> {
            if(connected) {
                try {
                    serialPort.closePort();
                } catch (SerialPortException ex) {
                    ex.printStackTrace();
                }
                serialButton.setText(gis("connect"));
                connected = false;
            }
            else {
                serialPort = new SerialPort(portChooser.getSelectionModel().getSelectedItem());
                try {
                    serialPort.openPort();
                    serialPort.setParams(baudRateChooser.getSelectionModel().getSelectedItem(), 
                                 SerialPort.DATABITS_8,
                                 SerialPort.STOPBITS_1,
                                 SerialPort.PARITY_NONE);
                    serialPort.addEventListener((serialPortEvent) -> {
                        if(serialPortEvent.isRXCHAR()) {
                            try {
                                ((TerminalTabPaneController)screens.get("terminal").getController()).getTerminal("serial").getController().write(serialPort.readString());
                            } catch (SerialPortException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    connected = true;
                    serialButton.setText(gis("disconnect"));
                } catch (SerialPortException ex) {
                    ex.printStackTrace();
                }
            }
        });
        Platform.runLater(() -> {
            menuBar.getScene().getWindow().setOnCloseRequest((event) -> {
                Alert dialog = new Alert(AlertType.CONFIRMATION, gis("close_confirm"), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                ButtonType result = dialog.showAndWait().get();
                if(result == ButtonType.YES) {
                    if(openFile != null && openFile.exists()) {
                        try {
                            SaveData data = new SaveData();
                            save(data);
                            data.saveFile(openFile);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    else {
                        FileChooser fc = new FileChooser();
                        fc.setInitialDirectory(getInitialDirectory());
                        fc.getExtensionFilters().add(saveFileExtensionFilter);
                        fc.getExtensionFilters().add(anyFilesExtensionFilter);
                        File target = fc.showSaveDialog(menuBar.getScene().getWindow());
                        if(target != null) {
                            try {
                                SaveData data = new SaveData();
                                save(data);
                                data.saveFile(target);
                                ((Stage)menuBar.getScene().getWindow()).setTitle(gis("title") + " - " + target.getName());
                                openFile = target;
                                setLastDirectory(target);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                event.consume();
                            }
                        }
                        else {
                            event.consume();
                        }
                    }
                }
                else if(result == ButtonType.NO) {

                }
                else {
                    event.consume();
                }
            });
        });
    }
    
    private void addScreen(String buttonLabel, URL resource, ResourceBundle bundle, String key) throws IOException {
        Pane content;
        FXMLLoader loader = new FXMLLoader(resource, bundle);
        content = loader.load();
        Initializable controller = loader.getController();
        Button button = new Button(buttonLabel);
        button.setOnAction((ActionEvent event) -> {
            setScreen(key);
        });
        button.setPrefSize(100, 100);
        button.setWrapText(true);
        button.setTextAlignment(TextAlignment.CENTER);
        FlowPane.setMargin(button, new Insets(1, 0, 0, 0));
        controlButtonPane.getChildren().add(button);
        contentPane.getChildren().add(content);
        content.setVisible(false);
        screens.put(key, new Screen(button, content, controller));
    }
    
    private void setScreen(String key) {
        screens.get(lastScreenKey).setVisible(false);
        screens.get(key).setVisible(true);
        lastScreenKey = key;
    }
    
    private String gis(String key) {
        return bundle.getString(key);
    }
    
    void deleteDirectory(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDirectory(f);
                }
            }
        }
        file.delete();
    }
    
    public void buildProgram() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(getInitialDirectory());
        File target = dc.showDialog(menuBar.getScene().getWindow());
        if(target != null) {
            setLastDirectory(target);
            buildProgram(target);
        }
    }
    
    public File buildProgram(File target) {
        File root = target;
        int i = 0;
        while(target.exists() && target.list().length > 0) {
            target = new File(root, (i == 0) ? ("build") : ("build" + i));
            i++;
        }
        
        if(!target.exists()) {
            target.mkdirs();
        }
        List<Component> components = ((ComponentsPaneController)screens.get("parts").getController()).getComponents();
        components.add(((ReceiverPaneController)screens.get("receiver").getController()).getReceiver());
        List<FlightMode> modes = ((FlightModesPaneController)screens.get("flight_modes").getController()).getFlightModes();
        Program prog = new Program(modes, components);
        prog.generate();
        
        URL source = getClass().getResource("");
        
        String pkg = "openwing/files/base/";
 
        try {
            JarURLConnection jarConnection = (JarURLConnection) source.openConnection();

            ZipFile jar = jarConnection.getJarFile();

            Enumeration<? extends ZipEntry> entries = jar.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if(name.startsWith(pkg)) {
                    String fileName = name.substring(pkg.length());

                    File file = new File(target.getPath() + File.separator + fileName);
                    if(entry.isDirectory()) {
                        file.mkdir();
                    }
                    else {
                        InputStream is = jar.getInputStream(entry);
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                        byte buffer[] = new byte[4096];
                        int length;
                        while((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                        os.close();
                        is.close();
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        File file = new File(target.getPath() + File.separator + "src" + File.separator + "main.cpp");
        file.getParentFile().mkdirs();
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(prog.getCode());
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        try {
            copyFolder(libraryFolder, new File(target, "lib"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return target;
    }
    
    public File buildPwmServosTuning(File target) {
        File root = new File(target.getAbsolutePath());
        int i = 0;
        while(target.exists() && target.list().length > 0) {
            target = new File(root, (i == 0) ? ("build") : ("build" + i));
            i++;
        }
        
        if(!target.exists()) {
            target.mkdirs();
        }
        PWMServosTuning prog = new PWMServosTuning();
        prog.generate();
        
        URL source = getClass().getResource("");
        
        String pkg = "files/PWMServosTuning/";
 
        try {
            JarURLConnection jarConnection = (JarURLConnection) source.openConnection();

            ZipFile jar = jarConnection.getJarFile();

            Enumeration<? extends ZipEntry> entries = jar.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if(name.startsWith(pkg)) {
                    String fileName = name.substring(pkg.length());

                    File file = new File(target.getPath() + File.separator + fileName);
                    if(entry.isDirectory()) {
                        file.mkdir();
                    }
                    else {
                        InputStream is = jar.getInputStream(entry);
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                        byte buffer[] = new byte[4096];
                        int length;
                        while((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                        os.close();
                        is.close();
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        File file = new File(target.getPath() + File.separator + "src" + File.separator + "main.cpp");
        file.getParentFile().mkdirs();
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(prog.getCode());
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        try {
            copyFolder(libraryFolder, new File(target, "lib"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return target;
    }
    
    public static void copyFolder(File source, File target) {
        if(source.exists() && source.isDirectory()) {
            if (!target.exists()) {
                target.mkdir();
            }

            for (String fileName : source.list()) {
                copyFolder(new File(source, fileName), new File(target, fileName));
            }
        }
        else {    
            InputStream is = null;
            OutputStream os = null;
            try {
                is = new FileInputStream(source);
                os = new FileOutputStream(target);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.close();
                is.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void save(SaveData data) {
        for(Map.Entry<String, Screen> screenEntry : screens.entrySet()) {
            if(screenEntry.getValue().getController() instanceof SaveLoadAble) {
                ((SaveLoadAble)screenEntry.getValue().getController()).save(data);
            }
        }
        
        for(Component component : Main.dataBundle.getComponents()) {
            component.save(data);
        }
        
        for(FlightMode flightMode : Main.dataBundle.getFlightModes()) {
            flightMode.save(data);
        }
    }

    @Override
    public void load(SaveData data) {
        for(Map.Entry<String, Screen> screenEntry : screens.entrySet()) {
            if(screenEntry.getValue().getController() instanceof SaveLoadAble) {
                ((SaveLoadAble)screenEntry.getValue().getController()).load(data);
            }
        }
        
        for(Component component : Main.dataBundle.getComponents()) {
            component.load(data);
        }
        
        for(FlightMode flightMode : Main.dataBundle.getFlightModes()) {
            flightMode.load(data);
        }
    }
    
    public static void setLastDirectory(File f) {
        if(f.isDirectory()) {
            lastDirectory = f;
            Main.preferences.put("last_directory", lastDirectory.getAbsolutePath());
        }
        else if(f.isFile()) {
            lastDirectory = f.getParentFile();
            Main.preferences.put("last_directory", lastDirectory.getAbsolutePath());
        }
    }
    
    public static File getInitialDirectory() {
        if(lastDirectory.exists()) {
            return lastDirectory;
        }
        else {
            return new File("./");
        }
    }
}

class Screen {
    Button controlButton;
    Pane content;
    Initializable controller;
    
    public Screen(Button controlButton, Pane content, Initializable controller) {
        this.controlButton = controlButton;
        this.content = content;
        this.controller = controller;
    }
    
    public void setVisible(boolean val) {
        content.setVisible(val);
    }
    
    public Initializable getController() {
        return controller;
    }
}