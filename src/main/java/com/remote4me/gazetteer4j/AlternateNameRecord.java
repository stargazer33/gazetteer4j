package com.remote4me.gazetteer4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by dima2 on 21.07.18.
 */
public class AlternateNameRecord {

    public int id;
    public String preferredName;
    public String shortName;
    public List<String> names = new ArrayList();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlternateNameRecord)) return false;
        AlternateNameRecord that = (AlternateNameRecord) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}