package com.teachmanage.server.service;

import com.teachmanage.common.exception.BusinessException;
import com.teachmanage.common.model.*;
import com.teachmanage.common.util.Validators;
import com.teachmanage.server.dao.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeachingService {
    private final DepartmentDao departmentDao;
    private final MajorDao majorDao;
    private final ClassDao classDao;
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;
    private final CourseDao courseDao;
    private final ScoreDao scoreDao;

    public TeachingService(DepartmentDao departmentDao, MajorDao majorDao, ClassDao classDao,
                           StudentDao studentDao, TeacherDao teacherDao, CourseDao courseDao,
                           ScoreDao scoreDao) {
        this.departmentDao = departmentDao;
        this.majorDao = majorDao;
        this.classDao = classDao;
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
        this.courseDao = courseDao;
        this.scoreDao = scoreDao;
    }

    public Map<String, Object> health() throws SQLException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("database", "ok");
        data.put("departments", departmentDao.countAll());
        data.put("students", studentDao.countAll());
        return data;
    }

    public List<Department> departments() throws SQLException {
        return departmentDao.findAll();
    }

    public Department department(String deptNo) throws SQLException {
        Department d = departmentDao.findByDeptNo(deptNo);
        if (d == null) {
            throw BusinessException.notFound("系不存在");
        }
        return d;
    }

    public Department createDepartment(Department d) throws SQLException {
        validateDepartment(d);
        if (departmentDao.existsByDeptNo(d.getDeptNo())) {
            throw BusinessException.conflict("系编号已存在");
        }
        departmentDao.insert(d);
        return department(d.getDeptNo());
    }

    public Department updateDepartment(String deptNo, Department d) throws SQLException {
        d.setDeptNo(deptNo);
        validateDepartment(d);
        ensureDepartment(deptNo);
        departmentDao.update(d);
        return department(deptNo);
    }

    public void deleteDepartment(String deptNo) throws SQLException {
        ensureDepartment(deptNo);
        departmentDao.delete(deptNo);
    }

    public List<Major> majors(String deptNo) throws SQLException {
        return majorDao.findAll(deptNo);
    }

    public Major major(String majorNo) throws SQLException {
        Major m = majorDao.findByMajorNo(majorNo);
        if (m == null) {
            throw BusinessException.notFound("专业不存在");
        }
        return m;
    }

    public Major createMajor(Major m) throws SQLException {
        validateMajor(m);
        ensureDepartment(m.getDeptNo());
        if (majorDao.existsByMajorNo(m.getMajorNo())) {
            throw BusinessException.conflict("专业编号已存在");
        }
        majorDao.insert(m);
        return major(m.getMajorNo());
    }

    public Major updateMajor(String majorNo, Major m) throws SQLException {
        m.setMajorNo(majorNo);
        validateMajor(m);
        ensureMajor(majorNo);
        ensureDepartment(m.getDeptNo());
        majorDao.update(m);
        return major(majorNo);
    }

    public void deleteMajor(String majorNo) throws SQLException {
        ensureMajor(majorNo);
        majorDao.delete(majorNo);
    }

    public List<Clazz> classes(String majorNo) throws SQLException {
        return classDao.findAll(majorNo);
    }

    public Clazz clazz(String classNo) throws SQLException {
        Clazz c = classDao.findByClassNo(classNo);
        if (c == null) {
            throw BusinessException.notFound("班级不存在");
        }
        return c;
    }

    public Clazz createClass(Clazz c) throws SQLException {
        validateClass(c);
        ensureMajor(c.getMajorNo());
        if (classDao.existsByClassNo(c.getClassNo())) {
            throw BusinessException.conflict("班级号已存在");
        }
        classDao.insert(c);
        return clazz(c.getClassNo());
    }

    public Clazz updateClass(String classNo, Clazz c) throws SQLException {
        c.setClassNo(classNo);
        validateClass(c);
        ensureClass(classNo);
        ensureMajor(c.getMajorNo());
        classDao.update(c);
        return clazz(classNo);
    }

    public void deleteClass(String classNo) throws SQLException {
        ensureClass(classNo);
        classDao.delete(classNo);
    }

    public List<Student> students(String classNo, String keyword) throws SQLException {
        return studentDao.findAll(classNo, keyword);
    }

    public Student student(String sno) throws SQLException {
        Student s = studentDao.findBySno(sno);
        if (s == null) {
            throw BusinessException.notFound("学生不存在");
        }
        return s;
    }

    public Student createStudent(Student s) throws SQLException {
        validateStudent(s);
        ensureOptionalClass(s.getClassNo());
        if (studentDao.existsBySno(s.getSno())) {
            throw BusinessException.conflict("学号已存在");
        }
        studentDao.insert(s);
        return student(s.getSno());
    }

    public Student updateStudent(String sno, Student s) throws SQLException {
        s.setSno(sno);
        validateStudent(s);
        ensureStudent(sno);
        ensureOptionalClass(s.getClassNo());
        studentDao.update(s);
        return student(sno);
    }

    public void deleteStudent(String sno) throws SQLException {
        ensureStudent(sno);
        studentDao.delete(sno);
    }

    public List<Teacher> teachers(String classNo, String keyword) throws SQLException {
        return teacherDao.findAll(classNo, keyword);
    }

    public Teacher teacher(String tno) throws SQLException {
        Teacher t = teacherDao.findByTno(tno);
        if (t == null) {
            throw BusinessException.notFound("教师不存在");
        }
        return t;
    }

    public Teacher createTeacher(Teacher t) throws SQLException {
        validateTeacher(t);
        ensureOptionalClass(t.getClassNo());
        if (teacherDao.existsByTno(t.getTno())) {
            throw BusinessException.conflict("教职工号已存在");
        }
        teacherDao.insert(t);
        return teacher(t.getTno());
    }

    public Teacher updateTeacher(String tno, Teacher t) throws SQLException {
        t.setTno(tno);
        validateTeacher(t);
        ensureTeacher(tno);
        ensureOptionalClass(t.getClassNo());
        teacherDao.update(t);
        return teacher(tno);
    }

    public void deleteTeacher(String tno) throws SQLException {
        ensureTeacher(tno);
        teacherDao.delete(tno);
    }

    public List<Course> courses(String teacherNo, String keyword) throws SQLException {
        return courseDao.findAll(teacherNo, keyword);
    }

    public Course course(String cno) throws SQLException {
        Course c = courseDao.findByCno(cno);
        if (c == null) {
            throw BusinessException.notFound("课程不存在");
        }
        return c;
    }

    public Course createCourse(Course c) throws SQLException {
        validateCourse(c);
        ensureOptionalTeacher(c.getTno());
        if (courseDao.existsByCno(c.getCno())) {
            throw BusinessException.conflict("课程号已存在");
        }
        courseDao.insert(c);
        return course(c.getCno());
    }

    public Course updateCourse(String cno, Course c) throws SQLException {
        c.setCno(cno);
        validateCourse(c);
        ensureCourse(cno);
        ensureOptionalTeacher(c.getTno());
        courseDao.update(c);
        return course(cno);
    }

    public void deleteCourse(String cno) throws SQLException {
        ensureCourse(cno);
        courseDao.delete(cno);
    }

    public List<Score> scores(String sno, String cno) throws SQLException {
        return scoreDao.findAll(sno, cno);
    }

    public Score score(String sno, String cno) throws SQLException {
        Score s = scoreDao.findByKey(sno, cno);
        if (s == null) {
            throw BusinessException.notFound("成绩记录不存在");
        }
        return s;
    }

    public Score createScore(Score s) throws SQLException {
        validateScore(s);
        ensureStudent(s.getSno());
        ensureCourse(s.getCno());
        if (scoreDao.existsByKey(s.getSno(), s.getCno())) {
            throw BusinessException.conflict("该学生此课程成绩已存在");
        }
        scoreDao.insert(s);
        return score(s.getSno(), s.getCno());
    }

    public Score updateScore(String sno, String cno, Score s) throws SQLException {
        s.setSno(sno);
        s.setCno(cno);
        validateScore(s);
        score(sno, cno);
        scoreDao.update(s);
        return score(sno, cno);
    }

    public void deleteScore(String sno, String cno) throws SQLException {
        score(sno, cno);
        scoreDao.delete(sno, cno);
    }

    public Map<String, Object> overview() throws SQLException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("departmentCount", departmentDao.countAll());
        data.put("majorCount", majorDao.countAll());
        data.put("classCount", classDao.countAll());
        data.put("studentCount", studentDao.countAll());
        data.put("teacherCount", teacherDao.countAll());
        data.put("courseCount", courseDao.countAll());
        data.put("scoreCount", scoreDao.countAll());
        return data;
    }

    public Map<String, Object> studentStatistics(String sno) throws SQLException {
        ensureStudent(sno);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("summary", scoreDao.studentSummary(sno));
        data.put("scores", scoreDao.studentStatistics(sno));
        return data;
    }

    public Map<String, Object> courseStatistics(String cno) throws SQLException {
        ensureCourse(cno);
        return scoreDao.courseSummary(cno);
    }

    public List<Map<String, Object>> classStatistics(String classNo) throws SQLException {
        ensureClass(classNo);
        return scoreDao.classSummary(classNo);
    }

    private void validateDepartment(Department d) {
        Validators.requireLength(d.getDeptNo(), 4, "系编号");
        d.setDeptName(Validators.requireText(d.getDeptName(), "系名称"));
    }

    private void validateMajor(Major m) {
        Validators.requireLength(m.getMajorNo(), 6, "专业编号");
        m.setMajorName(Validators.requireText(m.getMajorName(), "专业名称"));
        Validators.requireLength(m.getDeptNo(), 4, "系编号");
    }

    private void validateClass(Clazz c) {
        Validators.requireLength(c.getClassNo(), 8, "班级号");
        c.setClassName(Validators.trimToNull(c.getClassName()));
        Validators.requireLength(c.getMajorNo(), 6, "专业编号");
    }

    private void validateStudent(Student s) {
        Validators.requireLength(s.getSno(), 10, "学号");
        s.setSname(Validators.requireText(s.getSname(), "学生姓名"));
        s.setSsex(defaultSex(s.getSsex()));
        Validators.validateSex(s.getSsex(), "学生性别");
        Validators.validateAge(s.getSage());
        s.setClassNo(Validators.trimToNull(s.getClassNo()));
        if (s.getClassNo() != null) {
            Validators.requireLength(s.getClassNo(), 8, "班级号");
        }
    }

    private void validateTeacher(Teacher t) {
        Validators.requireLength(t.getTno(), 8, "教职工号");
        t.setTname(Validators.requireText(t.getTname(), "教师姓名"));
        t.setTsex(defaultSex(t.getTsex()));
        Validators.validateSex(t.getTsex(), "教师性别");
        t.setTtitle(Validators.trimToNull(t.getTtitle()));
        t.setClassNo(Validators.trimToNull(t.getClassNo()));
        if (t.getClassNo() != null) {
            Validators.requireLength(t.getClassNo(), 8, "班级号");
        }
    }

    private void validateCourse(Course c) {
        Validators.requireLength(c.getCno(), 6, "课程号");
        c.setCname(Validators.requireText(c.getCname(), "课程名称"));
        if (c.getCredit() == null) {
            c.setCredit(BigDecimal.ZERO);
        }
        Validators.validateCredit(c.getCredit());
        c.setTno(Validators.trimToNull(c.getTno()));
        if (c.getTno() != null) {
            Validators.requireLength(c.getTno(), 8, "教职工号");
        }
    }

    private void validateScore(Score s) {
        Validators.requireLength(s.getSno(), 10, "学号");
        Validators.requireLength(s.getCno(), 6, "课程号");
        Validators.validateGrade(s.getGrade());
    }

    private String defaultSex(String sex) {
        String value = Validators.trimToNull(sex);
        return value == null ? "男" : value;
    }

    private void ensureDepartment(String deptNo) throws SQLException {
        if (!departmentDao.existsByDeptNo(deptNo)) {
            throw BusinessException.notFound("系不存在");
        }
    }

    private void ensureMajor(String majorNo) throws SQLException {
        if (!majorDao.existsByMajorNo(majorNo)) {
            throw BusinessException.notFound("专业不存在");
        }
    }

    private void ensureClass(String classNo) throws SQLException {
        if (!classDao.existsByClassNo(classNo)) {
            throw BusinessException.notFound("班级不存在");
        }
    }

    private void ensureOptionalClass(String classNo) throws SQLException {
        if (classNo != null) {
            ensureClass(classNo);
        }
    }

    private void ensureStudent(String sno) throws SQLException {
        if (!studentDao.existsBySno(sno)) {
            throw BusinessException.notFound("学生不存在");
        }
    }

    private void ensureTeacher(String tno) throws SQLException {
        if (!teacherDao.existsByTno(tno)) {
            throw BusinessException.notFound("教师不存在");
        }
    }

    private void ensureOptionalTeacher(String tno) throws SQLException {
        if (tno != null) {
            ensureTeacher(tno);
        }
    }

    private void ensureCourse(String cno) throws SQLException {
        if (!courseDao.existsByCno(cno)) {
            throw BusinessException.notFound("课程不存在");
        }
    }
}
