import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Random;

@Getter
@Setter
public class Element {
    private String name;
    private double tnext;
    private double delayMean, delayDev;
    private String distribution;
    private int quantity;
    private double tcurr;
    private int state;
    private Element[] nextElement;
    private static int nextId = 0;
    private int id;


    public Element() {
        tnext = Double.MAX_VALUE;
        delayMean = 1.0;
        distribution = "exp";
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;
        name = "element" + id;
    }

    public Element(double delay) {
        name = "anonymus";
        tnext = 0.0;
        delayMean = delay;
        distribution = "";
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;
        name = "element" + id;
    }

    public Element(String nameOfElement, double delay) {
        name = nameOfElement;
        tnext = 0.0;
        delayMean = delay;
        distribution = "exp";
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;
        name = "element" + id;
    }

    public double getDelay() {
        double delay = getDelayMean();
        if ("exp".equalsIgnoreCase(getDistribution())) {
            delay = FunRand.Exp(getDelayMean());
        } else {
            if ("norm".equalsIgnoreCase(getDistribution())) {
                delay = FunRand.Norm(getDelayMean(),
                        getDelayDev());
            } else {
                if ("unif".equalsIgnoreCase(getDistribution())) {
                    delay = FunRand.Unif(getDelayMean(),
                            getDelayDev());
                } else {
                    if ("".equalsIgnoreCase(getDistribution()))
                        delay = getDelayMean();
                }
            }
        }
        return delay;
    }

    public Element getNextElement(int[] probability) {
        if (this.nextElement == null) {
            return null;
        }
        if (probability.length != this.nextElement.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        var listOfProbabilities = new ArrayList<Element>();
        for (int i = 0; i < probability.length; i++) {
            for (int j = 0; j < probability[i]; j++) {
                listOfProbabilities.add(this.nextElement[i]);
            }
        }
        Random r = new Random();
        return listOfProbabilities.get(r.nextInt(100));
    }

    public void setNextElement(Element[] nextElement) {
        this.nextElement = nextElement;
    }

    public void setNextElement(Element[] nextElement, int[] probability) {

    }

    public void inAct() {
    }

    public void outAct() {
        quantity++;
    }

    public double[] getTnext() {
        return new double[]{tnext};
    }

    public double getMinTnext() {
        return tnext;
    }

    public void printResult() {
        System.out.println(getName() + "  quantity = " + quantity);
    }

    public void printInfo() {
        System.out.println(getName() + " state= " + state +
                " quantity = " + quantity +
                " tnext= " + tnext);
    }

    public void doStatistics(double delta) {

    }
}
