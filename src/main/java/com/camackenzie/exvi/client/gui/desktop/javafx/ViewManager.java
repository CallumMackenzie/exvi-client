/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.javafx;

import com.camackenzie.exvi.core.async.Computation;
import com.camackenzie.exvi.core.async.ComputationFuture;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

/**
 *
 * @author callum
 */
public final class ViewManager {

    private final Map<String, ComputationFuture<FXMLLoadResult>> cachedViews;

    public ViewManager(String... viewPaths) {
        this(new HashMap<>());
        this.cacheFromPairs(viewPaths);
    }

    public ViewManager(Map<String, ComputationFuture<FXMLLoadResult>> futures) {
        this.cachedViews = futures;
    }

    public ViewManager(ViewManager base) {
        this(base.getFutures());
    }

    public void cacheFromPairs(String... viewPaths) {
        assert (viewPaths.length % 2 != 0);
        for (int i = 0; i < viewPaths.length; i += 2) {
            this.cacheViewFromClasspath(viewPaths[i],
                    viewPaths[i + 1]);
        }
    }

    public void cacheView(String name, URL url) {
        ComputationFuture<FXMLLoadResult> cf = new ComputationFuture(new Computation<FXMLLoadResult>() {
            private FXMLLoadResult result;

            @Override
            public FXMLLoadResult getResult() {
                return this.result;
            }

            @Override
            public void run() {
                long time = System.currentTimeMillis();
                try {
                    this.result = new FXMLLoadResult(url);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        cf.startComputation();
        this.cachedViews.put(name, cf);
    }

    public void cacheViewFromClasspath(String name, String path) {
        this.cacheView(name, getClass().getResource(path));
    }

    public Parent getFXML(String name) {
        FXMLLoadResult res = this.cachedViews.get(name).wrapped().getFailOnError();
        if (res.controller != null) {
            if (res.controller instanceof Controller) {
                ((Controller) res.controller).setupCache();
            }
        }
        return res.parent;
    }

    public Map<String, ComputationFuture<FXMLLoadResult>> getFutures() {
        return this.cachedViews;
    }

    private class FXMLLoadResult {

        FXMLLoader loader;
        Parent parent;
        Initializable controller;

        public FXMLLoadResult(URL url) throws IOException {
            this(new FXMLLoader(url));
        }

        public FXMLLoadResult(FXMLLoader loader) throws IOException {
            this.loader = loader;
            this.parent = loader.load();
            this.controller = loader.getController();
        }
    }

}
