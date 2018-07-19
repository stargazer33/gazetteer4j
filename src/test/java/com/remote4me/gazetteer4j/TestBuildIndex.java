package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.utils.CompositeIndexBuilder;

import java.io.IOException;

/**
 * Created by dima2 on 15.07.18.
 */
public class TestBuildIndex {

    public void test1() throws IOException {
        new CompositeIndexBuilder().buildIndex();
    }

}
