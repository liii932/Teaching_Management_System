package com.teachmanage.server.dao;

import com.teachmanage.common.model.Course;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDao extends BaseDao {
    public CourseDao(Connection conn) {
        super(conn);
    }

    public List<Course> findAll(String teacherNo, String keyword) throws SQLException {
        String sql = """
                SELECT c.Cno, c.Cname, c.Credit, c.Tno, t.Tname
                FROM course c
                LEFT JOIN teacher t ON t.Tno = c.Tno
                WHERE 1 = 1
                """;
        List<Object> params = new ArrayList<>();
        if (teacherNo != null && !teacherNo.isBlank()) {
            sql += " AND c.Tno = ?";
            params.add(teacherNo);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql += " AND (c.Cno LIKE ? OR c.Cname LIKE ?)";
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
        }
        sql += " ORDER BY c.Cno";
        return queryList(sql, this::map, params.toArray());
    }

    public Course findByCno(String cno) throws SQLException {
        return queryOne("""
                SELECT c.Cno, c.Cname, c.Credit, c.Tno, t.Tname
                FROM course c
                LEFT JOIN teacher t ON t.Tno = c.Tno
                WHERE c.Cno = ?
                """, this::map, cno);
    }

    public boolean existsByCno(String cno) throws SQLException {
        return exists("SELECT 1 FROM course WHERE Cno = ?", cno);
    }

    public int insert(Course c) throws SQLException {
        return update("INSERT INTO course (Cno, Cname, Credit, Tno) VALUES (?, ?, ?, ?)",
                c.getCno(), c.getCname(), c.getCredit(), c.getTno());
    }

    public int update(Course c) throws SQLException {
        return update("UPDATE course SET Cname = ?, Credit = ?, Tno = ? WHERE Cno = ?",
                c.getCname(), c.getCredit(), c.getTno(), c.getCno());
    }

    public int delete(String cno) throws SQLException {
        return update("DELETE FROM course WHERE Cno = ?", cno);
    }

    public int countAll() throws SQLException {
        return countAll("course");
    }

    private Course map(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCno(rs.getString("Cno"));
        c.setCname(rs.getString("Cname"));
        c.setCredit(rs.getBigDecimal("Credit"));
        c.setTno(rs.getString("Tno"));
        c.setTname(rs.getString("Tname"));
        return c;
    }
}
