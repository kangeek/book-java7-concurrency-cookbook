package com.getset.j7cc.chapter3;

import java.util.concurrent.TimeUnit;

public class Participant implements Runnable {
    private String name;
    private VideoConference conference;

    public Participant(String name, VideoConference conference) {
        this.name = name;
        this.conference = conference;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep((int)(Math.random()*10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        conference.linkedIn(name);
    }
}
