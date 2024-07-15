package updatequerybuilder;

import java.util.List;
import java.util.StringJoiner;


public class UpdateQueryBuilder {

    public static String buildUpdateQuery(String tableName, List<List<String>> primaryKeys) {
        for (List<String> keyList : primaryKeys) {

            StringJoiner keyClause = new StringJoiner(" AND ");
            for (String key : keyList) {
                keyClause.add(key + " = ?");
            }
        }
        String updateQuery = "UPDATE " + tableName + " SET  WHERE ";
        return updateQuery;
    }
}

