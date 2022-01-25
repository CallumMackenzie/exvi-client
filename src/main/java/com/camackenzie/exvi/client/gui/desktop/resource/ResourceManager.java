/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.resource;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author callum
 *
 * Manages operations on class resources.
 */
public class ResourceManager {

    public static Image getImage(String name) {
        try {
            return ImageIO.read(ResourceManager.class.getResourceAsStream(
                    "/" + name));
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }

    public static ImageIcon getImageIcon(String name) {
        return new ImageIcon(ResourceManager.class.getResource("/" + name));
    }

}
