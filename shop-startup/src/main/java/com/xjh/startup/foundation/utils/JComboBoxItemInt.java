/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

/**
 * @author 36181
 */
public class JComboBoxItemInt {
    private int value;
    private String showSting;

    public JComboBoxItemInt(int value, String showSting) {
        this.value = value;
        this.showSting = showSting;
    }

    @Override
    public String toString() {
        return showSting;
    }

    public int getValue() {
        return value;
    }
}
