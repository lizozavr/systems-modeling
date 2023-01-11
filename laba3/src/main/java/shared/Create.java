package shared;

public class Create extends Element {

    public Create(double delay, int channels) {
        super(delay,channels);
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTnextOnIndex(0,super.getTcurr() + super.getDelay());
        super.getNextElement().inAct();
    }
}
