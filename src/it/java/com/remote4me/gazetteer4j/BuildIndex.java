package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.utils.CompositeIndexBuilder;

import java.io.IOException;

/**
 * Created by dima2 on 15.07.18.
 */
public class BuildIndex {

    public void doBuild() throws IOException {

        new CompositeIndexBuilder().buildIndex();
    }

}
