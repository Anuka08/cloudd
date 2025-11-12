package Proposed_SFDO_DRNN;

import static Code.Run.Data;
import static Code.Run.target;
import static Code.Run.Feature;
import java.io.IOException;
import java.util.ArrayList;

public class Feature_fusion {
        
    public static void process() throws IOException, Exception {
        
        int n_cluster = Code.Run.Group_size;
        System.out.println("\nReading data..");
        Code.read.dataset();
        
        // NEW PIPELINE: Replace FCM with Feature Selection + Embedding + K-Means Grouping
        System.out.println("\n=== NEW FEATURE PROCESSING PIPELINE ===");
        System.out.println("Replacing FCM-based feature grouping with:");
        System.out.println("  1. Feature Selection (Mutual Information + RFE)");
        System.out.println("  2. Feature Embedding (Autoencoder)");
        System.out.println("  3. Feature Grouping (K-Means clustering)");
        
        Feature = Code.FeaturePipeline.processFeatures(Data, target);
        
        // Save the final combined features
        Code.write.write_double_csv(Feature, "Processed\\Fused_Feature.csv");
        System.out.println("\nFinal features saved to Processed/Fused_Feature.csv");
        
        // ============= ALGORITHM COMPARISON MODE =============
        // Run BOTH algorithms to compare performance
        
        // 1. SFDO Algorithm (Original)
        System.out.println("\n[1/2] Running SFDO-based DRNN for comparison...");
        SailFish_update.optimization(Feature, target);
        
        // 2. H-GWJA Algorithm (Hybrid Grey Wolf and JAYA - New Implementation)
        System.out.println("\n[2/2] Running H-GWJA (Hybrid Grey Wolf + JAYA) based DRNN...");
        HGWJA_update.optimization(Feature, target);
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
