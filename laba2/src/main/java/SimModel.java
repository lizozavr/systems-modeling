import java.util.ArrayList;

public class SimModel {

    public static void main(String[] args) {
        //Realization 1
        /*Create c = new Create(5);
        Process p1 = new Process(5, 1);
        System.out.println("id0 = " + c.getId() + "   id1=" + p1.getId());
        c.setNextElement(new Element[]{p1});
        p1.setMaxqueue(5);

        c.setName("CREATOR");
        p1.setName("PROCESSOR1");

        c.setDistribution("exp");
        p1.setDistribution("exp");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        Model model = new Model(list);
        model.simulate(1000.0);*/

        //Realization according to the task
        /*Create c = new Create(5);
        Process p1 = new Process(5, 1);
        Process p2 = new Process(5, 1);
        Process p3 = new Process(5,1);

        c.setNextElement(new Element[]{p1});
        p1.setNextElement(new Element[]{p2}, new int[]{100});
        p2.setNextElement(new Element[]{p3}, new int[]{100});

        p1.setMaxqueue(5);
        p2.setMaxqueue(5);
        p3.setMaxqueue(1);

        c.setName("CREATOR");
        p1.setName("PROCESSOR1");
        p2.setName("PROCESSOR2");
        p3.setName("PROCESSOR3");

        c.setDistribution("exp");
        p1.setDistribution("exp");
        p2.setDistribution("exp");
        p3.setDistribution("exp");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);
        list.add(p3);
        Model model = new Model(list);
        model.simulate(1000.0);*/

        //Realization with channels
         /*Create c = new Create(5);
        Process p1 = new Process(5,1);
        Process p2 = new Process(5,3);

        c.setNextElement(new Element[]{p1});
        p1.setNextElement(new Element[]{p2}, new int[]{100});
        p2.setNextElement(new Element[]{p1}, new int[]{100});

        p1.setMaxqueue(5);
        p2.setMaxqueue(5);

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
        model.simulate(1000.0); */

        //Realization with modified set/getNextElement method
        Create c = new Create(5);
        Process p1 = new Process(5, 1);
        Process p2 = new Process(5, 1);
        Process p3 = new Process(5,1);

        c.setNextElement(new Element[]{p1});
        p1.setNextElement(new Element[]{p2, p3}, new int[]{50, 50});

        p1.setMaxqueue(5);
        p2.setMaxqueue(5);
        p3.setMaxqueue(5);

        c.setName("CREATOR");
        p1.setName("PROCESSOR1");
        p2.setName("PROCESSOR2");
        p3.setName("PROCESSOR3");

        c.setDistribution("exp");
        p1.setDistribution("exp");
        p2.setDistribution("exp");
        p3.setDistribution("exp");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);
        list.add(p3);
        Model model = new Model(list);
        model.simulate(1000.0);
    }
}
