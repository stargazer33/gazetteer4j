package com.remote4me.gazetteer4j.utils;

import com.remote4me.gazetteer4j.AlternateNameRecord;
import com.remote4me.gazetteer4j.DocFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * TODO
 */
public class IndexBuilder {

    private Analyzer analyzer;
    private DocFactory docFactory;

    private static Logger LOG = Logger.getLogger( IndexBuilder.class.getName() );

    private Function<String[], Boolean> shouldIndexCallback;

    private Map<Integer, AlternateNameRecord> idToAlternateMap;
    private int count = 0;

    public IndexBuilder(Analyzer analyzer,
                        DocFactory docFactory,
                        Function<String[], Boolean> shouldIndexCallback,
                        Map<Integer, AlternateNameRecord> idToAlternateMap
    ) {
        this.analyzer = analyzer;
        this.docFactory = docFactory;
        this.shouldIndexCallback = shouldIndexCallback;
        this.idToAlternateMap = idToAlternateMap;
    }

    /**
     * Build the com.remote4me.gazetteer4j index line by line
     *
     * @param geonamesFile
     *            path of the com.remote4me.gazetteer4j file
     * @param indexDirectoryPath
     *            path to the created Lucene index directory.
     * @throws IOException when something went wrong
     */
    public void buildIndex(String geonamesFile, String indexDirectoryPath) throws IOException
    {
        LOG.log(Level.INFO, "Start Building Index: ["+geonamesFile+"] -> ["+indexDirectoryPath+"]");
        Directory indexDir = FSDirectory.open(new File(indexDirectoryPath).toPath());

        if (DirectoryReader.indexExists(indexDir)) {
            LOG.log(Level.SEVERE, "Index already exists in " + indexDirectoryPath );
            return;
        }

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter indexWriter = new IndexWriter(indexDir, config)) {
            try (Stream<String> stream = Files.lines(Paths.get(geonamesFile))) {
                stream.forEach((String line) -> {
                            count += 1;
                            if (count % 1000000 == 0) {
                                LOG.log(Level.INFO, ""+count);
                            }
                            String[] tokens = line.split("\t");
                            try {
                                if (docFactory.shouldAddToIndex(tokens) &&
                                        shouldIndexCallback.apply(tokens)
                                        ) {
                                    Document doc = docFactory.createFromLineInGeonamesFile(
                                            tokens,
                                            idToAlternateMap
                                    );
                                    indexWriter.addDocument(doc);
                                }
                            } catch (IOException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                );
            }

            LOG.log(Level.INFO, "Building Finished");
        } // IndexWriter
    }


}
