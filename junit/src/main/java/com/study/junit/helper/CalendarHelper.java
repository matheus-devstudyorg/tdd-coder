package com.study.junit.helper;

import java.util.Date;
import static com.study.junit.util.DateUtil.addDays;

public class CalendarHelper {
    public Date today(){
        return new Date();
    }

    public Date tomorrow(){
        return futureOf(1);
    }

    public Date yesterday(){
        return futureOf(-1);
    }

    public Date beforeYesterday(){
        return futureOf(-2);
    }

    public Date futureOf(int days) {
        return addDays(today(), days);
    }
}
