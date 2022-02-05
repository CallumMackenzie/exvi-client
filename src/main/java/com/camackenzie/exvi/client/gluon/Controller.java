/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gluon;

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
        this(false);
    }

    public final void cacheFXML(Views... views) {
        for (int i = 0; i < views.length; ++i) {
            if (shouldPreCache) {
                this.toCache.add(views[i].getID());
                this.toCache.add(views[i].getPath());
            }
            this.viewPaths.put(views[i].getID(), views[i].getPath());
        }
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

    private void setView(String name, Stage stage) {
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

    public final void setView(Views view, Node node) {
        this.setView(view, (Stage) node.getScene().getWindow());
    }

    public final void setView(Views view, Stage stage) {
        if (this.viewPaths.containsValue(view.getPath())) {
            this.setView(view.getID(), stage);
        } else {
            this.viewManager.cacheView(view.getID(), view.getURL());
            this.setView(view.getID(), stage);
        }
    }

    public final ViewManager getViewManager() {
        return this.viewManager;
    }

}
