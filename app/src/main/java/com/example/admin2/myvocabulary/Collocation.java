package com.example.admin2.myvocabulary;

import java.util.Set;
import java.util.TreeSet;

public class Collocation implements Comparable<Collocation> {

    private String original;
    private Set<String> synonims;

    public Collocation(String o) {
        original = o;
        synonims = new TreeSet<>();
    }
    public void addSyn(String s) {
        synonims.add(s);
    }
    public String getOriginal() {
        return original;
    }
    public void setOriginal(String original) {
        this.original = original;
    }
    public Set<String> getSynonims() {
        return synonims;
    }
    public void setSynonims(Set<String> synonims) {
        this.synonims = synonims;
    }

    public int compareTo(Collocation col) {
        return original.compareTo(col.original);
    }
    @Override
    public String toString() {
        return original + ": " +  synonims.toString() ;
    }


}