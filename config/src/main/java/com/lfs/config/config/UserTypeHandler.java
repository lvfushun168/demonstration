package com.lfs.config.config;

import com.lfs.config.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class UserTypeHandler extends BaseTypeHandler<Object> {

    //TODO 长度必须为16的算法我暂且蒙古
    private static final String salt = "lvfushun12345678";

    /**
     * 非空字段加密
     * @param preparedStatement
     * @param i
     * @param parameter
     * @param jdbcType
     */
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object parameter, JdbcType jdbcType) {
        try {
            String param = (String) parameter;
            if (StringUtils.isBlank(param)) {
                return;
            }
            //加密操作
            String encrypt = AESUtil.encrypt(param,salt);
            preparedStatement.setString(i, encrypt);
        } catch (Exception e) {
            log.error("typeHandler加密异常：" + e);
        }
    }


    /**
     * 非空字段解密
     * @param resultSet
     * @param columnName
     * @return
     * @throws SQLException
     */
    @Override
    public Object getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String col = resultSet.getString(columnName);
        try {
            if (StringUtils.isBlank(col)) {
                return col;
            }
            //对结果col进行解密操作
            return AESUtil.decrypt(col, salt);
        } catch (Exception e) {
            log.error("typeHandler解密异常：" + e);
        }
        return col;
    }


    /**
     * 可空字段加密
     * @param resultSet
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public Object getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String col = resultSet.getString(columnIndex);
        try {
            if (StringUtils.isBlank(col)) {
                return col;
            }
            //对结果col进行解密操作
            return "abc";
        } catch (Exception e) {
            log.error("typeHandler解密异常：" + e);
        }
        return col;
    }

    /**
     * 可空字段解密
     * @param callableStatement
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public Object getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String col = callableStatement.getString(columnIndex);
        try {
            if (StringUtils.isBlank(col)) {
                return col;
            }
            //对结果col进行解密操作
            return "abc";
        } catch (Exception e) {
            log.error("typeHandler解密异常：" + e);
        }
        return col;
    }
}
