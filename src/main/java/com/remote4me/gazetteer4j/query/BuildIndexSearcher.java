package com.remote4me.gazetteer4j.query;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

/**
 * TODO
 */
public class BuildIndexSearcher {

    /**
     *
     * @param indexPath
     * @return
     * @throws IOException
     */
    public IndexSearcher createSearcher(String indexPath) throws IOException {

        Directory indexDir = FSDirectory.open(new File(indexPath).toPath());
        if (!DirectoryReader.indexExists(indexDir)) {
            throw new IOException("No Lucene Index Directory Found !");
        }
        return new IndexSearcher( DirectoryReader.open(indexDir) );
    }
}
