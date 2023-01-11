package shared;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

@Getter
@Setter
public class Element {
    private String name;
    private double delayMean, delayDev;
    private String distribution;
    private int quantity;
    private double tcurr;
    private Element[] nextElement;
    private static int nextId = 0;
    private int id;
    private int[] states;
    private double[] tnext;
    private int channels;
    private int queue, maxqueue, max_observed_queue, failure;
    private double meanQueue;
    private int mean_load;
    private int[] probability;
    private int[] priority;

    public Element(double delay, int channels) {
        name = "anonymus";
        delayMean = delay;
        distribution = "";
        nextElement = null;
        id = nextId;
        nextId++;
        name = "element" + id;
        queue = 0;
        maxqueue = Integer.MAX_VALUE;
        meanQueue = 0.0;
        mean_load = 0;
        this.channels = channels;
        this.states = new int[channels];
        Arrays.fill(this.states, 0);
        this.tnext = new double[channels];
        Arrays.fill(this.tnext, 0);
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

    public void setStateOnIndex(int inex, int state) {
        this.states[inex] = state;
    }

    public int getStateOnIndex(int index) {
        return this.states[index];
    }

    public Element getNextElement() {
        if (this.nextElement == null) {
            return null;
        }
        if (probability != null && priority != null) {
            throw new ArrayIndexOutOfBoundsException("Priority and probability were set together!");
        }
        if (probability != null) {

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

        } else if (priority != null) {
            return selectByPriority();
        } else {
            Random r = new Random();
            var nextElements = getAllElements();
            return nextElements[r.nextInt(nextElements.length)];
        }
    }

    public Element selectByPriority() {
        var priorities = Arrays.copyOf(priority, priority.length);
        var minQueue = Double.MAX_VALUE;
        var minQueueIndex = 0;
        for (int i = 0; i < priorities.length; i++) {
            var min = Arrays.stream(priorities).min().getAsInt();
            if (min == 100000)
                break;

            var maxPrIndex = -1;
            for (int n = 0; n < priorities.length; n++) {
                if (priorities[n] == min) {
                    maxPrIndex = n;
                    break;
                }
            }
            for (var state : nextElement[maxPrIndex].states) {
                if (state == 0) {
                    return nextElement[maxPrIndex];
                } else {
                    if (nextElement[maxPrIndex].queue < minQueue) {
                        minQueue = nextElement[maxPrIndex].queue;
                        minQueueIndex = maxPrIndex;
                    }
                }
                priorities[maxPrIndex] = 100000;
            }
        }
        return nextElement[minQueueIndex];
    }

    public Element[] getAllElements() {
        return this.nextElement;
    }

    private void setNextElement(Element[] nextElement) {
        this.nextElement = nextElement;
    }

    public void inAct() {

    }

    public void outAct() {
        quantity++;
    }

    public double getMinTnext() {
        double minValue = this.tnext[0];
        for (var i : this.tnext) {
            if (i < minValue) {
                minValue = i;
            }
        }
        return minValue;
    }

    public void setTnext(double[] tnext) {
        this.tnext = tnext;
    }

    public void setTnextOnIndex(int index, double tnext) {
        this.tnext[index] = tnext;
    }

    public double getDelayMean() {
        return delayMean;
    }

    public void setDelayMean(double delayMean) {
        this.delayMean = delayMean;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void printResult() {
        System.out.println(getName() + " quantity = " + quantity);
    }

    public void printInfo() {
        System.out.println(getName() + " states = " + Arrays.toString(this.states) +
                " quantity = " + this.getQuantity() +
                " tnext = " + Arrays.toString(this.tnext) +
                " failure = " + this.getFailure() +
                " queue = " + this.getQueue());
    }

    public void addFailure() {
        failure++;
    }

    public void doStatistics(double delta) {
        this.meanQueue += this.queue * delta;

        if (this.queue > this.max_observed_queue) {
            this.max_observed_queue = this.queue;
        }

        for (int i = 0; i < channels; i++) {
            this.mean_load += this.states[i] * delta;
        }
        this.mean_load = this.mean_load / this.channels;
    }

    public double getMeanLoad() {
        return mean_load;
    }

    public int getMaxObservedQueue() {
        return max_observed_queue;
    }

    public double getMeanQueue() {
        return meanQueue;
    }

    public double[] getTnext() {
        return this.tnext;
    }

    public void setNextElement(Element[] nextElement, int[] probability, int[] priority) {
        this.setNextElement(nextElement);
        this.probability = probability;
        this.priority = priority;
    }

    public ArrayList<Integer> getCurrentChannel() {
        var current_channels = new ArrayList<Integer>();
        for (int i = 0; i < channels; i++) {
            if (this.tnext[i] == this.getTcurr()) {
                current_channels.add(i);
            }
        }
        return current_channels;
    }

    public ArrayList<Integer> getFreeChannels() {
        var free_channels = new ArrayList<Integer>();
        for (int i = 0; i < channels; i++) {
            if (this.states[i] == 0) {
                free_channels.add(i);
            }
        }
        return free_channels;
    }
}
