package com.teachmanage.server.dao;

import com.teachmanage.common.model.Clazz;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClassDao extends BaseDao {
    public ClassDao(Connection conn) {
        super(conn);
    }

    public List<Clazz> findAll(String majorNo) throws SQLException {
        String sql = """
                SELECT c.ClassNo, c.ClassName, c.MajorNo, m.MajorName, d.DeptNo, d.DeptName
                FROM `class` c
                LEFT JOIN major m ON m.MajorNo = c.MajorNo
                LEFT JOIN department d ON d.DeptNo = m.DeptNo
                """;
        List<Object> params = new ArrayList<>();
        if (majorNo != null && !majorNo.isBlank()) {
            sql += " WHERE c.MajorNo = ?";
            params.add(majorNo);
        }
        sql += " ORDER BY c.ClassNo";
        return queryList(sql, this::map, params.toArray());
    }

    public Clazz findByClassNo(String classNo) throws SQLException {
        return queryOne("""
                SELECT c.ClassNo, c.ClassName, c.MajorNo, m.MajorName, d.DeptNo, d.DeptName
                FROM `class` c
                LEFT JOIN major m ON m.MajorNo = c.MajorNo
                LEFT JOIN department d ON d.DeptNo = m.DeptNo
                WHERE c.ClassNo = ?
                """, this::map, classNo);
    }

    public boolean existsByClassNo(String classNo) throws SQLException {
        return exists("SELECT 1 FROM `class` WHERE ClassNo = ?", classNo);
    }

    public int insert(Clazz c) throws SQLException {
        return update("INSERT INTO `class` (ClassNo, ClassName, MajorNo) VALUES (?, ?, ?)",
                c.getClassNo(), c.getClassName(), c.getMajorNo());
    }

    public int update(Clazz c) throws SQLException {
        return update("UPDATE `class` SET ClassName = ?, MajorNo = ? WHERE ClassNo = ?",
                c.getClassName(), c.getMajorNo(), c.getClassNo());
    }

    public int delete(String classNo) throws SQLException {
        return update("DELETE FROM `class` WHERE ClassNo = ?", classNo);
    }

    public int countAll() throws SQLException {
        return countAll("`class`");
    }

    private Clazz map(ResultSet rs) throws SQLException {
        Clazz c = new Clazz();
        c.setClassNo(rs.getString("ClassNo"));
        c.setClassName(rs.getString("ClassName"));
        c.setMajorNo(rs.getString("MajorNo"));
        c.setMajorName(rs.getString("MajorName"));
        c.setDeptNo(rs.getString("DeptNo"));
        c.setDeptName(rs.getString("DeptName"));
        return c;
    }
}
