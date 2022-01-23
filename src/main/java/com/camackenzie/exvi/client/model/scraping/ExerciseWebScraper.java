/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model.scraping;

import com.camackenzie.exvi.core.async.FutureWrapper;
import com.camackenzie.exvi.core.model.Exercise;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author callum
 */
public interface ExerciseWebScraper {

    FutureWrapper<List<Exercise>> scrape();

    public default void scrapeAndSaveToFileAsJson(String path) throws IOException {
        FutureWrapper<List<Exercise>> out = this.scrape();
        Gson gson = new Gson();
        Files.writeString(Path.of(path), gson.toJson(out.getFailOnError()
                .toArray(sz -> new Exercise[sz])));
    }
}
