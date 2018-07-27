package com.remote4me.gazetteer4j.index;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * TODO
 */
public class IndexBuilder {

    private static Logger LOG = Logger.getLogger( IndexBuilder.class.getName() );

    private Analyzer analyzer;
    private DocFactory docFactory;
    private IndexFilter indexFilter;
    private Map<Integer, AlternateNameRecord> idToAlternateMap;
    private int count = 0;
    private Map<String, Location> adm1ToIdMap;
    private Map<String, Location> countryToIdMap;

    /**
     *
     * @param analyzer Index-time analyzer
     * @param docFactory knows how to create Lucene Document objects from records in Geoname file
     * @param indexFilter knows which records in Geoname file should be added to Lucene index
     * @param idToAlternateMap - used to obtain "preferred name" stored in AlternateNameRecord; key: geoname id
     * @param adm1ToIdMap - used to find the ADM1 name; key: ADM1 code
     * @param countryToIdMap - used to find the country names; key: country code
     */
    public IndexBuilder(Analyzer analyzer,
                        DocFactory docFactory,
                        IndexFilter indexFilter,
                        Map<Integer, AlternateNameRecord> idToAlternateMap,
                        Map<String, Location> adm1ToIdMap,
                        Map<String, Location> countryToIdMap)
    {
        this.analyzer = analyzer;
        this.docFactory = docFactory;
        this.indexFilter = indexFilter;
        this.idToAlternateMap = idToAlternateMap;
        this.adm1ToIdMap = adm1ToIdMap;
        this.countryToIdMap = countryToIdMap;
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
    public void init(String geonamesFile, String indexDirectoryPath) throws IOException
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
                                if ( indexFilter.shouldAddToIndex(tokens) )
                                {
                                    Document doc = docFactory.createFromLineInGeonamesFile(
                                            tokens,
                                            idToAlternateMap,
                                            adm1ToIdMap,
                                            countryToIdMap
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
