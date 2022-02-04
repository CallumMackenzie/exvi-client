/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model;

import java.util.Random;

/**
 *
 * @author callum
 */
public class RandomWrapper extends Random {

    public double doubleInRange(double min, double max) {
        return this.nextDouble() * (max - min) + min;
    }

    public int intInRange(int min, int max) {
        return (int) this.doubleInRange(min, max);
    }
}
