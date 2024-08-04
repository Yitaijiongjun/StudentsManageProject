private void updateStudent(String studentId, String name, String age, String gender, String className) {
        try {
            String query = "SELECT * FROM `学生` WHERE `学号` = ?";
            PreparedStatement stmt = StudentManagementSystem.conn.prepareStatement(query);
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                query = "UPDATE `学生` SET `姓名` = ?, `性别` = ?, `班级` = ? , `宿舍` = ? WHERE `学号` = ?";
                stmt = StudentManagementSystem.conn.prepareStatement(query);
                stmt.setString(1, name.isEmpty() ? rs.getString("name") : name);
                stmt.setString(2, age.isEmpty() ? rs.getString("age") : age);
                stmt.setString(3, gender.isEmpty() ? rs.getString("gender") : gender);
                stmt.setString(4, className.isEmpty() ? rs.getString("class") : className);
                stmt.setString(5, studentId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "学生信息已更新!");
            } else {
                query = "INSERT INTO students (student_id, name, age, gender, class) VALUES (?, ?, ?, ?, ?)";
                stmt = StudentManagementSystem.conn.prepareStatement(query);
                stmt.setString(1, studentId);
                stmt.setString(2, name);
                stmt.setString(3, age);
                stmt.setString(4, gender);
				stmt.setString(2, name);
                stmt.setString(3, age);
                stmt.setString(4, gender);
                stmt.setString(5, className);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "学生信息已添加!");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

int MAX_ALLOCA_SIZE = 512;

int mysql_num_fields(MYSQL_RES res) {
    return res.field_count;
}
static boolean column_types_flag;
public void displayResultSet(MYSQL_RES result) throws SQLException {

    StringBuilder separator = new StringBuilder("+");
    MYSQL_ROW cur = new MYSQL_ROW();
    MYSQL_FIELD field = new MYSQL_FIELD();
    List<Boolean> num_flag = new ArrayList<>();
    long sz;

    sz = 1 * mysql_num_fields(result);
    num_flag = new ArrayList<>((int) sz);
    if (column_types_flag) {
        //print_field_types(result);
        //if (!mysql_num_rows(result)) return;
        mysql_field_seek(result, 0);
    }
    //bool copy(const char *s, size_t arg_length, const CHARSET_INFO *cs);
    separator.append("+");
    //Calls mysql_fetch_field api to returns the type of next table field.
    while ((field = mysql_fetch_field(result))) {
        size_t length = column_names ? field->name_length : 0;
        if (quick)
            length = max<size_t>(length, field->length);
    else
        length = max<size_t>(length, field->max_length);
        if (length < 4 && !IS_NOT_NULL(field->flags))
            length = 4;  // Room for "NULL"
        if (opt_binhex && is_binary_field(field)) length = 2 + length * 2;
        field->max_length = (ulong)length;
        separator.fill(separator.length() + length + 2, '-');
        separator.append('+');
    }
    separator.append('\0');  // End marker for \0
    tee_puts(separator.ptr(), PAGER);
    if (column_names) {
        mysql_field_seek(result, 0);
        (void)tee_fputs("|", PAGER);
        for (uint off = 0; (field = mysql_fetch_field(result)); off++) {
      const size_t name_length = strlen(field->name);
      const size_t numcells = charset_info->cset->numcells(
                    charset_info, field->name, field->name + name_length);
      const size_t display_length = field->max_length + name_length - numcells;
            tee_fprintf(PAGER, " %-*s |",
                    min<int>((int)display_length, MAX_COLUMN_LENGTH),
            field->name);
            num_flag[off] = IS_NUM(field->type);
        }
        (void)tee_fputs("\n", PAGER);
        tee_puts(separator.ptr(), PAGER);
    }

    while ((cur = mysql_fetch_row(result))) {
        ulong *lengths = mysql_fetch_lengths(result);
        (void)tee_fputs("| ", PAGER);
        mysql_field_seek(result, 0);
        for (uint off = 0; off < mysql_num_fields(result); off++) {
      const char *buffer;
            uint data_length;
            uint field_max_length;
            size_t visible_length;
            uint extra_padding;

            if (off) (void)tee_fputs(" ", PAGER);

            if (cur[off] == nullptr) {
                buffer = "NULL";
                data_length = 4;
            } else {
                buffer = cur[off];
                data_length = (uint)lengths[off];
            }

            field = mysql_fetch_field(result);
            field_max_length = field->max_length;

      /*
       How many text cells on the screen will this string span?  If it
       contains multibyte characters, then the number of characters we occupy
       on screen will be fewer than the number of bytes we occupy in memory.

       We need to find how much screen real-estate we will occupy to know how
       many extra padding-characters we should send with the printing
       function.
      */
            visible_length = charset_info->cset->numcells(charset_info, buffer,
                    buffer + data_length);
            extra_padding = (uint)(data_length - visible_length);

            if (opt_binhex && is_binary_field(field))
                print_as_hex(PAGER, cur[off], lengths[off], field_max_length);
            else if (field_max_length > MAX_COLUMN_LENGTH)
                tee_print_sized_data(buffer, data_length,
                        MAX_COLUMN_LENGTH + extra_padding, false);
            else {
                if (num_flag[off] != 0) /* if it is numeric, we right-justify it */
                    tee_print_sized_data(buffer, data_length,
                            field_max_length + extra_padding, true);
                else
                    tee_print_sized_data(buffer, data_length,
                            field_max_length + extra_padding, false);
            }
            tee_fputs(" |", PAGER);
        }
        (void)tee_fputs("\n", PAGER);

        // Check interrupted_query last; this ensures that we get at least one
        // row. This is useful for aborted EXPLAIN ANALYZE queries.
        if (interrupted_query) break;
    }
    tee_puts(separator.ptr(), PAGER);
    my_safe_afree((bool *)num_flag, sz, MAX_ALLOCA_SIZE);







    ResultSetMetaData metaData = resultSet.getMetaData();
    int columnCount = metaData.getColumnCount();
    boolean[] numFlag = new boolean[columnCount];
    int[] columnWidths = new int[columnCount];




}
int mysql_field_seek(MYSQL_RES result, int field_offset) {
    int return_value = result.current_field;
    result.current_field = field_offset;
    return return_value;
}
long mysql_num_rows(MYSQL_RES res) { return res.row_count; }
class MYSQL_ROW extends ArrayList<ArrayList<Character>>{}        /* return data as array of strings */

class MYSQL_ROWS {
    MYSQL_ROWS next; /* list of rows */
    MYSQL_ROW data;
    long length;
}

class MYSQL_DATA {
    MYSQL_ROWS data;
    Object alloc;
    int rows;
    int fields;
}

class MYSQL_RES {
    int row_count;
    MYSQL_FIELD fields;
    MYSQL_DATA data;
    MYSQL_ROWS data_cursor;
    List<Long> lengths; /* column lengths of current row */
    Object handle;          /* for unbuffered reads */
    Object methods;
    MYSQL_ROW row;         /* If unbuffered read */
    MYSQL_ROW current_row; /* buffer to current row */
    Object field_alloc;
    int field_count, current_field;
    boolean eof; /* Used by mysql_fetch_row */
    /* mysql_stmt_close() had to cancel this result */
    boolean unbuffered_fetch_cancelled;
    enum_resultset_metadata metadata;
    Object extension;
}
enum enum_resultset_metadata {
    /** No metadata will be sent. */
    RESULTSET_METADATA_NONE(0),
    /** The server will send all metadata. */
    RESULTSET_METADATA_FULL(1);
    int value;
    enum_resultset_metadata(int value) {
        this.value = value;
    }
};

class MYSQL_FIELD {
    List<Character> name;               /* name of column */
    List<Character> org_name;           /* original column name, if an alias */
    List<Character> table;              /* Table of column if column was a field */
    List<Character> org_table;          /* Org table name, if table was an alias */
    List<Character> db;                 /* Database for table */
    List<Character> catalog;            /* Catalog for table */
    List<Character> def;                /* Default value (set by mysql_list_fields) */
    long length;                        /* Width of column (create length) */
    long max_length;                    /* Max width for selected set */
    int name_length;
    int org_name_length;
    int table_length;
    int org_table_length;
    int db_length;
    int catalog_length;
    int def_length;
    int flags;                          /* Div flags */
    int decimals;                       /* Number of decimals in field */
    int charsetnr;                      /* Character set */
    enum_field_types type;         /* Type of field. See mysql_com.h for types */
    Object extension;
}

enum enum_field_types {
    MYSQL_TYPE_DECIMAL,
    MYSQL_TYPE_TINY,
    MYSQL_TYPE_SHORT,
    MYSQL_TYPE_LONG,
    MYSQL_TYPE_FLOAT,
    MYSQL_TYPE_DOUBLE,
    MYSQL_TYPE_NULL,
    MYSQL_TYPE_TIMESTAMP,
    MYSQL_TYPE_LONGLONG,
    MYSQL_TYPE_INT24,
    MYSQL_TYPE_DATE,
    MYSQL_TYPE_TIME,
    MYSQL_TYPE_DATETIME,
    MYSQL_TYPE_YEAR,
    MYSQL_TYPE_NEWDATE, /**< Internal to MySQL. Not used in protocol */
    MYSQL_TYPE_VARCHAR,
    MYSQL_TYPE_BIT,
    MYSQL_TYPE_TIMESTAMP2,
    MYSQL_TYPE_DATETIME2,   /**< Internal to MySQL. Not used in protocol */
    MYSQL_TYPE_TIME2,       /**< Internal to MySQL. Not used in protocol */
    MYSQL_TYPE_TYPED_ARRAY, /**< Used for replication only */
    MYSQL_TYPE_VECTOR(242),
    MYSQL_TYPE_INVALID(243),
    MYSQL_TYPE_BOOL(244), /**< Currently just a placeholder */
    MYSQL_TYPE_JSON(245),
    MYSQL_TYPE_NEWDECIMAL(246),
    MYSQL_TYPE_ENUM(247),
    MYSQL_TYPE_SET(248),
    MYSQL_TYPE_TINY_BLOB(249),
    MYSQL_TYPE_MEDIUM_BLOB(250),
    MYSQL_TYPE_LONG_BLOB(251),
    MYSQL_TYPE_BLOB(252),
    MYSQL_TYPE_VAR_STRING(253),
    MYSQL_TYPE_STRING(254),
    MYSQL_TYPE_GEOMETRY(255);

    private final Integer value;
    private final int defaultValue;
    // 有参数的构造函数
    enum_field_types(Integer value) {
        this.value = value;
        this.defaultValue = -1;  // 默认值，不会被使用
    }
    // 无参数的构造函数
    enum_field_types() {
        this.value = null;
        this.defaultValue = ordinal();  // 使用枚举常量的序号作为默认值
    }
    public Integer getValue() {
        return value != null ? value : defaultValue;
    }
}