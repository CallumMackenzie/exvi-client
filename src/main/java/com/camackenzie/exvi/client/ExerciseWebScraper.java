/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client;

import com.camackenzie.exvi.core.Exercise;
import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 *
 * @author callum
 */
public interface ExerciseWebScraper {

    Future<ArrayList<Exercise>> scrape();
}
