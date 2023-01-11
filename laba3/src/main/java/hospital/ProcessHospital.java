package hospital;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProcessHospital extends ElementHospital {
    int[] types;
    double[] t_starts;
    int[][] required_path;
    ArrayList<Double> t_starts_queue = new ArrayList<>();
    int[] prior_types;
    ArrayList<Integer> queue_types = new ArrayList<>();
    double delta_t_following_to_the_lab_reception;
    double tprev_following_to_the_lab_reception;
    double delta_t_finished2_new;
    double type2_cnt_new;

    public ProcessHospital(double delay, int channels) {
        super(delay, channels);
        this.types = new int[channels];
        Arrays.fill(this.types, -1);
        delta_t_following_to_the_lab_reception = 0;
        tprev_following_to_the_lab_reception = 0;
        delta_t_finished2_new = 0;
        type2_cnt_new = 0;
        t_starts = new double[channels];
        Arrays.fill(this.t_starts, -1);
    }

    public void setRequiredPath(int[][] required_path) {
        this.required_path = required_path;
    }

    public void setPriorTypes(int[] priorTypes) {
        this.prior_types = priorTypes;
    }

    @Override
    public void inAct(int next_type_element, double t_start) {
        setNextTypeElement(next_type_element);
        if (Objects.equals(getName(), "GO_TO_THE_LAB_RECEPTION")) {
            delta_t_following_to_the_lab_reception += getTcurr() - tprev_following_to_the_lab_reception;
            tprev_following_to_the_lab_reception = getTcurr();
        }
        if (Objects.equals(getName(), "GO_TO_THE_RECEPTION") && next_type_element == 2) {
            delta_t_finished2_new += getTcurr() - t_start;
            type2_cnt_new++;
        }
        var freeChannels = getFreeChannels();
        if (freeChannels.size() > 0) {
            for (var i : freeChannels) {
                super.setStateOnIndex(freeChannels.get(freeChannels.indexOf(i)), 1);
                this.setTnextOnIndex(freeChannels.get(freeChannels.indexOf(i)), super.getTcurr() + super.getDelay());
                this.types[i] = next_type_element;
                this.t_starts[i] = t_start;
                break;
            }
        } else {
            if (getQueue() < getMaxqueue()) {
                setQueue(getQueue() + 1);
                queue_types.add(next_type_element);
                t_starts_queue.add(t_start);
            } else {
                super.addFailure();
            }
        }
    }

    @Override
    public void outAct() {
        super.outAct();
        var currentChannel = this.getCurrentChannel();

        for (var i : currentChannel) {
            this.setTnextOnIndex(i, Double.MAX_VALUE);
            this.setStateOnIndex(i, 0);

            var prevNextTypeElement = types[i];
            var prevTStart = t_starts[i];

            types[i] = -1;
            t_starts[i] = -1;

            if (getQueue() > 0) {
                setQueue(getQueue() - 1);
                int priorIndexFromQueue = getPriorIndexFromQueue();
                var nextTypeElement = queue_types.remove(priorIndexFromQueue);
                setNextTypeElement(nextTypeElement);
                this.setStateOnIndex(i, 1);
                this.setTnextOnIndex(i, super.getTcurr() + super.getDelay());
                types[i] = nextTypeElement;
                t_starts[i] = t_starts_queue.remove(priorIndexFromQueue);
            }

            var nextElement1 = super.getNextElement();
            if (nextElement1 != null) {
                if (Objects.equals(getName(), "GO_TO_THE_RECEPTION")) {
                    setNextTypeElement(1);
                } else {
                    setNextTypeElement(prevNextTypeElement);
                }
                System.out.println("Next type element: " + getNextTypeElement());

                if (required_path == null) {
                    var nextEl = getNextElement();
                    if (nextEl instanceof ElementHospital) {
                        ((ElementHospital) nextEl).inAct(getNextTypeElement(), prevTStart);
                    }
                } else {
                    for (int pathIndex = 0; pathIndex < required_path.length; pathIndex++) {
                        for (int indexIndex = 0; indexIndex < required_path[pathIndex].length; indexIndex++) {
                            if (required_path[pathIndex][indexIndex] == getNextTypeElement()) {
                                var allElements = getAllElements();
                                var nextElement = allElements[pathIndex];
                                ((ElementHospital) nextElement).inAct(getNextTypeElement(), prevTStart);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public int getPriorIndexFromQueue() {
        if (prior_types != null && prior_types.length > 0) {
            for (var priorIndex : prior_types) {
                var distinctQueueTypes = queue_types.stream().distinct().collect(Collectors.toList());
                for (var typeIndex : distinctQueueTypes) {
                    if (typeIndex == priorIndex) {
                        return queue_types.indexOf(typeIndex);
                    }
                }
            }
        }
        return 0;
    }


}