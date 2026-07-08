package com.teachmanage.server.dao;

import com.teachmanage.common.model.Teacher;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeacherDao extends BaseDao {
    public TeacherDao(Connection conn) {
        super(conn);
    }

    public List<Teacher> findAll(String classNo, String keyword) throws SQLException {
        String sql = """
                SELECT t.Tno, t.Tname, t.Tsex, t.Ttitle, t.ClassNo, c.ClassName
                FROM teacher t
                LEFT JOIN `class` c ON c.ClassNo = t.ClassNo
                WHERE 1 = 1
                """;
        List<Object> params = new ArrayList<>();
        if (classNo != null && !classNo.isBlank()) {
            sql += " AND t.ClassNo = ?";
            params.add(classNo);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql += " AND (t.Tno LIKE ? OR t.Tname LIKE ?)";
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
        }
        sql += " ORDER BY t.Tno";
        return queryList(sql, this::map, params.toArray());
    }

    public Teacher findByTno(String tno) throws SQLException {
        return queryOne("""
                SELECT t.Tno, t.Tname, t.Tsex, t.Ttitle, t.ClassNo, c.ClassName
                FROM teacher t
                LEFT JOIN `class` c ON c.ClassNo = t.ClassNo
                WHERE t.Tno = ?
                """, this::map, tno);
    }

    public boolean existsByTno(String tno) throws SQLException {
        return exists("SELECT 1 FROM teacher WHERE Tno = ?", tno);
    }

    public int insert(Teacher t) throws SQLException {
        return update("INSERT INTO teacher (Tno, Tname, Tsex, Ttitle, ClassNo) VALUES (?, ?, ?, ?, ?)",
                t.getTno(), t.getTname(), t.getTsex(), t.getTtitle(), t.getClassNo());
    }

    public int update(Teacher t) throws SQLException {
        return update("UPDATE teacher SET Tname = ?, Tsex = ?, Ttitle = ?, ClassNo = ? WHERE Tno = ?",
                t.getTname(), t.getTsex(), t.getTtitle(), t.getClassNo(), t.getTno());
    }

    public int delete(String tno) throws SQLException {
        return update("DELETE FROM teacher WHERE Tno = ?", tno);
    }

    public int countAll() throws SQLException {
        return countAll("teacher");
    }

    private Teacher map(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setTno(rs.getString("Tno"));
        t.setTname(rs.getString("Tname"));
        t.setTsex(rs.getString("Tsex"));
        t.setTtitle(rs.getString("Ttitle"));
        t.setClassNo(rs.getString("ClassNo"));
        t.setClassName(rs.getString("ClassName"));
        return t;
    }
}
