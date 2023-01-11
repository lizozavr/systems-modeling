package shared;

public class Process extends Element {

    public Process(double delay, int channels) {
        super(delay, channels);
    }

    @Override
    public void inAct() {
        var freeRoute = getFreeChannels();
        if (freeRoute.size() > 0) {
            for (var i : freeRoute) {
                super.setStateOnIndex(freeRoute.get(freeRoute.indexOf(i)), 1);
                this.setTnextOnIndex(freeRoute.get(freeRoute.indexOf(i)), super.getTcurr() + super.getDelay());
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
        var current_channel = this.getCurrentChannel();
        for (int i = 0; i < current_channel.size(); i++) {
            super.outAct();
            this.setTnextOnIndex(current_channel.get(i), Double.MAX_VALUE);
            this.setStateOnIndex(current_channel.get(i), 0);
            if (getQueue() > 0) {
                setQueue(getQueue() - 1);
                this.setStateOnIndex(current_channel.get(i), 1);
                this.setTnextOnIndex(current_channel.get(i), super.getTcurr() + super.getDelay());
            }
            var next_el = super.getNextElement();
            if (next_el != null) {
                next_el.inAct();
            }
        }
    }
}
