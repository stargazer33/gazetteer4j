package com.remote4me.gazetteer4j.index;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dima2 on 21.07.18.
 */
public class AltNameRecord {

    public int id;
    public String preferredName;
    public String shortName;
    public List<String> namesList = new ArrayList();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AltNameRecord)) return false;
        AltNameRecord that = (AltNameRecord) o;
        return id == that.id;
    }

    @Override
    public int hashCode() { return id; }
}
