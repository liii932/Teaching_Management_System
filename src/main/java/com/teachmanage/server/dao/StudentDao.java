package com.teachmanage.server.dao;

import com.teachmanage.common.model.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDao extends BaseDao {
    public StudentDao(Connection conn) {
        super(conn);
    }

    public List<Student> findAll(String classNo, String keyword) throws SQLException {
        String sql = """
                SELECT s.Sno, s.Sname, s.Ssex, s.Sage, s.ClassNo, c.ClassName
                FROM student s
                LEFT JOIN `class` c ON c.ClassNo = s.ClassNo
                WHERE 1 = 1
                """;
        List<Object> params = new ArrayList<>();
        if (classNo != null && !classNo.isBlank()) {
            sql += " AND s.ClassNo = ?";
            params.add(classNo);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql += " AND (s.Sno LIKE ? OR s.Sname LIKE ?)";
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
        }
        sql += " ORDER BY s.Sno";
        return queryList(sql, this::map, params.toArray());
    }

    public Student findBySno(String sno) throws SQLException {
        return queryOne("""
                SELECT s.Sno, s.Sname, s.Ssex, s.Sage, s.ClassNo, c.ClassName
                FROM student s
                LEFT JOIN `class` c ON c.ClassNo = s.ClassNo
                WHERE s.Sno = ?
                """, this::map, sno);
    }

    public boolean existsBySno(String sno) throws SQLException {
        return exists("SELECT 1 FROM student WHERE Sno = ?", sno);
    }

    public int insert(Student s) throws SQLException {
        return update("INSERT INTO student (Sno, Sname, Ssex, Sage, ClassNo) VALUES (?, ?, ?, ?, ?)",
                s.getSno(), s.getSname(), s.getSsex(), s.getSage(), s.getClassNo());
    }

    public int update(Student s) throws SQLException {
        return update("UPDATE student SET Sname = ?, Ssex = ?, Sage = ?, ClassNo = ? WHERE Sno = ?",
                s.getSname(), s.getSsex(), s.getSage(), s.getClassNo(), s.getSno());
    }

    public int delete(String sno) throws SQLException {
        return update("DELETE FROM student WHERE Sno = ?", sno);
    }

    public int countAll() throws SQLException {
        return countAll("student");
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setSno(rs.getString("Sno"));
        s.setSname(rs.getString("Sname"));
        s.setSsex(rs.getString("Ssex"));
        int age = rs.getInt("Sage");
        s.setSage(rs.wasNull() ? null : age);
        s.setClassNo(rs.getString("ClassNo"));
        s.setClassName(rs.getString("ClassName"));
        return s;
    }
}
