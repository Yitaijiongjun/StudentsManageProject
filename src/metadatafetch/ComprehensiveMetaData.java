package metadatafetch;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class ComprehensiveMetaData {
    public static void main(String[] args) {
        try {
            // 加载驱动程序
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 连接到数据库
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.188.128:3306/students_manage","root","210569");

            // 获取 DatabaseMetaData 对象
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            // 获取所有表的信息
            ResultSet tables = databaseMetaData.getTables(null, null, "%", new String[] {"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("表名: " + tableName);
            }

            // 获取指定表的主键信息
            ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(null, null, "学生");
            while (primaryKeys.next()) {
                String pkName = primaryKeys.getString("PK_NAME");
                String pkColumnName = primaryKeys.getString("COLUMN_NAME");
                System.out.println("主键名称: " + pkName);
                System.out.println("主键列名: " + pkColumnName);
            }

            // 获取指定表的外键信息
            ResultSet foreignKeys = databaseMetaData.getImportedKeys(null, null, "学生");
            while (foreignKeys.next()) {
                String fkName = foreignKeys.getString("FK_NAME");
                String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
                String pkTableName = foreignKeys.getString("PKTABLE_NAME");
                String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
                System.out.println("外键名称: " + fkName);
                System.out.println("外键列名: " + fkColumnName);
                System.out.println("引用的主表名: " + pkTableName);
                System.out.println("引用的主表列名: " + pkColumnName);
            }
/*
            // 获取指定表的列信息
            ResultSet columns = databaseMetaData.getColumns(null, null, "学生", "%");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                int columnSize = columns.getInt("COLUMN_SIZE");
                System.out.println("列名: " + columnName);
                System.out.println("列类型: " + columnType);
                System.out.println("列大小: " + columnSize);
            }
            // 获取指定表的索引信息
            ResultSet indexes = databaseMetaData.getIndexInfo(null, null, "学生", false, false);
            while (indexes.next()) {
                String indexName = indexes.getString("INDEX_NAME");
                String indexColumnName = indexes.getString("COLUMN_NAME");
                System.out.println("索引名称: " + indexName);
                System.out.println("索引列名: " + indexColumnName);
            }
 */
            // 关闭连接
            tables.close();
            primaryKeys.close();
            foreignKeys.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

