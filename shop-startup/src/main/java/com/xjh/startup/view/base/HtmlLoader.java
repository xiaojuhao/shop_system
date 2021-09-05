package com.xjh.startup.view.base;

import java.net.URL;

public class HtmlLoader {
    public static URL load(String name) throws Exception {
        return HtmlLoader.class.getResource("/html/" + name).toURI().toURL();
    }
}
