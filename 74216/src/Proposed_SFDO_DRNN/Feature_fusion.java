package Proposed_SFDO_DRNN;

import static Code.Run.Data;
import static Code.Run.target;
import static Code.Run.Feature;
import static Code.Run.Feature_HGWJA;
import java.io.IOException;
import java.util.ArrayList;

public class Feature_fusion {
        
    public static void process() throws IOException, Exception {
        
        int n_cluster = Code.Run.Group_size;
        System.out.println("\nReading data..");
        Code.read.dataset();
        
        // ============= DUAL FEATURE PROCESSING PIPELINE =============
        System.out.println("\n=== DUAL PIPELINE ARCHITECTURE ===");
        System.out.println("Pipeline 1: FCM-based features for FCM-ANN, ANFIS, SVM, SFDO-DRNN");
        System.out.println("Pipeline 2: Advanced features (Autoencoder + K-Means) for H-GWJA-DRNN");
        System.out.println("=====================================================\n");
        
        // PIPELINE 1: Original FCM-based feature processing for first 4 algorithms
        System.out.println("\n[Pipeline 1] Processing FCM-based features...");
        ArrayList<Integer> Clustered = Code.FCM.group(Data, n_cluster);
        
        Feature = new ArrayList<>();
        for (int i = 0; i < Data.size(); i++) {
            ArrayList<Double> feature = new ArrayList();
            for (int j = 0; j < n_cluster; j++) {
                double ai = (double) n_cluster;
                double fused_data = fuse_feature(Data.get(i), Clustered, j+1, ai);
                feature.add(fused_data);
            }
            Feature.add(feature);
        }
        
        Code.write.write_double_csv(Feature, "Processed\\Fused_Feature_FCM.csv");
        System.out.println("FCM-based features saved to Processed/Fused_Feature_FCM.csv");
        System.out.println("Features for algorithms: FCM-ANN, ANFIS, SVM, SFDO-DRNN");
        
        // PIPELINE 2: Advanced feature processing ONLY for H-GWJA
        System.out.println("\n[Pipeline 2] Processing Advanced features for H-GWJA...");
        System.out.println("  1. Feature Selection (Mutual Information + RFE)");
        System.out.println("  2. Feature Embedding (Autoencoder)");
        System.out.println("  3. Feature Grouping (K-Means clustering)");
        
        Feature_HGWJA = Code.FeaturePipeline.processFeatures(Data, target);
        
        Code.write.write_double_csv(Feature_HGWJA, "Processed\\Fused_Feature_HGWJA.csv");
        System.out.println("\nAdvanced features saved to Processed/Fused_Feature_HGWJA.csv");
        System.out.println("Features for algorithm: H-GWJA-DRNN (Proposed)\n");
        
        // ============= ALGORITHM EXECUTION =============
        // Run BOTH optimization algorithms with their respective features
        
        // 1. SFDO Algorithm with FCM features (for comparison)
        System.out.println("\n[1/2] Running SFDO-based DRNN with FCM features...");
        SailFish_update.optimization(Feature, target);
        
        // 2. H-GWJA Algorithm with Advanced features (PROPOSED METHOD)
        System.out.println("\n[2/2] Running H-GWJA (Hybrid Grey Wolf + JAYA) with Advanced features...");
        HGWJA_update.optimization(Feature_HGWJA, target);
    }
    
    // Feature fusion
    private static double fuse_feature(ArrayList<Double> F, ArrayList<Integer> Clustered, int cluster_n, double ai) {
        
        double fused_data =  0;
        for (int i = 0; i < Clustered.size(); i++) {
            if (Clustered.get(i)==cluster_n) {          // if feature attribute in current cluster
                fused_data += ((1.0/ai) * F.get(i));    // formula for feature fusion 
            }
        }
        return fused_data;
    }
}
