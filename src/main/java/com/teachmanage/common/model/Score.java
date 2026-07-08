package com.teachmanage.common.model;

import java.math.BigDecimal;

public class Score {
    private String sno;
    private String sname;
    private String cno;
    private String cname;
    private BigDecimal credit;
    private BigDecimal grade;

    public String getSno() { return sno; }
    public void setSno(String sno) { this.sno = sno; }
    public String getSname() { return sname; }
    public void setSname(String sname) { this.sname = sname; }
    public String getCno() { return cno; }
    public void setCno(String cno) { this.cno = cno; }
    public String getCname() { return cname; }
    public void setCname(String cname) { this.cname = cname; }
    public BigDecimal getCredit() { return credit; }
    public void setCredit(BigDecimal credit) { this.credit = credit; }
    public BigDecimal getGrade() { return grade; }
    public void setGrade(BigDecimal grade) { this.grade = grade; }
}
