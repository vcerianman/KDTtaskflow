package com.kdt.taskflow.mapper.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@MappedJdbcTypes({ JdbcType.ARRAY, JdbcType.OTHER })
@MappedTypes(List.class)
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
            throws SQLException {
        Connection conn = ps.getConnection();
        Array array = conn.createArrayOf("text", parameter.toArray(new String[0]));
        ps.setArray(i, array);
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return extractList(rs.getObject(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return extractList(rs.getObject(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return extractList(cs.getObject(columnIndex));
    }

    private List<String> extractList(Object val) throws SQLException {
        if (val == null) {
            return new ArrayList<>();
        }
        if (val instanceof Array array) {
            Object inner = array.getArray();
            if (inner instanceof Object[] objArray) {
                List<String> list = new ArrayList<>(objArray.length);
                for (Object obj : objArray) {
                    list.add(obj != null ? obj.toString() : null);
                }
                return list;
            } else if (inner instanceof String[] strArray) {
                return new ArrayList<>(Arrays.asList(strArray));
            }
        }
        if (val instanceof String[] strArray) {
            return new ArrayList<>(Arrays.asList(strArray));
        }
        if (val instanceof Object[] objArray) {
            List<String> list = new ArrayList<>(objArray.length);
            for (Object obj : objArray) {
                list.add(obj != null ? obj.toString() : null);
            }
            return list;
        }
        if (val instanceof String str) {
            String text = str.trim();
            if (text.startsWith("{") && text.endsWith("}")) {
                String inner = text.substring(1, text.length() - 1).trim();
                if (!inner.isEmpty()) {
                    String[] parts = inner.split(",");
                    List<String> list = new ArrayList<>(parts.length);
                    for (String part : parts) {
                        String cleaned = part.trim();
                        if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() >= 2) {
                            cleaned = cleaned.substring(1, cleaned.length() - 1);
                        }
                        list.add(cleaned);
                    }
                    return list;
                }
            }
            return List.of(text);
        }
        return new ArrayList<>();
    }
}
