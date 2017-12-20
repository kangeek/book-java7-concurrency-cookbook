package com.getset.j7cc.chapter1;

import java.util.Date;

public class Event {
    private Date date;
    private String event;

    public Event(String event) {
        this.date = new Date();
        this.event = event;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
