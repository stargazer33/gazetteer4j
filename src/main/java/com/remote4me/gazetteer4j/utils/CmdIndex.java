package com.remote4me.gazetteer4j.utils;

import com.remote4me.gazetteer4j.index.CompositeIndexBuilder;

/**
 * Created by dima2 on 19.07.18.
 */
public class CmdIndex {

    public static void main(String[] args) throws Exception
    {
        new CompositeIndexBuilder().buildIndex();
    }
}
