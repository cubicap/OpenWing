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
package openwing;

import openwing.controller.MainWindowController;
import openwing.core.DataBundle;
import openwing.core.components.ComponentBuilder;
import openwing.core.flightmodes.FlightModeBuilder;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import openwing.platformio.PlatformIO;

public class Main extends Application {
    
    public static String locale = "en";
    public static String platformioPath = "";
    
    public static DataBundle dataBundle;
    
    public static Preferences preferences;
            
    public static File homeFolder;
    
    public static ResourceBundle rb;
    
    public static String githubRepoURL = "https://github.com/cubicap/OpenWing";
    public static String wikiURL = "https://github.com/cubicap/OpenWing/wiki";
    
    public static boolean hasPlatformIO = false;
    
    @Override
    public void start(Stage stage) throws Exception {
        hasPlatformIO = PlatformIO.checkPIO();
        
        homeFolder = new File(System.getProperty("user.home") + (System.getProperty("user.home").endsWith(File.separator) ? "" : File.separator) + "Documents" + File.separator + "OpenWing");
        
        if(!homeFolder.exists()) {
            homeFolder.mkdirs();
            new File(homeFolder, "Libraries").mkdirs();
        }
        preferences = Preferences.userNodeForPackage(getClass());
        
        locale = preferences.get("locale", "en");
        platformioPath = preferences.get("pio_path", "");
        MainWindowController.lastDirectory = new File(preferences.get("last_directory", ""));
        
        rb = ResourceBundle.getBundle("openwing.resources.Strings", new Locale(locale));
        
        PlatformIO.setPlatformioPath(platformioPath);
        
        dataBundle = new DataBundle(rb);
        
        dataBundle.addFlightModes(new FlightModeBuilder(rb).parseAllClasspath());
        dataBundle.addComponents(new ComponentBuilder(rb).parseAllClasspath());
        
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.setWidth(1280);
        stage.setHeight(720);
        
        Parent root = FXMLLoader.load(getClass().getResource("/openwing/view/MainWindow.fxml"), rb);
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/openwing/view/style.css");
        
        stage.setScene(scene);
        
        stage.setTitle(rb.getString("title"));
        
        stage.show();
    }
    
    @Override
    public void stop() {
        try {
            super.stop();
            Thread.sleep(500);
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
