package bank;

import shared.Element;
import shared.Model;

import java.util.ArrayList;

public class ModelBank extends Model {
    private int queue_move;
    private double average_client;
    private ProcessBank[] balancing;

    public ModelBank(ArrayList<Element> elements, ProcessBank[] balancing) {
        super(elements);
        queue_move = 0;
        this.balancing = balancing;
        average_client = 0;

    }

    public void calculateAverageClient(double delta) {
        average_client += delta *
                (double) (balancing[0].getQueue() + balancing[1].getQueue() + balancing[0].getStateOnIndex(0) + balancing[1].getStateOnIndex(0));
    }

    @Override
    public void simulate(double time) {
        while (tcurr < time) {
            tnext = Double.MAX_VALUE;
            for (Element e : list) {
                if (e.getMinTnext() < tnext) {
                    tnext = e.getMinTnext();
                    event = e.getId();

                }
            }
            for (var e : list) {
                e.doStatistics(tnext - tcurr);
            }
            calculateAverageClient(tnext - tcurr);
            tcurr = tnext;
            for (var e : list) {
                e.setTcurr(tcurr);
            }
            if (list.size() > event) {
                list.get(event).outAct();
            }
            for (var e : list) {
                var arrayTnexts = e.getTnext();
                for (var tn : arrayTnexts) {
                    if (tn == tcurr) {
                        e.outAct();
                        break;
                    }
                }
            }
            printInfo();
            changeQueue();
        }
        printResult();
    }

    public void changeQueue() {
        var queueList = new ArrayList<Integer>();

        for (Element e : list) {
            if (e instanceof ProcessBank) {
                queueList.add(e.getQueue());
            }
        }

        if (queueList.get(0) - queueList.get(1) >= 2) {
            list.get(1).setQueue(list.get(1).getQueue() - 1);
            list.get(1).setQueue(list.get(2).getQueue() + 1);
            System.out.println("From CASH1 queue 1 car moved to CASH2 queue!");
            queue_move += 1;
        } else if (queueList.get(1) - queueList.get(0) >= 2) {
            list.get(2).setQueue(list.get(2).getQueue() - 1);
            list.get(1).setQueue(list.get(1).getQueue() + 1);
            System.out.println("From CASH2 queue 1 car moved to CASH1 queue!");
            queue_move += 1;
        }
    }

    @Override
    public void printResult() {
        super.printResult();
        double globalMeanTimeOfDepartureAccumulator = 0;
        double globalMeanTimeInBankAccumulator = 0;
        int numOfProcessors = 0;

        for (Element e : list) {
            if (e instanceof ProcessBank) {
                numOfProcessors += 1;
                globalMeanTimeOfDepartureAccumulator += ((ProcessBank) e).delta_t_departure / e.getQuantity();
                globalMeanTimeInBankAccumulator += ((ProcessBank) e).delta_t_in_bank / e.getQuantity();

                System.out.println("Mean time of departure (" + e.getName() + "): " + ((ProcessBank) e).delta_t_departure / e.getQuantity());
            }
        }

        average_client = (average_client / tcurr);
        var globalMeanTimeOfDeparture = globalMeanTimeOfDepartureAccumulator / numOfProcessors;
        var globalMeanTimeInBank = globalMeanTimeInBankAccumulator / numOfProcessors;

        System.out.println("Global mean client in bank: " + average_client);
        System.out.println("Global mean time of departure: " + globalMeanTimeOfDeparture);
        System.out.println("Global mean time in bank: " + globalMeanTimeInBank);
        System.out.println("Global change queue cnt: " + queue_move);
    }
}
