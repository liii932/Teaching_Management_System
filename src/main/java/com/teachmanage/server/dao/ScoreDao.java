package com.teachmanage.server.dao;

import com.teachmanage.common.model.Score;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreDao extends BaseDao {
    public ScoreDao(Connection conn) {
        super(conn);
    }

    public List<Score> findAll(String sno, String cno) throws SQLException {
        String sql = """
                SELECT sc.Sno, s.Sname, sc.Cno, c.Cname, c.Credit, sc.Grade
                FROM sc
                JOIN student s ON s.Sno = sc.Sno
                JOIN course c ON c.Cno = sc.Cno
                WHERE 1 = 1
                """;
        List<Object> params = new ArrayList<>();
        if (sno != null && !sno.isBlank()) {
            sql += " AND sc.Sno = ?";
            params.add(sno);
        }
        if (cno != null && !cno.isBlank()) {
            sql += " AND sc.Cno = ?";
            params.add(cno);
        }
        sql += " ORDER BY sc.Sno, sc.Cno";
        return queryList(sql, this::map, params.toArray());
    }

    public Score findByKey(String sno, String cno) throws SQLException {
        return queryOne("""
                SELECT sc.Sno, s.Sname, sc.Cno, c.Cname, c.Credit, sc.Grade
                FROM sc
                JOIN student s ON s.Sno = sc.Sno
                JOIN course c ON c.Cno = sc.Cno
                WHERE sc.Sno = ? AND sc.Cno = ?
                """, this::map, sno, cno);
    }

    public boolean existsByKey(String sno, String cno) throws SQLException {
        return exists("SELECT 1 FROM sc WHERE Sno = ? AND Cno = ?", sno, cno);
    }

    public int insert(Score s) throws SQLException {
        return update("INSERT INTO sc (Sno, Cno, Grade) VALUES (?, ?, ?)", s.getSno(), s.getCno(), s.getGrade());
    }

    public int update(Score s) throws SQLException {
        return update("UPDATE sc SET Grade = ? WHERE Sno = ? AND Cno = ?", s.getGrade(), s.getSno(), s.getCno());
    }

    public int delete(String sno, String cno) throws SQLException {
        return update("DELETE FROM sc WHERE Sno = ? AND Cno = ?", sno, cno);
    }

    public int countAll() throws SQLException {
        return countAll("sc");
    }

    public List<Map<String, Object>> studentStatistics(String sno) throws SQLException {
        return queryMaps("""
                SELECT sc.Sno AS sno, s.Sname AS sname, sc.Cno AS cno, c.Cname AS cname,
                       c.Credit AS credit, sc.Grade AS grade
                FROM sc
                JOIN student s ON s.Sno = sc.Sno
                JOIN course c ON c.Cno = sc.Cno
                WHERE sc.Sno = ?
                ORDER BY sc.Cno
                """, sno);
    }

    public Map<String, Object> studentSummary(String sno) throws SQLException {
        List<Map<String, Object>> rows = queryMaps("""
                SELECT s.Sno AS sno, s.Sname AS sname, COUNT(sc.Cno) AS courseCount,
                       COALESCE(SUM(c.Credit), 0) AS totalCredit, AVG(sc.Grade) AS avgGrade
                FROM student s
                LEFT JOIN sc ON sc.Sno = s.Sno
                LEFT JOIN course c ON c.Cno = sc.Cno
                WHERE s.Sno = ?
                GROUP BY s.Sno, s.Sname
                """, sno);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public Map<String, Object> courseSummary(String cno) throws SQLException {
        List<Map<String, Object>> rows = queryMaps("""
                SELECT c.Cno AS cno, c.Cname AS cname, COUNT(sc.Sno) AS studentCount,
                       MAX(sc.Grade) AS maxGrade, MIN(sc.Grade) AS minGrade, AVG(sc.Grade) AS avgGrade
                FROM course c
                LEFT JOIN sc ON sc.Cno = c.Cno
                WHERE c.Cno = ?
                GROUP BY c.Cno, c.Cname
                """, cno);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<Map<String, Object>> classSummary(String classNo) throws SQLException {
        return queryMaps("""
                SELECT cl.ClassNo AS classNo, cl.ClassName AS className, c.Cno AS cno, c.Cname AS cname,
                       COUNT(sc.Sno) AS studentCount, AVG(sc.Grade) AS avgGrade
                FROM `class` cl
                JOIN student s ON s.ClassNo = cl.ClassNo
                JOIN sc ON sc.Sno = s.Sno
                JOIN course c ON c.Cno = sc.Cno
                WHERE cl.ClassNo = ?
                GROUP BY cl.ClassNo, cl.ClassName, c.Cno, c.Cname
                ORDER BY c.Cno
                """, classNo);
    }

    private Score map(ResultSet rs) throws SQLException {
        Score s = new Score();
        s.setSno(rs.getString("Sno"));
        s.setSname(rs.getString("Sname"));
        s.setCno(rs.getString("Cno"));
        s.setCname(rs.getString("Cname"));
        s.setCredit(rs.getBigDecimal("Credit"));
        s.setGrade(rs.getBigDecimal("Grade"));
        return s;
    }
}
