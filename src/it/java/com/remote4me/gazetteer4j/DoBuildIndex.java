package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.index.CompositeIndexBuilder;

import java.io.IOException;

/**
 * Created by dima2 on 15.07.18.
 */
public class DoBuildIndex {

    public void doBuild() throws IOException {

        new CompositeIndexBuilder().buildIndex();
    }

}
