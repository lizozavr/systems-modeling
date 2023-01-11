package hospital;

import shared.Element;
import shared.Model;

import java.util.ArrayList;
import java.util.Objects;

public class ModelHospital extends Model {
    public ModelHospital(ArrayList<Element> elements) {
        super(elements);
        event = elements.get(0).getId();
    }

    @Override
    public void simulate(double time) {
        while (tcurr < time) {
            tnext = Double.MAX_VALUE;
            for (Element e : list) {
                if (e.getMinTnext() < tnext && !(e instanceof DisposeHospital)) {
                    tnext = e.getMinTnext();
                    event = e.getId();
                }
            }
            for (var e : list) {
                e.doStatistics(tnext - tcurr);
            }
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
        }
        printResult();
    }

    public void printResult() {
        super.printResult();
        System.out.println();
        double globalMeanIntervalBetweenArrivingToTheLab = 0;
        double globalMeanTimeFinishingAccumulator = 0;
        double numOfFinished = 0;

        for (var e : list) {
            e.printResult();
            if (e instanceof ProcessHospital p) {
                if (Objects.equals(e.getName(), "GO_TO_THE_LAB_RECEPTION")) {
                    globalMeanIntervalBetweenArrivingToTheLab = p.delta_t_following_to_the_lab_reception / p.getQuantity();
                } else if (Objects.equals(e.getName(), "GO_TO_THE_RECEPTION")) {
                    double meanTimeFinishing = 0;
                    if (p.type2_cnt_new != 0) {
                        meanTimeFinishing = p.delta_t_finished2_new / p.type2_cnt_new;
                    } else {
                        meanTimeFinishing = Double.MAX_VALUE;
                    }
                    System.out.println("Mean time finishing for 2 type: " + meanTimeFinishing);
                }
            } else if (e instanceof DisposeHospital p) {
                globalMeanTimeFinishingAccumulator += p.delta_t_finished1 + p.delta_t_finished2 + p.delta_t_finished3;
                numOfFinished += p.getQuantity();

                double meanTimeFinishingType1 = p.type1_cnt != 0 ? p.delta_t_finished1 / p.type1_cnt : Double.MAX_VALUE;
                double meanTimeFinishingType2 = p.type2_cnt != 0 ? p.delta_t_finished2 / p.type2_cnt : Double.MAX_VALUE;
                double meanTimeFinishingType3 = p.type3_cnt != 0 ? p.delta_t_finished3 / p.type3_cnt : Double.MAX_VALUE;

                if (meanTimeFinishingType1 != Double.MAX_VALUE)
                    System.out.println("Mean time finishing for 1 type: " + meanTimeFinishingType1);
                if (meanTimeFinishingType2 != Double.MAX_VALUE)
                    System.out.println("Mean time finishing for 2 type: " + meanTimeFinishingType2);
                if (meanTimeFinishingType3 != Double.MAX_VALUE)
                    System.out.println("Mean time finishing for 3 type: " + meanTimeFinishingType3);
            }
        }

        var globalMeanTimeFinishing = globalMeanTimeFinishingAccumulator / numOfFinished;
        System.out.println("Global mean interval between arriving to the laboratory: " + globalMeanTimeFinishing);
        System.out.println("Global mean time of finishing: " + globalMeanIntervalBetweenArrivingToTheLab);
    }
}
