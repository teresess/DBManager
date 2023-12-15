package upd.dev.dbmanager;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import upd.dev.dbmanager.parametrs.Option;
import upd.dev.dbmanager.parametrs.Where;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBManagerBuilder {

    public Connection connection;
    private DBManagerBuilder.Option option;
    private DBManagerBuilder.Path path;
    private DBManagerBuilder.Table table;
    private DBManagerBuilder.Where where;
    private DBManagerBuilder.Type type;
    public DBManagerBuilder() {
        setAllNull();
    }
    private List<upd.dev.dbmanager.parametrs.Option> getOption() {
        return option.getOption();
    }

    private String getPath() {
        return path.getPath();
    }

    private String getTable() {
        return table.getTable();
    }

    private List<upd.dev.dbmanager.parametrs.Where> getWhere() {
        return where.getWhere();
    }
    private Types getType() {
        return type.getTypes();
    }
    public DBManagerBuilder setType(Types type) {
        this.type = new Type(type);

        return this;
    }
    private void setAllNull() {
        setWhere(upd.dev.dbmanager.parametrs.Where.add(null, null));
        setOption(upd.dev.dbmanager.parametrs.Option.add(null, null));
        setType(null);
        setPath(null);
        setTable(null);
    }
    public DBManagerBuilder setTable(String table) {
        this.table = new Table(table);

        return this;
    }
    public DBManagerBuilder setWhere(upd.dev.dbmanager.parametrs.Where... where) {
        this.where = new Where(where);

        return this;
    }
    public DBManagerBuilder setOption(upd.dev.dbmanager.parametrs.Option... option) {
        this.option = new Option(option);

        return this;
    }
    public DBManagerBuilder setPath(String path) {
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
        private List<upd.dev.dbmanager.parametrs.Option> option;
        public Option(upd.dev.dbmanager.parametrs.Option... option) {
            List<upd.dev.dbmanager.parametrs.Option> options = new ArrayList<>(Arrays.asList(option));
            this.option = options;
        }
        public List<upd.dev.dbmanager.parametrs.Option> getOption() {
            return option;
        }
    }
    public static class Where {
        private List<upd.dev.dbmanager.parametrs.Where> where;
        public List<upd.dev.dbmanager.parametrs.Where> getWhere() {
            return where;
        }

        public Where(upd.dev.dbmanager.parametrs.Where... where) {

            List<upd.dev.dbmanager.parametrs.Where> wheres = new ArrayList<>(Arrays.asList(where));
            this.where = wheres;
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

            getWhere().forEach((whereOption) -> {
                keys.add(whereOption.getKey());
                values.add("'%s'".formatted(whereOption.getValue()));
            });

            for (int i = 0;i < keys.size();i++) {
                where+="%s=%s".formatted(keys.get(i), values.get(i));

                if (i != keys.size()-1) {
                    where+=" AND ";
                }
            }

            String que = "SELECT COUNT(*) FROM %s WHERE %s".formatted(getTable(), where);
            PreparedStatement preparedStatement = createStatement(que);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    ret = (count > 0);
                }
                connection.close();
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

            getOption().forEach((options) -> {
                keys.add(options.getKey());
                values.add("'%s'".formatted(options.getValue()));
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

            getWhere().forEach((whereOption) -> {
                keys.add(whereOption.getKey());
                values.add("'%s'".formatted(whereOption.getValue()));
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

            getOption().forEach((options) -> {
                update_col.add(options.getKey());
                new_update_col.add("'%s'".formatted(options.getValue()));
            });

            getWhere().forEach((whereOption) -> {
                where_key.add(whereOption.getKey());
                where_value.add("'%s'".formatted(whereOption.getValue()));
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

            getWhere().forEach((whereOption) -> {
                keys.add(whereOption.getKey());
                values.add("'%s'".formatted(whereOption.getValue()));
            });
            getOption().forEach((options) -> selected.add(options.getValue()));

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
            PreparedStatement preparedStatement = createStatement(que);
            if (getType() != Types.SELECT) {
                preparedStatement.execute();
            } else {
                ResultSet resultSet = preparedStatement.executeQuery();
                List<String> sel = new ArrayList<>();

                getOption().forEach((options) -> sel.add(options.getValue()));

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

    public PreparedStatement createStatement(String que) {
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