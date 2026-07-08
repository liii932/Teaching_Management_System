package com.teachmanage.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.teachmanage.common.exception.BusinessException;
import com.teachmanage.common.model.*;
import com.teachmanage.server.service.TeachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ApiHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiHandler.class);
    private final TeachingService service;

    public ApiHandler(TeachingService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (WebUtil.handleCors(exchange)) {
            return;
        }
        try {
            route(exchange);
        } catch (BusinessException e) {
            WebUtil.sendError(exchange, e.status(), e.getMessage());
        } catch (Exception e) {
            log.error("API error: {} {}", exchange.getRequestMethod(), exchange.getRequestURI(), e);
            WebUtil.sendError(exchange, 500, e.getMessage() == null ? "服务器内部错误" : e.getMessage());
        }
    }

    private void route(HttpExchange exchange) throws Exception {
        List<String> parts = WebUtil.segments(exchange);
        if (parts.isEmpty()) {
            WebUtil.sendError(exchange, 404, "Not Found");
            return;
        }
        String resource = parts.get(0);
        String method = exchange.getRequestMethod().toUpperCase();
        switch (resource) {
            case "health" -> require(method, "GET", () -> WebUtil.sendOk(exchange, service.health()));
            case "departments" -> departments(exchange, parts, method);
            case "majors" -> majors(exchange, parts, method);
            case "classes" -> classes(exchange, parts, method);
            case "students" -> students(exchange, parts, method);
            case "teachers" -> teachers(exchange, parts, method);
            case "courses" -> courses(exchange, parts, method);
            case "scores" -> scores(exchange, parts, method);
            case "statistics" -> statistics(exchange, parts, method);
            default -> WebUtil.sendError(exchange, 404, "Unknown API");
        }
    }

    private void departments(HttpExchange exchange, List<String> p, String method) throws Exception {
        if (p.size() == 1 && method.equals("GET")) WebUtil.sendOk(exchange, service.departments());
        else if (p.size() == 1 && method.equals("POST")) WebUtil.sendCreated(exchange, service.createDepartment(WebUtil.readBody(exchange, Department.class)));
        else if (p.size() == 2 && method.equals("GET")) WebUtil.sendOk(exchange, service.department(p.get(1)));
        else if (p.size() == 2 && method.equals("PUT")) WebUtil.sendOk(exchange, service.updateDepartment(p.get(1), WebUtil.readBody(exchange, Department.class)));
        else if (p.size() == 2 && method.equals("DELETE")) { service.deleteDepartment(p.get(1)); WebUtil.sendOk(exchange, Map.of("deleted", true)); }
        else methodNotAllowed(exchange);
    }

    private void majors(HttpExchange exchange, List<String> p, String method) throws Exception {
        Map<String, String> q = WebUtil.query(exchange);
        if (p.size() == 1 && method.equals("GET")) WebUtil.sendOk(exchange, service.majors(q.get("deptNo")));
        else if (p.size() == 1 && method.equals("POST")) WebUtil.sendCreated(exchange, service.createMajor(WebUtil.readBody(exchange, Major.class)));
        else if (p.size() == 2 && method.equals("GET")) WebUtil.sendOk(exchange, service.major(p.get(1)));
        else if (p.size() == 2 && method.equals("PUT")) WebUtil.sendOk(exchange, service.updateMajor(p.get(1), WebUtil.readBody(exchange, Major.class)));
        else if (p.size() == 2 && method.equals("DELETE")) { service.deleteMajor(p.get(1)); WebUtil.sendOk(exchange, Map.of("deleted", true)); }
        else methodNotAllowed(exchange);
    }

    private void classes(HttpExchange exchange, List<String> p, String method) throws Exception {
        Map<String, String> q = WebUtil.query(exchange);
        if (p.size() == 1 && method.equals("GET")) WebUtil.sendOk(exchange, service.classes(q.get("majorNo")));
        else if (p.size() == 1 && method.equals("POST")) WebUtil.sendCreated(exchange, service.createClass(WebUtil.readBody(exchange, Clazz.class)));
        else if (p.size() == 2 && method.equals("GET")) WebUtil.sendOk(exchange, service.clazz(p.get(1)));
        else if (p.size() == 2 && method.equals("PUT")) WebUtil.sendOk(exchange, service.updateClass(p.get(1), WebUtil.readBody(exchange, Clazz.class)));
        else if (p.size() == 2 && method.equals("DELETE")) { service.deleteClass(p.get(1)); WebUtil.sendOk(exchange, Map.of("deleted", true)); }
        else methodNotAllowed(exchange);
    }

    private void students(HttpExchange exchange, List<String> p, String method) throws Exception {
        Map<String, String> q = WebUtil.query(exchange);
        if (p.size() == 1 && method.equals("GET")) WebUtil.sendOk(exchange, service.students(q.get("classNo"), q.get("keyword")));
        else if (p.size() == 1 && method.equals("POST")) WebUtil.sendCreated(exchange, service.createStudent(WebUtil.readBody(exchange, Student.class)));
        else if (p.size() == 2 && method.equals("GET")) WebUtil.sendOk(exchange, service.student(p.get(1)));
        else if (p.size() == 2 && method.equals("PUT")) WebUtil.sendOk(exchange, service.updateStudent(p.get(1), WebUtil.readBody(exchange, Student.class)));
        else if (p.size() == 2 && method.equals("DELETE")) { service.deleteStudent(p.get(1)); WebUtil.sendOk(exchange, Map.of("deleted", true)); }
        else methodNotAllowed(exchange);
    }

    private void teachers(HttpExchange exchange, List<String> p, String method) throws Exception {
        Map<String, String> q = WebUtil.query(exchange);
        if (p.size() == 1 && method.equals("GET")) WebUtil.sendOk(exchange, service.teachers(q.get("classNo"), q.get("keyword")));
        else if (p.size() == 1 && method.equals("POST")) WebUtil.sendCreated(exchange, service.createTeacher(WebUtil.readBody(exchange, Teacher.class)));
        else if (p.size() == 2 && method.equals("GET")) WebUtil.sendOk(exchange, service.teacher(p.get(1)));
        else if (p.size() == 2 && method.equals("PUT")) WebUtil.sendOk(exchange, service.updateTeacher(p.get(1), WebUtil.readBody(exchange, Teacher.class)));
        else if (p.size() == 2 && method.equals("DELETE")) { service.deleteTeacher(p.get(1)); WebUtil.sendOk(exchange, Map.of("deleted", true)); }
        else methodNotAllowed(exchange);
    }

    private void courses(HttpExchange exchange, List<String> p, String method) throws Exception {
        Map<String, String> q = WebUtil.query(exchange);
        if (p.size() == 1 && method.equals("GET")) WebUtil.sendOk(exchange, service.courses(q.get("teacherNo"), q.get("keyword")));
        else if (p.size() == 1 && method.equals("POST")) WebUtil.sendCreated(exchange, service.createCourse(WebUtil.readBody(exchange, Course.class)));
        else if (p.size() == 2 && method.equals("GET")) WebUtil.sendOk(exchange, service.course(p.get(1)));
        else if (p.size() == 2 && method.equals("PUT")) WebUtil.sendOk(exchange, service.updateCourse(p.get(1), WebUtil.readBody(exchange, Course.class)));
        else if (p.size() == 2 && method.equals("DELETE")) { service.deleteCourse(p.get(1)); WebUtil.sendOk(exchange, Map.of("deleted", true)); }
        else methodNotAllowed(exchange);
    }

    private void scores(HttpExchange exchange, List<String> p, String method) throws Exception {
        Map<String, String> q = WebUtil.query(exchange);
        if (p.size() == 1 && method.equals("GET")) WebUtil.sendOk(exchange, service.scores(q.get("studentNo"), q.get("courseNo")));
        else if (p.size() == 1 && method.equals("POST")) WebUtil.sendCreated(exchange, service.createScore(WebUtil.readBody(exchange, Score.class)));
        else if (p.size() == 3 && method.equals("GET")) WebUtil.sendOk(exchange, service.score(p.get(1), p.get(2)));
        else if (p.size() == 3 && method.equals("PUT")) WebUtil.sendOk(exchange, service.updateScore(p.get(1), p.get(2), WebUtil.readBody(exchange, Score.class)));
        else if (p.size() == 3 && method.equals("DELETE")) { service.deleteScore(p.get(1), p.get(2)); WebUtil.sendOk(exchange, Map.of("deleted", true)); }
        else methodNotAllowed(exchange);
    }

    private void statistics(HttpExchange exchange, List<String> p, String method) throws Exception {
        if (!method.equals("GET")) {
            methodNotAllowed(exchange);
            return;
        }
        if (p.size() == 2 && p.get(1).equals("overview")) WebUtil.sendOk(exchange, service.overview());
        else if (p.size() == 3 && p.get(1).equals("student")) WebUtil.sendOk(exchange, service.studentStatistics(p.get(2)));
        else if (p.size() == 3 && p.get(1).equals("course")) WebUtil.sendOk(exchange, service.courseStatistics(p.get(2)));
        else if (p.size() == 3 && p.get(1).equals("class")) WebUtil.sendOk(exchange, service.classStatistics(p.get(2)));
        else WebUtil.sendError(exchange, 404, "Unknown statistics API");
    }

    private void require(String actual, String expected, IoTask task) throws Exception {
        if (!actual.equals(expected)) {
            throw new BusinessException(405, "Method Not Allowed");
        }
        task.run();
    }

    private void methodNotAllowed(HttpExchange exchange) throws IOException {
        WebUtil.sendError(exchange, 405, "Method Not Allowed");
    }

    @FunctionalInterface
    private interface IoTask {
        void run() throws Exception;
    }
}
