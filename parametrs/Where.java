package upd.dev.dbmanager.parametrs;

public class Where {
    String where;
    String newValue;

    public String getKey() {
        return where;
    }

    public String getValue() {
        return newValue;
    }

    public Where(String where, String newValue) {
        this.where = where;
        this.newValue = newValue;
    }
    public static Where add(String where, String newValue) {
        return new Where(where, newValue);
    }
}
