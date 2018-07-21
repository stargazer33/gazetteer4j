package com.remote4me.gazetteer4j.utils;

import com.remote4me.gazetteer4j.AlternateNameRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by dima2 on 21.07.18.
 */
public class AlternateNamesFromFile {

    private static final Logger LOG = Logger.getLogger(AlternateNamesFromFile.class.getName());

    private Map<Integer, AlternateNameRecord> idToRecordMap = new HashMap<>();

    private int count = 1;

    public void processAlternateNames(String alternateFile) throws IOException {
        LOG.log(Level.INFO, "Start reading: [" + alternateFile + "] ");

        try (Stream<String> stream = Files.lines(Paths.get(alternateFile))) {
            stream.forEach((String line) ->
                {
                    if (count % 1000000 == 0) {
                        LOG.log(Level.INFO, "" + count);
                    }
                    String[] tokens = line.split("\t");
                    try {

                        processOneLine(tokens);
                        count++;

                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
            );
        }

        LOG.log(Level.INFO, "Reading Finished");
    }

    private void processOneLine(String[] tokens) throws IOException {
        int id = 0;
        String isolanguage = "";
        String alternateName = "";
        String isPreferredName = "";
        String isShortName = "";

        try {
            id = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            throw new IOException(e);
        }

        try {
            isolanguage = tokens[2];
            alternateName = tokens[3];
            isPreferredName = tokens[4];
            isShortName = tokens[5];
        } catch (ArrayIndexOutOfBoundsException e){
            return;
        }

        if (isolanguage.equals("en") && (
                isPreferredName.contains("1") || isShortName.contains("1"))
                )
        {
            AlternateNameRecord record = idToRecordMap.get(id);
            if (record == null) {
                record = new AlternateNameRecord();
                idToRecordMap.put(id, record);
            }

            if (isPreferredName.contains("1")) {
                record.preferredName = alternateName;
            }
            if (isShortName.contains("1")) {
                record.shortName = alternateName;
            }
        }
    }

    public Map<Integer, AlternateNameRecord> getIdToRecordMap() {
        return idToRecordMap;
    }
}
