package bank;

import shared.Element;

public class ProcessBank extends Element {
    public double delta_t_departure, tprev_departure, delta_t_in_bank, tprev_in_bank;

    public ProcessBank(double delay, int channels) {
        super(delay, channels);
        delta_t_departure = 0;
        tprev_departure = 0;
        delta_t_in_bank = 0;
        tprev_in_bank = 0;
    }

    @Override
    public void inAct() {
        var freeRoute = getFreeChannels();
        if (freeRoute.size() > 0) {
            for (var i : freeRoute) {
                tprev_in_bank = getTcurr();
                setStateOnIndex(i, 1);
                setTnextOnIndex(i, getTcurr() + getDelay());
                break;
            }
        } else {
            if (getQueue() < getMaxqueue()) {
                setQueue(getQueue() + 1);
            } else {
                super.addFailure();
            }
        }
    }

    @Override
    public void outAct() {
        super.outAct();
        var currentChannel = getCurrentChannel();
        for (var i : currentChannel) {
            setTnextOnIndex(i, Double.MAX_VALUE);
            setStateOnIndex(i, 0);

            delta_t_departure += getTcurr() - tprev_departure;
            tprev_departure = getTcurr();
            delta_t_in_bank = getTcurr() - tprev_in_bank;

            if (getQueue() > 0) {
                setQueue(getQueue() - 1);
                setStateOnIndex(i, 1);
                setTnextOnIndex(i, getTcurr() + getDelay());
            }

            var nextEl = getNextElement();
            if (nextEl != null) {
                nextEl.inAct();
            }
        }
    }
}
