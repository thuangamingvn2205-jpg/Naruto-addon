package com.example.narutomod.data;

public enum Rank {
    ACADEMY("academy", "Học viên Học viện", 50),
    GENIN("genin", "Genin", 100),
    CHUNIN("chunin", "Chunin", 160),
    JONIN("jonin", "Jonin", 260),
    ELITE_JONIN("elite_jonin", "Jonin tinh nhuệ", 380),
    KAGE("kage", "Kage", 550);

    public final String id;
    public final String display;
    public final int baseChakra;

    Rank(String id, String display, int baseChakra) {
        this.id = id;
        this.display = display;
        this.baseChakra = baseChakra;
    }

    public boolean atLeast(Rank other) {
        return this.ordinal() >= other.ordinal();
    }

    public static Rank fromId(String s) {
        for (Rank r : values()) {
            if (r.id.equalsIgnoreCase(s)) return r;
        }
        return null;
    }
}
