package com.teachmanage.server.dao;

import com.teachmanage.common.model.Major;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MajorDao extends BaseDao {
    public MajorDao(Connection conn) {
        super(conn);
    }

    public List<Major> findAll(String deptNo) throws SQLException {
        String sql = """
                SELECT m.MajorNo, m.MajorName, m.DeptNo, d.DeptName
                FROM major m
                LEFT JOIN department d ON d.DeptNo = m.DeptNo
                """;
        List<Object> params = new ArrayList<>();
        if (deptNo != null && !deptNo.isBlank()) {
            sql += " WHERE m.DeptNo = ?";
            params.add(deptNo);
        }
        sql += " ORDER BY m.MajorNo";
        return queryList(sql, this::map, params.toArray());
    }

    public Major findByMajorNo(String majorNo) throws SQLException {
        return queryOne("""
                SELECT m.MajorNo, m.MajorName, m.DeptNo, d.DeptName
                FROM major m
                LEFT JOIN department d ON d.DeptNo = m.DeptNo
                WHERE m.MajorNo = ?
                """, this::map, majorNo);
    }

    public boolean existsByMajorNo(String majorNo) throws SQLException {
        return exists("SELECT 1 FROM major WHERE MajorNo = ?", majorNo);
    }

    public int insert(Major m) throws SQLException {
        return update("INSERT INTO major (MajorNo, MajorName, DeptNo) VALUES (?, ?, ?)",
                m.getMajorNo(), m.getMajorName(), m.getDeptNo());
    }

    public int update(Major m) throws SQLException {
        return update("UPDATE major SET MajorName = ?, DeptNo = ? WHERE MajorNo = ?",
                m.getMajorName(), m.getDeptNo(), m.getMajorNo());
    }

    public int delete(String majorNo) throws SQLException {
        return update("DELETE FROM major WHERE MajorNo = ?", majorNo);
    }

    public int countAll() throws SQLException {
        return countAll("major");
    }

    private Major map(ResultSet rs) throws SQLException {
        Major m = new Major();
        m.setMajorNo(rs.getString("MajorNo"));
        m.setMajorName(rs.getString("MajorName"));
        m.setDeptNo(rs.getString("DeptNo"));
        m.setDeptName(rs.getString("DeptName"));
        return m;
    }
}
