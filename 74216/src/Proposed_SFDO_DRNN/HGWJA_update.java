package Proposed_SFDO_DRNN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HGWJA_update {
    
    public static ArrayList<Double> acc = new ArrayList();
    public static ArrayList<Double> dr = new ArrayList();
    public static ArrayList<Double> fpr = new ArrayList();
    
    public static void optimization(ArrayList<ArrayList<Double>> feature, ArrayList<Double> clas) throws Exception {
   
        acc = new ArrayList();
        dr = new ArrayList();
        fpr = new ArrayList();
        
        int NP = 10, D = 5, Max = 15, epoch = 0;
        
        ArrayList<ArrayList<Double>> Wolves = generate_soln(NP, D);
        ArrayList<Double> Fitness = HGWJA_fitness.func(Wolves, feature, clas);
        
        ArrayList<Double> Alpha_wolf = new ArrayList();
        ArrayList<Double> Beta_wolf = new ArrayList();
        ArrayList<Double> Delta_wolf = new ArrayList();
        double Alpha_score = Double.MAX_VALUE;
        double Beta_score = Double.MAX_VALUE;
        double Delta_score = Double.MAX_VALUE;
        
        for (int i = 0; i < Wolves.size(); i++) {
            double fit = Fitness.get(i);
            if (fit < Alpha_score) {
                Delta_score = Beta_score;
                Delta_wolf = (ArrayList<Double>) Beta_wolf.clone();
                Beta_score = Alpha_score;
                Beta_wolf = (ArrayList<Double>) Alpha_wolf.clone();
                Alpha_score = fit;
                Alpha_wolf = (ArrayList<Double>) Wolves.get(i).clone();
            } else if (fit < Beta_score) {
                Delta_score = Beta_score;
                Delta_wolf = (ArrayList<Double>) Beta_wolf.clone();
                Beta_score = fit;
                Beta_wolf = (ArrayList<Double>) Wolves.get(i).clone();
            } else if (fit < Delta_score) {
                Delta_score = fit;
                Delta_wolf = (ArrayList<Double>) Wolves.get(i).clone();
            }
        }
        
        System.out.println("\nPlease wait it will take some time(nearly 30 mins)...");
        while (epoch < Max) {
            double a = 2.0 - epoch * (2.0 / Max);
            
            for (int i = 0; i < Wolves.size(); i++) {
                ArrayList<Double> wolf = Wolves.get(i);
                ArrayList<Double> new_wolf = new ArrayList();
                
                for (int j = 0; j < D; j++) {
                    double r1 = Math.random();
                    double r2 = Math.random();
                    double A1 = 2.0 * a * r1 - a;
                    double C1 = 2.0 * r2;
                    double D_alpha = Math.abs(C1 * Alpha_wolf.get(j) - wolf.get(j));
                    double X1 = Alpha_wolf.get(j) - A1 * D_alpha;
                    
                    r1 = Math.random();
                    r2 = Math.random();
                    double A2 = 2.0 * a * r1 - a;
                    double C2 = 2.0 * r2;
                    double D_beta = Math.abs(C2 * Beta_wolf.get(j) - wolf.get(j));
                    double X2 = Beta_wolf.get(j) - A2 * D_beta;
                    
                    r1 = Math.random();
                    r2 = Math.random();
                    double A3 = 2.0 * a * r1 - a;
                    double C3 = 2.0 * r2;
                    double D_delta = Math.abs(C3 * Delta_wolf.get(j) - wolf.get(j));
                    double X3 = Delta_wolf.get(j) - A3 * D_delta;
                    
                    double gwo_position = (X1 + X2 + X3) / 3.0;
                    
                    int best_idx = Fitness.indexOf(Collections.min(Fitness));
                    int worst_idx = Fitness.indexOf(Collections.max(Fitness));
                    double best_val = Wolves.get(best_idx).get(j);
                    double worst_val = Wolves.get(worst_idx).get(j);
                    
                    double r_jaya1 = Math.random();
                    double r_jaya2 = Math.random();
                    double jaya_position = wolf.get(j) + r_jaya1 * (best_val - Math.abs(wolf.get(j))) 
                                          - r_jaya2 * (worst_val - Math.abs(wolf.get(j)));
                    
                    double hybrid_position = 0.6 * gwo_position + 0.4 * jaya_position;
                    
                    hybrid_position = Math.max(0.0, Math.min(1.0, hybrid_position));
                    new_wolf.add(hybrid_position);
                }
                Wolves.set(i, new_wolf);
            }
            
            Fitness = HGWJA_fitness.func(Wolves, feature, clas);
            
            for (int i = 0; i < Wolves.size(); i++) {
                double fit = Fitness.get(i);
                if (fit < Alpha_score) {
                    Delta_score = Beta_score;
                    Delta_wolf = (ArrayList<Double>) Beta_wolf.clone();
                    Beta_score = Alpha_score;
                    Beta_wolf = (ArrayList<Double>) Alpha_wolf.clone();
                    Alpha_score = fit;
                    Alpha_wolf = (ArrayList<Double>) Wolves.get(i).clone();
                } else if (fit < Beta_score) {
                    Delta_score = Beta_score;
                    Delta_wolf = (ArrayList<Double>) Beta_wolf.clone();
                    Beta_score = fit;
                    Beta_wolf = (ArrayList<Double>) Wolves.get(i).clone();
                } else if (fit < Delta_score) {
                    Delta_score = fit;
                    Delta_wolf = (ArrayList<Double>) Wolves.get(i).clone();
                }
            }
            epoch++;
        }
        
        Code.Run.Accuracy.add(avg(acc));
        Code.Run.Detection_Rate.add(avg(dr));
        Code.Run.FPR.add(avg(fpr));
    }

    private static ArrayList<ArrayList<Double>> generate_soln(int NP, int D) {
        ArrayList<ArrayList<Double>> data = new ArrayList<>();
        for (int i = 0; i < NP; i++) {
            ArrayList<Double> tem = new ArrayList();
            for (int j = 0; j < D; j++) {
                tem.add(Math.random());
            }
            data.add(tem);
        }
        return data;
    }
    
    private static double avg(ArrayList<Double> data) {
        double summ = 0;
        for (int i = 0; i < data.size(); i++) {
            summ += data.get(i);
        }
        return summ/data.size();
    }
}
