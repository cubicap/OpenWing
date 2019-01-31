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
package openwing.util;

import java.io.IOException;

public class WebBrowserOpener {
    public static void open(String url) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("win")) {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
        } else if(os.contains("mac")) {
            Runtime.getRuntime().exec("open " + url);
        } else if(os.contains("nux") || os.contains("nix")) {
            Runtime.getRuntime().exec("xdg-open " + url);
        }
    }
}
