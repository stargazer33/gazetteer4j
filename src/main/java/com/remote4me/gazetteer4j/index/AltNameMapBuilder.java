package com.remote4me.gazetteer4j.index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 */
class AltNameMapBuilder {

    private static final Logger LOG = Logger.getLogger(AltNameMapBuilder.class.getName());

    /**
     * key: geoname id
     */
    private Map<Integer, AltNameRecord> idToRecordMap = new HashMap<>();

    private int count = 1;

    public void init(String alternateFile) throws IOException {
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
        addCustomNames();
        LOG.log(Level.INFO, "Reading Finished");
    }

    private void addCustomNames() {
        AltNameRecord record;

        record = idToRecordMap.get(6252001);
        record.namesList.add("USA");

        record = idToRecordMap.get(2635167);
        record.namesList.add("UK");

        record = idToRecordMap.get(5128581);
        record.namesList.add("NYC");
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

        if (isolanguage.equals("en")){
            AltNameRecord record = idToRecordMap.get(id);
            if (record == null) {
                record = new AltNameRecord();
                idToRecordMap.put(id, record);
            }

            if (isPreferredName.contains("1")) {
                record.preferredName = alternateName;
            }
            if (isShortName.contains("1")) {
                record.shortName = alternateName;
            }
            record.namesList.add(alternateName);
        }
    }

    public Map<Integer, AltNameRecord> getIdToRecordMap() {
        return idToRecordMap;
    }
}
