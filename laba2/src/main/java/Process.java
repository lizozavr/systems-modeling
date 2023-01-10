import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
public class Process extends Element {

    private int queue, maxqueue, max_observed_queue, failure;
    private double meanQueue;
    private int channels;
    private int mean_load;
    private int[] probability;
    private int[] states;
    private double[] tnext;

    public Process(double delay, int channels) {
        super(delay);
        queue = 0;
        maxqueue = Integer.MAX_VALUE;
        meanQueue = 0.0;
        mean_load = 0;
        this.channels = channels;
        this.states = new int[channels];
        Arrays.fill(this.states, 0);
        this.tnext = new double[channels];
        Arrays.fill(this.tnext, Double.MAX_VALUE);
    }

    @Override
    public void inAct() {
        var freeRoute = getFreeChannels();
        if (freeRoute.size() > 0) {
            this.states[freeRoute.get(0)] = 1;
            this.tnext[freeRoute.get(0)] = super.getTcurr() + super.getDelay();
        } else {
            if (getQueue() < getMaxqueue()) {
                setQueue(getQueue() + 1);
            } else {
                failure++;
            }
        }
    }

    @Override
    public void outAct() {
        var currentChannel = this.getCurrentChannel();
        for (int i = 0; i < currentChannel.size(); i++) {
            super.outAct();
            this.tnext[currentChannel.get(i)] = Double.MAX_VALUE;
            this.states[currentChannel.get(i)] = 0;
            if (getQueue() > 0) {
                setQueue(getQueue() - 1);
                this.states[currentChannel.get(i)] = 1;
                this.tnext[currentChannel.get(i)] = super.getTcurr() + super.getDelay();
            }
            var nextElement = super.getNextElement(this.probability);
            if (nextElement != null) {
                nextElement.inAct();
            }
        }
    }

    @Override
    public double getMinTnext() {
        double minValue = this.tnext[0];
        for (var i : this.tnext) {
            if (i < minValue) {
                minValue = i;
            }
        }
        return minValue;
    }

    @Override
    public double[] getTnext() {
        return this.tnext;
    }

    @Override
    public void setNextElement(Element[] nextElement, int[] probability) {
        super.setNextElement(nextElement);
        this.probability = probability;
    }

    public ArrayList<Integer> getCurrentChannel() {
        var currentChannels = new ArrayList<Integer>();
        for (int i = 0; i < channels; i++) {
            if (this.tnext[i] == this.getTcurr()) {
                currentChannels.add(i);
            }
        }
        return currentChannels;
    }

    public ArrayList<Integer> getFreeChannels() {
        var freeChannels = new ArrayList<Integer>();
        for (int i = 0; i < channels; i++) {
            if (this.states[i] == 0) {
                freeChannels.add(i);
            }
        }
        return freeChannels;
    }

    @Override
    public void printInfo() {
        System.out.println(getName() + " states= " + Arrays.toString(this.states) +
                " quantity = " + super.getQuantity() +
                " tnext= " + Arrays.toString(this.tnext) +
                " failure = " + this.getFailure() +
                " queue = " + this.getQueue());
    }

    @Override
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
}
