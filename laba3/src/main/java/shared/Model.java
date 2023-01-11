package shared;

import bank.ProcessBank;
import java.util.ArrayList;

public class Model {

    public ArrayList<Element> list = new ArrayList<>();
    public double tnext, tcurr;
    public int event;

    public Model(ArrayList<Element> elements) {
        list = elements;
        tnext = 0.0;
        event = 0;
        tcurr = tnext;
    }

    public void simulate(double time) {
        while (tcurr < time) {
            tnext = Double.MAX_VALUE;
            for (Element e : list) {
                if (e.getMinTnext() < tnext) {
                    tnext = e.getMinTnext();
                    event = e.getId();

                }
            }
            for (Element e : list) {
                e.doStatistics(tnext - tcurr);
            }
            tcurr = tnext;
            for (Element e : list) {
                e.setTcurr(tcurr);
            }
            if (list.size() > event) {
                list.get(event).outAct();
            }

            for (Element e : list) {
                var arrayTnexts = e.getTnext();
                for (var tn : arrayTnexts) {
                    if (tn == tcurr) {
                        e.outAct();
                        break;
                    }
                }
            }
            printInfo();
        }
        printResult();
    }

    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }

    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        double globalMaxObservedQueueLength = 0.0;
        double globalMeanQueueLengthAccumulator = 0.0;
        double globalFailureProbabilityAccumulator = 0.0;
        double globalMeanLoadAccumulator = 0.0;
        int numOfProcessors = 0;
        double meanQueueLength = 0.0;
        double failureProbability = 0.0;
        double meanLoad = 0.0;

        for (Element p : list) {
            p.printResult();
            if (p instanceof Process || p instanceof ProcessBank) {

                numOfProcessors += 1;
                meanQueueLength = p.getMeanQueue() / this.tcurr;

                if (p.getQuantity() + p.getFailure() != 0) {
                    failureProbability = (double) (p.getFailure()) / (p.getQuantity() + p.getFailure());
                } else {
                    failureProbability = 0;
                }

                meanLoad = p.getMeanLoad() / tcurr;

                globalMeanQueueLengthAccumulator += meanQueueLength;
                globalFailureProbabilityAccumulator += failureProbability;
                globalMeanLoadAccumulator += meanLoad;

                if (p.getMaxObservedQueue() > globalMaxObservedQueueLength) {
                    globalMaxObservedQueueLength = p.getMaxObservedQueue();
                }


                System.out.println("Average queue length: " + meanQueueLength);
                System.out.println("Failure probability: " + failureProbability);
                System.out.println("Average load: " + meanLoad);
                System.out.println();
            }
        }
        var globalMeanQueueLength = globalMeanQueueLengthAccumulator / numOfProcessors;
        var globalFailureProbability = globalFailureProbabilityAccumulator / numOfProcessors;
        var globalMeanLoad = globalMeanLoadAccumulator / numOfProcessors;

        System.out.println("Global max observed queue length: " + globalMaxObservedQueueLength);
        System.out.println("Global mean queue length: " + globalMeanQueueLength);
        System.out.println("Global failure probability: " + globalFailureProbability);
        System.out.println("Global mean load: " + globalMeanLoad);
    }
}