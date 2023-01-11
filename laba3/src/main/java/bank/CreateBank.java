package bank;

import shared.Element;

public class CreateBank extends Element {

    public CreateBank(double delay, int channels) {
        super(delay, channels);
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTnextOnIndex(0, super.getTcurr() + super.getDelay());

        var nextElements = super.getAllElements();
        var p1 = nextElements[0];
        var p2 = nextElements[1];

        if (p1.getQueue() == p2.getQueue()) {
            p1.inAct();
        } else if (p1.getQueue() == 0 && p2.getQueue() == 0) {
            p1.inAct();
        } else if (p1.getQueue() < p2.getQueue()) {
            p1.inAct();
        } else {
            p2.inAct();
        }
    }
}
