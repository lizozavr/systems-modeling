package hospital;

import shared.Element;
import java.util.Objects;

public class ElementHospital extends Element {

    private int next_type_element;

    public ElementHospital(double delay, int channels) {
        super(delay, channels);
        this.next_type_element = 0;
    }

    @Override
    public double getDelay() {
        if (Objects.equals(getName(), "RECEPTION")) {
            if (next_type_element == 1) {
                setDelayMean(15);
            } else if (next_type_element == 2) {
                setDelayMean(40);
            } else if (next_type_element == 3) {
                setDelayMean(30);
            }
        }
        return super.getDelay();
    }

    public void inAct(int next_type_element, double t_start) {

    }

    public void setNextTypeElement(int type) {
        this.next_type_element = type;
    }

    public int getNextTypeElement() {
        return this.next_type_element;
    }
}

