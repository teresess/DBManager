package upd.dev.dbmanager.parametrs;

public class Option {
    String key;
    String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Option(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Option add(String key, String value) {
        return new Option(key, value);
    }

    public static Option add(String value) {
        return new Option("", value);
    }
}
