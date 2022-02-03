/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.javafx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 *
 * @author callum
 */
public abstract class Controller implements Initializable {

    private final ViewManager viewManager;
    private final HashMap<String, String> viewPaths;
    private final boolean shouldPreCache;

    private final ArrayList<String> toCache;

    public Controller(boolean shouldPreCache) {
        this.viewManager = new ViewManager();
        this.toCache = new ArrayList<>();
        this.viewPaths = new HashMap<>();
        this.shouldPreCache = shouldPreCache;
    }

    public Controller() {
        this(true);
    }

    public final void cacheFXML(String... pairs) {
        assert (pairs.length % 2 == 0);
        for (int i = 0; i < pairs.length; i += 2) {
            if (shouldPreCache) {
                this.toCache.add(pairs[i]);
                this.toCache.add(pairs[i + 1]);
            }
            this.viewPaths.put(pairs[i], pairs[i + 1]);
        }
    }

    public final void setupCache() {
        if (this.shouldPreCache) {
            this.viewManager
                    .cacheFromPairs(this.toCache.toArray(sz -> new String[sz]));
        }
    }

    public final void setView(String name, Node node) {
        this.setView(name, (Stage) node.getScene().getWindow());
    }

    public final void setView(String name, Stage stage) {
        if (this.shouldPreCache) {
            stage.getScene().setRoot(viewManager.getFXML(name));
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(this.viewPaths.get(name)));
                if (loader.getController() instanceof Controller) {
                    ((Controller) loader.getController()).setupCache();
                }
                stage.getScene().setRoot(loader.load());
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }

    public final ViewManager getViewManager() {
        return this.viewManager;
    }

}
