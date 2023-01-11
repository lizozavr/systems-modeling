package hospital;

import java.util.ArrayList;
import java.util.Random;

public class CreateHospital extends ElementHospital {
    private int[] typeProbability; //50,10,40

    public CreateHospital(double delay, int channels, int[] typeProbability) {
        super(delay, channels);
        this.typeProbability = typeProbability;
    }

    @Override
    public void outAct() {
        super.outAct();
        setTnextOnIndex(0, getTcurr() + getDelay());
        setNextTypeElement(getNextElementType());
        var nextEl = getNextElement();
        ((ElementHospital) nextEl).inAct(getNextTypeElement(), getTcurr());
    }

    public int getNextElementType() {
        int[] types = {1, 2, 3};
        if (this.typeProbability.length != types.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        var listOfProbabilities = new ArrayList<Integer>();
        for (int i = 0; i < typeProbability.length; i++) {
            for (int j = 0; j < typeProbability[i]; j++) {
                listOfProbabilities.add(types[i]);
            }
        }
        Random r = new Random();
        return listOfProbabilities.get(r.nextInt(100));
    }
}