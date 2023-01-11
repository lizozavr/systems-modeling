import bank.CreateBank;
import bank.ModelBank;
import bank.ProcessBank;
import shared.*;
import shared.Process;
import hospital.CreateHospital;
import hospital.DisposeHospital;
import hospital.ModelHospital;
import hospital.ProcessHospital;

import java.util.ArrayList;

public class SimModel {

    public static void main(String[] args) {
        /* Uncomment needed model: */
//        baseProcessor();
//        bankProcessor();
//        hospitalProcessor();
    }

    public static void baseProcessor() {
        //Multiply channels, probability, priority
        Create c = new Create(5, 1);
        Process p1 = new Process(5, 1);
        Process p2 = new Process(5, 1);

        c.setNextElement(new Element[]{p1, p2}, null, new int[]{3, 1});

        p1.setMaxqueue(3);
        p2.setMaxqueue(3);

        c.setName("CREATOR");
        p1.setName("PROCESSOR1");
        p2.setName("PROCESSOR2");

        c.setDistribution("exp");
        p1.setDistribution("exp");
        p2.setDistribution("exp");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);
        Model model = new Model(list);
        model.simulate(1000.0);
    }

    public static void bankProcessor() {
        CreateBank c = new CreateBank(0.5, 1);
        ProcessBank p1 = new ProcessBank(0.3, 1);
        ProcessBank p2 = new ProcessBank(0.3, 1);

        c.setNextElement(new Element[]{p1, p2}, null, null);

        p1.setMaxqueue(3);
        p2.setMaxqueue(3);

        c.setName("CREATOR");
        p1.setName("CASH1");
        p2.setName("CASH2");

        c.setDistribution("exp");
        p1.setDistribution("exp");
        p2.setDistribution("exp");

        p1.setStateOnIndex(0, 1);
        p2.setStateOnIndex(0, 1);
        p1.setTnextOnIndex(0, FunRand.Norm(1, 0.3));
        p2.setTnextOnIndex(0, FunRand.Norm(1, 0.3));
        c.setTnextOnIndex(0, 0.1);

        p1.setQueue(2);
        p2.setQueue(2);

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);
        ModelBank model = new ModelBank(list, new ProcessBank[]{p1, p2});
        model.simulate(1000.0);
    }

    public static void hospitalProcessor() {
        CreateHospital c = new CreateHospital(15.0, 1, new int[]{50, 10, 40});
        ProcessHospital p1 = new ProcessHospital(0, 2);

        ProcessHospital p2 = new ProcessHospital(3, 3);
        p2.setDelayDev(8);

        ProcessHospital p3 = new ProcessHospital(2, 10);
        p3.setDelayDev(5);

        ProcessHospital p4 = new ProcessHospital(4.5, 1);
        p4.setDelayDev(3);

        ProcessHospital p5 = new ProcessHospital(4, 1);
        p5.setDelayDev(2);

        ProcessHospital p6 = new ProcessHospital(2, 10);
        p6.setDelayDev(5);

        c.setNextElement(new Element[]{p1, p2}, new int[]{50, 50}, null);
        DisposeHospital d1 = new DisposeHospital(0, 1);
        d1.setName("EXIT1");

        DisposeHospital d2 = new DisposeHospital(0, 1);
        d2.setName("EXIT2");

        p1.setMaxqueue(100);
        p2.setMaxqueue(100);
        p3.setMaxqueue(0);
        p4.setMaxqueue(100);
        p5.setMaxqueue(100);
        p6.setMaxqueue(0);

        c.setName("CREATE");
        p1.setName("RECEPTION");
        p2.setName("GO_TO_THE_WARD");
        p3.setName("GO_TO_THE_LAB_RECEPTION");
        p4.setName("LAB_REGISTRY");
        p5.setName("EXAMINATION");
        p6.setName("GO_TO_THE_RECEPTION");

        c.setDistribution("exp");
        p1.setDistribution("exp");
        p2.setDistribution("unif");
        p3.setDistribution("unif");
        p4.setDistribution("erlang");
        p5.setDistribution("erlang");
        p6.setDistribution("unif");

        c.setNextElement(new Element[]{p1}, new int[]{100}, null);
        p1.setNextElement(new Element[]{p2, p3}, new int[]{50, 50}, null);
        p2.setNextElement(new Element[]{d1}, new int[]{100}, null);
        p3.setNextElement(new Element[]{p4}, new int[]{100}, null);
        p4.setNextElement(new Element[]{p5}, new int[]{100}, null);
        p5.setNextElement(new Element[]{d2, p6}, new int[]{50, 50}, null);
        p6.setNextElement(new Element[]{p1}, new int[]{100}, null);
        p1.setPriorTypes(new int[]{1});

        p1.setRequiredPath(new int[][]{new int[]{1}, new int[]{2, 3}});
        p5.setRequiredPath(new int[][]{new int[]{3}, new int[]{2}});
        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        list.add(p6);
        list.add(d1);
        list.add(d2);
        ModelHospital model = new ModelHospital(list);
        model.simulate(1000.0);
    }
}
