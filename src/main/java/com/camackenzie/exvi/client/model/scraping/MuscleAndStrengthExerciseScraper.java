/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model.scraping;

import com.camackenzie.exvi.core.model.Exercise;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.ArrayList;
//import com.gargoylesoftware.htmlunit.*;
//import com.gargoylesoftware.htmlunit.html.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 *
 * @author callum
 */
public class MuscleAndStrengthExerciseScraper implements ExerciseWebScraper {

    private static final String SITE = "https://www.muscleandstrength.com/";

    @Override
    public Future<ArrayList<Exercise>> scrape() {
        throw new UnsupportedOperationException("");
    }

}
