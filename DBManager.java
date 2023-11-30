package upd.dev.dbmanager;

public class DBManager {
    public static DBManagerImpl createConnection() {
        return new DBManagerImpl();
    }
}
