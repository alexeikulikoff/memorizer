package com.example.admin2.myvocabulary;

public class Couple implements Comparable<Couple> {
    private String english;
    private String russian;
    public Couple(String e, String r) {
        english = e;
        russian = r;
    }
    public String getEnglish() {
        return english;
    }
    public String getRussian() {
        return russian;
    }
    @Override
    public int compareTo(Couple comp) {
        return comp.english.equals(english) & comp.russian.equals(russian) ? 0 : -1;
    }
    @Override
    public String toString() {
        return english + " " + russian;
    }

}
