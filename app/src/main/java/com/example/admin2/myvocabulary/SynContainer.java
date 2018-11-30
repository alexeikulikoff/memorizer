package com.example.admin2.myvocabulary;

import java.util.Set;
import java.util.TreeSet;

public class SynContainer {
    private Set<Collocation> collocations;

    public SynContainer() {
        collocations = new TreeSet<>();
    }
    public void add(Collocation c) {
        collocations.add(c);
    }
    public Set<Collocation> getCollocatins() {
        return collocations;
    }
    public void setCollocatins(Set<Collocation> collocatins) {
        this.collocations = collocatins;
    }
    @Override
    public String toString() {
        return collocations.toString();
    }
}
