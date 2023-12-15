# DBManager

Пример использования:

Type **SELECT** (`return List<List<String>>`):

        List<List<String>> list = DBManager.createConnection().setPath("./rebuke.db").setType(Types.SELECT).setTable("rebuke")
                .setOption(
                        Option.add("user_id"),
                        Option.add("", "by_user_id")
                )
                .setWhere(
                        Where.add("rebuke_num", "3")
                ).exeQue();

Доступные методы:

**exeQue(`List<List<String>>`)** - выполняет созданный запрос.\
**checkOnReg(`boolean`)** - выполняет запрос и проверяет существует ли запись с заданными параметрами.

Достуаные типы:

**CREATE, DELETE, UPDATE, SELECT, CHECK**



 
