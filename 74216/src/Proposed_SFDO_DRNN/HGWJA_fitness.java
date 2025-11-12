package Proposed_SFDO_DRNN;

import static Proposed_SFDO_DRNN.HGWJA_update.acc;
import static Proposed_SFDO_DRNN.HGWJA_update.dr;
import static Proposed_SFDO_DRNN.HGWJA_update.fpr;

import java.util.ArrayList;

public class HGWJA_fitness {
    
    public static ArrayList<Double> func(ArrayList<ArrayList<Double>> solution, ArrayList<ArrayList<Double>> feature, ArrayList<Double> clas) throws Exception {
        
        ArrayList<Double> fit = new ArrayList();
        for (int i = 0; i <solution.size(); i++) {
            ArrayList<Double> fit_metrics = Proposed_SFDO_DRNN.DRNN.callmain(feature, clas, solution.get(i));
            fit.add(1.0 - fit_metrics.get(0));
            acc.add(fit_metrics.get(0)); dr.add(fit_metrics.get(1)); fpr.add(fit_metrics.get(2));
        }
        return fit;
    }
}
