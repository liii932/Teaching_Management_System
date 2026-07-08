package com.teachmanage.server.dao;

import com.teachmanage.common.model.Department;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DepartmentDao extends BaseDao {
    public DepartmentDao(Connection conn) {
        super(conn);
    }

    public List<Department> findAll() throws SQLException {
        return queryList("SELECT DeptNo, DeptName FROM department ORDER BY DeptNo", this::map);
    }

    public Department findByDeptNo(String deptNo) throws SQLException {
        return queryOne("SELECT DeptNo, DeptName FROM department WHERE DeptNo = ?", this::map, deptNo);
    }

    public boolean existsByDeptNo(String deptNo) throws SQLException {
        return exists("SELECT 1 FROM department WHERE DeptNo = ?", deptNo);
    }

    public int insert(Department d) throws SQLException {
        return update("INSERT INTO department (DeptNo, DeptName) VALUES (?, ?)", d.getDeptNo(), d.getDeptName());
    }

    public int update(Department d) throws SQLException {
        return update("UPDATE department SET DeptName = ? WHERE DeptNo = ?", d.getDeptName(), d.getDeptNo());
    }

    public int delete(String deptNo) throws SQLException {
        return update("DELETE FROM department WHERE DeptNo = ?", deptNo);
    }

    public int countAll() throws SQLException {
        return countAll("department");
    }

    private Department map(ResultSet rs) throws SQLException {
        return new Department(rs.getString("DeptNo"), rs.getString("DeptName"));
    }
}
