package upd.dev.dbmanager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBManagerImpl {

    public Connection connection = null;
    private DBManagerImpl.Option option;
    private DBManagerImpl.Path path;
    private DBManagerImpl.Table table;
    private DBManagerImpl.Where where;
    private DBManagerImpl.Type type;
    public DBManagerImpl() {
        setOption(null);
        setWhere(null);
        setType(null);
        setPath(null);
        setTable(null);
    }
    private Map<String, String> getOption() {
        return option.getOption();
    }

    private String getPath() {
        return path.getPath();
    }

    private String getTable() {
        return table.getTable();
    }

    private Map<String, String> getWhere() {
        return where.getWhere();
    }
    private Types getType() {
        return type.getTypes();
    }
    public DBManagerImpl setType(Types type) {
        this.type = new Type(type);

        return this;
    }
    public DBManagerImpl setTable(String table) {
        this.table = new Table(table);

        return this;
    }
    public DBManagerImpl setWhere(Map<String, String> where) {
        this.where = new Where(where);

        return this;
    }
    public DBManagerImpl setOption(Map<String, String> option) {
        this.option = new Option(option);

        return this;
    }
    public DBManagerImpl setPath(String path) {
        this.path = new Path(path);

        return this;
    }
    public static class Path {
        private String path;
        public Path(String path) {
            this.path = path;
        }
        public String getPath() {
            return path;
        }
    }
    public static class Table {
        private String table;
        public Table(String table) {
            this.table = table;
        }

        public String getTable() {
            return table;
        }
    }
    public static class Option {
        private Map<String, String> option;
        public Option(Map<String, String> option) {
            this.option = option;
        }
        public Map<String, String> getOption() {
            return option;
        }
    }
    public static class Where {
        private Map<String, String> where;
        public Map<String, String> getWhere() {
            return where;
        }

        public Where(Map<String, String> where) {
            this.where = where;
        }
    }
    public static class Type {
        public Types getTypes() {
            return types;
        }
        private Types types;

        public Type(Types types) {
            this.types = types;
        }
    }
    public boolean checkOnReg() {
        boolean ret = false;

        if (getType() == Types.CHECK) {
            String where = "";
            List<String> keys = new ArrayList<>(), values = new ArrayList<>();

            getWhere().forEach((key, value) -> {
                keys.add(key);
                values.add("'%s'".formatted(value));
            });

            for (int i = 0;i < keys.size();i++) {
                where+="%s=%s".formatted(keys.get(i), values.get(i));

                if (i != keys.size()-1) {
                    where+=" AND ";
                }
            }

            String que = "SELECT COUNT(*) FROM %s WHERE %s".formatted(getTable(), where);
            PreparedStatement preparedStatement = exeQueImpl(que);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    ret = (count > 0);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }
    public List<List<String>> exeQue() {
        String que = "null";
        List<List<String>> returned = new ArrayList<>();

        if (getType() == Types.CREATE) {

            List<String> keys = new ArrayList<>(), values = new ArrayList<>();

            getOption().forEach((key, value) -> {
                keys.add(key);
                values.add("'%s'".formatted(value));
            });
            String s_keys = "", s_values = "";

            for (int i = 0;i < keys.size();i++) {
                s_keys+=keys.get(i);
                s_values+=values.get(i);

                if (i != keys.size()-1) {
                    s_keys+=", ";
                    s_values+=", ";
                }
            }

            que = "INSERT INTO %s (%s) VALUES (%s)".formatted(getTable(), s_keys, s_values);
        } else if (getType() == Types.DELETE) {
            String where = "";
            List<String> keys = new ArrayList<>(), values = new ArrayList<>();

            getWhere().forEach((key, value) -> {
                keys.add(key);
                values.add("'%s'".formatted(value));
            });

            for (int i = 0;i < keys.size();i++) {
                where+="%s=%s".formatted(keys.get(i), values.get(i));

                if (i != keys.size()-1) {
                    where+=" AND ";
                }
            }

            que = "DELETE FROM %s WHERE %s".formatted(getTable(), where);
        } else if (getType() == Types.UPDATE) {
            String update = "", where = "";

            List<String> update_col = new ArrayList<>(), new_update_col = new ArrayList<>(),
                    where_key = new ArrayList<>(), where_value = new ArrayList<>();

            getOption().forEach((key, value) -> {
                update_col.add(key);
                new_update_col.add("'%s'".formatted(value));
            });

            getWhere().forEach((key, value) -> {
                where_key.add(key);
                where_value.add("'%s'".formatted(value));
            });

            for (int i = 0;i < where_key.size();i++) {
                where+="%s=%s".formatted(where_key.get(i), where_value.get(i));

                if (i != where_key.size()-1) {
                    where+=" AND ";
                }
            }
            for (int i = 0;i < update_col.size();i++) {
                update+="%s=%s".formatted(update_col.get(i), new_update_col.get(i));

                if (i != update_col.size()-1) {
                    update+=" AND ";
                }
            }

            que = "UPDATE %s SET %s WHERE %s".formatted(getTable(), update, where);
        } else if (getType() == Types.SELECT) {
            String where = "", select = "";
            List<String> keys = new ArrayList<>(), values = new ArrayList<>(), selected = new ArrayList<>();

            getWhere().forEach((key, value) -> {
                keys.add(key);
                values.add("'%s'".formatted(value));
            });
            getOption().forEach((f, opt) -> {
                selected.add(opt);
            });

            for (int i=0;i< selected.size();i++) {
                select+=selected.get(i);
                if (i != selected.size()-1) {
                    select+=", ";
                }
            }

            for (int i = 0;i < keys.size();i++) {
                where+="%s=%s".formatted(keys.get(i), values.get(i));

                if (i != keys.size()-1) {
                    where+=" AND ";
                }
            }

            que = "SELECT %s FROM %s WHERE %s ".formatted(select, getTable(), where);
        }

        try {
            PreparedStatement preparedStatement = exeQueImpl(que);
            if (getType() != Types.SELECT) {
                preparedStatement.execute();
            } else {
                ResultSet resultSet = preparedStatement.executeQuery();
                List<String> sel = new ArrayList<>();

                getOption().forEach((l, sell) -> {
                    sel.add(sell);
                });

                while (resultSet.next()) {
                    List<String> list = new ArrayList<>();

                    for (int i=0;i< sel.size();i++) {
                        list.add(resultSet.getString(sel.get(i)));
                    }
                    returned.add(list);
                }
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returned;
    }

    public PreparedStatement exeQueImpl(String que) {
        PreparedStatement preparedStatement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:%s".formatted(getPath()));

            preparedStatement = connection.prepareStatement(que);

            System.out.printf("Execute que: %s\n".formatted(que));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }
}