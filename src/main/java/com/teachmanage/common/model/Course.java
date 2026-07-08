package com.teachmanage.common.model;

import java.math.BigDecimal;

public class Course {
    private String cno;
    private String cname;
    private BigDecimal credit;
    private String tno;
    private String tname;

    public String getCno() { return cno; }
    public void setCno(String cno) { this.cno = cno; }
    public String getCname() { return cname; }
    public void setCname(String cname) { this.cname = cname; }
    public BigDecimal getCredit() { return credit; }
    public void setCredit(BigDecimal credit) { this.credit = credit; }
    public String getTno() { return tno; }
    public void setTno(String tno) { this.tno = tno; }
    public String getTname() { return tname; }
    public void setTname(String tname) { this.tname = tname; }
}
