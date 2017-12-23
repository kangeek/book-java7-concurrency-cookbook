package com.getset.j7cc.chapter2;

public class TicketOffice1 implements Runnable {
    private Cinema cinema;
    public TicketOffice1 (Cinema cinema) {
        this.cinema=cinema;
    }

    @Override
    public void run() {
        cinema.sellTickets1(3);
        cinema.sellTickets1(2);
        cinema.sellTickets2(2);
        cinema.returnTickets1(3);
        cinema.sellTickets1(5);
        cinema.sellTickets2(2);
        cinema.sellTickets2(2);
        cinema.sellTickets2(2);
    }
}
