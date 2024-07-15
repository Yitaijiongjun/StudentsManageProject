package metadatafetch;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static studentsmanageproject.StudentManagementSystem.conn;
import static studentsmanageproject.StudentManagementSystem.frame;

public class FetchData {
    static public void fetchData(List<String> columnNames, List<Class<?>> columnTypes, List<List<Object>> data, String tableName) {
        try {
            String query = "SELECT * FROM `"+ tableName +"`";
            Statement stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(query)) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(metaData.getColumnName(i));
                    columnTypes.add(Class.forName(metaData.getColumnClassName(i)));
                }
                while (rs.next()) {
                    List<Object> row = new Vector<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getObject(i));
                    }
                    data.add(row);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            /*
            1.数据库连接问题：如果数据库连接 conn 在执行查询时由于网络问题、数据库服务器不可用或连
            接超时等原因失败，则 executeQuery(query) 方法可能会抛出 SQLException。
            2.SQL 语法错误：如果 SQL 查询 query 本身存在语法错误，或者引用了不存在的
            表、列等，数据库在执行时会失败，并通过 JDBC 驱动抛出 SQLException。
            3.资源访问问题：如果数据库用户没有足够的权限访问查询中涉及的表或列，也可能会抛出 SQLException。
             */
            JOptionPane.showMessageDialog(frame, "数据库错误: " + e.getMessage());
        }
    }
    static public List<List<String>> fetchDatabaseInformation() {
        List<List<String>> tables = new ArrayList<>();
        try {
            // 获取 DatabaseMetaData 对象
            DatabaseMetaData databaseMetaData = conn.getMetaData();
/*
TABLE_CAT String => table catalog (may be null)
TABLE_SCHEM String => table schema (may be null)
TABLE_NAME String => table name
TABLE_TYPE String => table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
REMARKS String => explanatory comment on the table
TYPE_CAT String => the types catalog (may be null)
TYPE_SCHEM String => the types schema (may be null)
TYPE_NAME String => type name (may be null)
SELF_REFERENCING_COL_NAME String => name of the designated "identifier" column of a typed table (may be null)
REF_GENERATION String => specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER", "DERIVED". (may be null)
*/
            // 获取所有表的信息
            try (ResultSet tablesSet = databaseMetaData.getTables(null, null, "%", new String[]{"TABLE", "VIEW"})) {
                while (tablesSet.next()) {
                    List<String> table = new ArrayList<>();
                    String tableName = tablesSet.getString("TABLE_NAME");
                    String tableType = tablesSet.getString("TABLE_TYPE");
                    // 判断表名是否包含中文字符
                    if (containsChinese(tableName)) {
                        table.add(tableName);
                        table.add(tableType);
                        tables.add(table);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "数据库错误: " + e.getMessage());
        }
        return tables;
    }

    static public void fetchTableInformation(List<List<String>> primaryKeys, List<List<String>> foreignKeys, String tableName) {
        try {
            // 获取 DatabaseMetaData 对象
            DatabaseMetaData databaseMetaData = conn.getMetaData();
/*
TABLE_CAT String => table catalog (may be null)
TABLE_SCHEM String => table schema (may be null)
TABLE_NAME String => table name
COLUMN_NAME String => column name
KEY_SEQ short => sequence number within primary key( a value of 1 represents the first column of the primary key, a value of 2 would represent the second column within the primary key).
PK_NAME String => primary key name (may be null)
*/
            // 获取指定表的主键信息
            try (ResultSet primaryKeysSet = databaseMetaData.getPrimaryKeys(null, null, tableName)) {
                while (primaryKeysSet.next()) {
                    List<String> pk = new ArrayList<>();
                    String pkColumnName = primaryKeysSet.getString("COLUMN_NAME"); //主键列名
                    String pkName = primaryKeysSet.getString("PK_NAME"); //主键名
                    String pkSequence = primaryKeysSet.getString("KEY_SEQ"); //主键序号
                    pk.add(pkColumnName);
                    pk.add(pkName);
                    pk.add(pkSequence);
                    primaryKeys.add(pk);
                }
            }
/*
Retrieves a description of the primary key columns that are referenced by the given table's foreign key columns (the primary keys imported by a table). They are ordered by PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME, and KEY_SEQ.
Each primary key column description has the following columns:
PKTABLE_CAT String => primary key table catalog being imported (may be null)
PKTABLE_SCHEM String => primary key table schema being imported (may be null)
PKTABLE_NAME String => primary key table name being imported
PKCOLUMN_NAME String => primary key column name being imported
FKTABLE_CAT String => foreign key table catalog (may be null)
FKTABLE_SCHEM String => foreign key table schema (may be null)
FKTABLE_NAME String => foreign key table name
FKCOLUMN_NAME String => foreign key column name
KEY_SEQ short => sequence number within a foreign key( a value of 1 represents the first column of the foreign key, a value of 2 would represent the second column within the foreign key).
UPDATE_RULE short => What happens to a foreign key when the primary key is updated:
 */
            // 获取指定表的外键信息
            try (ResultSet foreignKeysSet = databaseMetaData.getImportedKeys(null, null, tableName)) {
                while (foreignKeysSet.next()) {
                    List<String> fk = new ArrayList<>();
                    String fkColumnName = foreignKeysSet.getString("FKCOLUMN_NAME"); // 外键列名
                    String fkName = foreignKeysSet.getString("FK_NAME"); // 外键名称
                    String pkTableName = foreignKeysSet.getString("PKTABLE_NAME"); // 引用表名
                    String pkColumnName = foreignKeysSet.getString("PKCOLUMN_NAME"); // 引用表的主码列名
                    fk.add(fkColumnName);
                    fk.add(fkName);
                    fk.add(pkTableName);
                    fk.add(pkColumnName);
                }
            }
        } catch (SQLException e) {
            /*
            1.数据库连接问题：如果数据库连接 conn 在执行查询时由于网络问题、数据库服务器不可用或连
            接超时等原因失败，则 executeQuery(query) 方法可能会抛出 SQLException。
            2.SQL 语法错误：如果 SQL 查询 query 本身存在语法错误，或者引用了不存在的
            表、列等，数据库在执行时会失败，并通过 JDBC 驱动抛出 SQLException。
            3.资源访问问题：如果数据库用户没有足够的权限访问查询中涉及的表或列，也可能会抛出 SQLException。
             */
            JOptionPane.showMessageDialog(frame, "数据库错误: " + e.getMessage());
        }
    }
    // 判断字符串是否包含中文字符的辅助方法
    private static boolean containsChinese(String str) {
        for (char c : str.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FA5) {
                return true;
            }
        }
        return false;
    }
}

