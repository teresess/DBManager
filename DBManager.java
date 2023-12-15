package upd.dev.dbmanager;

public class DBManager {
    public static DBManagerBuilder createConnection() {
        return new DBManagerBuilder();
    }
}
