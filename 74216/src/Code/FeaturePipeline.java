package Code;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;


public class FeaturePipeline {
    
    public static ArrayList<ArrayList<Double>> processFeatures(ArrayList<ArrayList<Double>> rawData, 
                                                                 ArrayList<Double> target) throws Exception {
        System.out.println("\n========================================");
        System.out.println("Starting OPTIMIZED Feature Processing Pipeline");
        System.out.println("========================================");
        
        System.out.println("Input data: " + rawData.size() + " samples, " + rawData.get(0).size() + " features");
        
        // PERFORMANCE OPTIMIZATION: Sample data early to reduce processing time
        int maxSamples = 5000; // Reduced from 370k+ to 5000 for faster processing
        ArrayList<ArrayList<Double>> sampledData = rawData;
        ArrayList<Double> sampledTarget = target;
        
        if (rawData.size() > maxSamples) {
            System.out.println("Sampling " + maxSamples + " samples for faster processing...");
            sampledData = new ArrayList<>(rawData.subList(0, maxSamples));
            sampledTarget = new ArrayList<>(target.subList(0, maxSamples));
        }
        
        // Step 1: Feature Selection (MI-based + RFE) - OPTIMIZED
        System.out.println("\n--- Step 1: Feature Selection (Optimized) ---");
        
        double miPercentage = 0.6; // Increased from 0.5 to select more features faster
        ArrayList<Integer> selectedIndicesMI = FeatureSelection.selectFeaturesByMI(sampledData, sampledTarget, miPercentage);
        
        ArrayList<ArrayList<Double>> dataAfterMI = filterDataByIndices(sampledData, selectedIndicesMI);
        
        int targetFeatureCount = Math.min(15, selectedIndicesMI.size() / 2); // Reduced feature count
        ArrayList<Integer> selectedIndicesRFE = FeatureSelection.applyRFE(dataAfterMI, sampledTarget, 
                                                                           getSequentialIndices(selectedIndicesMI.size()), 
                                                                           targetFeatureCount);
        
        ArrayList<Integer> finalSelectedIndices = new ArrayList<>();
        for (int idx : selectedIndicesRFE) {
            finalSelectedIndices.add(selectedIndicesMI.get(idx));
        }
        
        ArrayList<ArrayList<Double>> selectedData = filterDataByIndices(sampledData, finalSelectedIndices);
        
        saveToCSV(selectedData, "74216/Processed/Selected_Features.csv");
        System.out.println("Saved Selected_Features.csv with " + selectedData.get(0).size() + " features");
        
        // Step 2: Feature Embedding (Autoencoder) - OPTIMIZED
        System.out.println("\n--- Step 2: Feature Embedding (Optimized) ---");
        
        int bottleneckSize = 12; // Reduced from 16 for faster training
        ArrayList<ArrayList<Double>> embeddedData = FeatureEmbedding.applyAutoencoderEmbedding(selectedData, bottleneckSize);
        
        saveToCSV(embeddedData, "74216/Processed/Embedded_Features.csv");
        System.out.println("Saved Embedded_Features.csv with " + embeddedData.get(0).size() + " features");
        
        // Step 3: Feature Grouping (K-Means) - OPTIMIZED
        System.out.println("\n--- Step 3: Feature Grouping (Optimized) ---");
        
        int minK = 3;
        int maxK = 6; // Reduced from 8 to speed up clustering
        // Further sampling for K-Means (already sampled data, no need for more)
        
        ArrayList<ArrayList<Double>> groupedData = FeatureGrouping.applyKMeansClustering(embeddedData, minK, maxK);
        
        saveToCSV(groupedData, "74216/Processed/Grouped_Features.csv");
        System.out.println("Saved Grouped_Features.csv with " + groupedData.get(0).size() + " features");
        
        // Step 4: Combine all features
        System.out.println("\n--- Step 4: Combining Features ---");
        
        ArrayList<ArrayList<Double>> combinedData = new ArrayList<>();
        for (int i = 0; i < selectedData.size(); i++) {
            ArrayList<Double> combinedRow = new ArrayList<>();
            
            combinedRow.addAll(selectedData.get(i));
            combinedRow.addAll(embeddedData.get(i));
            combinedRow.addAll(groupedData.get(i));
            
            combinedData.add(combinedRow);
        }
        
        System.out.println("\n========================================");
        System.out.println("Pipeline Complete!");
        System.out.println("Final combined features: " + combinedData.get(0).size());
        System.out.println("========================================\n");
        
        return combinedData;
    }
    
    private static ArrayList<ArrayList<Double>> filterDataByIndices(ArrayList<ArrayList<Double>> data, 
                                                                      ArrayList<Integer> indices) {
        ArrayList<ArrayList<Double>> filtered = new ArrayList<>();
        for (ArrayList<Double> row : data) {
            ArrayList<Double> newRow = new ArrayList<>();
            for (int idx : indices) {
                newRow.add(row.get(idx));
            }
            filtered.add(newRow);
        }
        return filtered;
    }
    
    private static ArrayList<Integer> getSequentialIndices(int count) {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            indices.add(i);
        }
        return indices;
    }
    
    private static void saveToCSV(ArrayList<ArrayList<Double>> data, String filename) throws IOException {
        File file = new File(filename);
        file.getParentFile().mkdirs(); // ✅ auto-create folders

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for (ArrayList<Double> row : data) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < row.size(); i++) {
                sb.append(row.get(i));
                if (i < row.size() - 1) sb.append(",");
            }
            writer.write(sb.toString());
            writer.newLine();
        }
        writer.close();
    }

}
