package hospital;

import java.util.Arrays;

public class DisposeHospital extends ElementHospital {
    double delta_t_finished1, delta_t_finished2, delta_t_finished3;
    double type1_cnt, type2_cnt, type3_cnt;

    public DisposeHospital(double delay, int channels) {
        super(delay, channels);
        this.delta_t_finished1 = 0;
        this.delta_t_finished2 = 0;
        this.delta_t_finished3 = 0;
        this.type1_cnt = 0;
        this.type2_cnt = 0;
        this.type3_cnt = 0;
        var currentTnext = getTnext();
        Arrays.fill(currentTnext, Double.MAX_VALUE);
        setTnext(currentTnext);
    }

    @Override
    public void inAct(int next_type_element, double t_start) {
        if (next_type_element == 1) {
            delta_t_finished1 += getTcurr() - t_start;
            type1_cnt++;
        } else if (next_type_element == 2) {
            delta_t_finished2 += getTcurr() - t_start;
            type2_cnt++;
        } else if (next_type_element == 3) {
            delta_t_finished3 += getTcurr() - t_start;
            type3_cnt++;
        }
        super.outAct();
    }
}
