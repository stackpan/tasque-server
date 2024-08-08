package io.github.stackpan.tasque.util;

public class Regexps {

    public static final String TIMESTAMP = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(.\\d{1,})?Z";

    public static final String UUID = "[0-9a-f]{8}\\b-[0-9a-f]{4}\\b-[0-9a-f]{4}\\b-[0-9a-f]{4}\\b-[0-9a-f]{12}";

    public static final String LIMITED_STRING = "^(?!\\btrue\\b|\\bfalse\\b)(?=.*[A-z])[A-z\\d\\s.,:]+$";

    public static final String COLOR_HEX = "^#[0-9a-f]{6}$";

}
