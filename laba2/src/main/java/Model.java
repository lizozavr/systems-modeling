import java.util.ArrayList;

public class Model {

    private ArrayList<Element> list = new ArrayList<>();
    double tnext, tcurr;
    int event;

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
        var global_max_observed_queue_length = 0.0;
        var global_mean_queue_length_accumulator = 0.0;
        var global_failure_probability_accumulator = 0.0;
        var global_mean_load_accumulator = 0.0;
        var num_of_processors = 0;
        var mean_queue_length = 0.0;
        var failure_probability = 0.0;
        var mean_load = 0.0;

        for (Element e : list) {
            e.printResult();
            if (e instanceof Process) {
                Process p = (Process) e;

                num_of_processors += 1;
                mean_queue_length = p.getMeanQueue() / this.tcurr;

                if (e.getQuantity() + p.getFailure() != 0) {
                    failure_probability = (double) (p.getFailure()) / (e.getQuantity() + p.getFailure());
                } else {
                    failure_probability = 0;
                }

                // середнє завантаження пристрою
                mean_load = p.getMeanLoad() / tcurr;

                global_mean_queue_length_accumulator += mean_queue_length;
                global_failure_probability_accumulator += failure_probability;
                global_mean_load_accumulator += mean_load;

                if (p.getMaxObservedQueue() > global_max_observed_queue_length) {
                    global_max_observed_queue_length = ((Process) e).getMaxObservedQueue();
                }


                System.out.println("Average queue length: " + mean_queue_length);
                System.out.println("Failure probability: " + failure_probability);
                System.out.println("Average load: " + mean_load);
                System.out.println();
            }
        }
        var global_mean_queue_length = global_mean_queue_length_accumulator / num_of_processors;
        var global_failure_probability = global_failure_probability_accumulator / num_of_processors;
        var global_mean_load = global_mean_load_accumulator / num_of_processors;

        System.out.println("Global max observed queue length: " + global_max_observed_queue_length);
        System.out.println("Global mean queue length: " + global_mean_queue_length);
        System.out.println("Global failure probability: " + global_failure_probability);
        System.out.println("Global mean load: " + global_mean_load);
    }
}