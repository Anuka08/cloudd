package Code;

import smile.clustering.KMeans;
import smile.clustering.PartitionClustering;
import java.util.ArrayList;

public class FeatureGrouping {
    
    // Feature Grouping: Replace FCM with K-Means clustering
    // Try K = 3 to 8, select best K by Silhouette score
    // Add each sample's cluster_id and distance_to_centroid as new features
    public static ArrayList<ArrayList<Double>> applyKMeansClustering(ArrayList<ArrayList<Double>> data, 
                                                                      int minK, int maxK) {
        System.out.println("\n// KMeans Grouping: Finding optimal K between " + minK + " and " + maxK + "...");
        // ✅ Speedup: sample data for clustering to avoid long runtime
        int maxRows = 8000; // you can try 5000–10000
        if (data.size() > maxRows) {
            data = new ArrayList<>(data.subList(0, maxRows));
            System.out.println("Sampling data for KMeans: using first " + maxRows + " rows out of " + data.size());
        }

        int numSamples = data.size();
        int numFeatures = data.get(0).size();
        
        double[][] dataArray = new double[numSamples][numFeatures];
        for (int i = 0; i < numSamples; i++) {
            for (int j = 0; j < numFeatures; j++) {
                dataArray[i][j] = data.get(i).get(j);
            }
        }
        
        double bestSilhouette = -1.0;
        int bestK = minK;
        KMeans bestKMeans = null;
        
        for (int k = minK; k <= maxK; k++) {
            KMeans kmeans = KMeans.fit(dataArray, k);
            double silhouette = calculateSilhouetteScore(dataArray, kmeans);
            
            System.out.println("K=" + k + ", Silhouette Score: " + String.format("%.4f", silhouette));
            
            if (silhouette > bestSilhouette) {
                bestSilhouette = silhouette;
                bestK = k;
                bestKMeans = kmeans;
            }
        }
        
        System.out.println("Best K=" + bestK + " with Silhouette Score: " + String.format("%.4f", bestSilhouette));
        
        ArrayList<ArrayList<Double>> groupedData = new ArrayList<>();
        
        for (int i = 0; i < numSamples; i++) {
            ArrayList<Double> newRow = new ArrayList<>(data.get(i));
            
            int clusterId = bestKMeans.predict(dataArray[i]);
            newRow.add((double) clusterId);
            
            double[] centroid = bestKMeans.centroids[clusterId];
            double distanceToCentroid = euclideanDistance(dataArray[i], centroid);
            newRow.add(distanceToCentroid);
            
            groupedData.add(newRow);
        }
        
        System.out.println("Added cluster_id and distance_to_centroid features. Total features: " + 
                          groupedData.get(0).size());
        
        return groupedData;
    }
    
    private static double calculateSilhouetteScore(double[][] data, KMeans kmeans) {
        int n = Math.min(data.length, 5000); // ✅ evaluate silhouette on max 5000
        double[][] sample = new double[n][];
        for (int i = 0; i < n; i++) sample[i] = data[i];

        int[] labels = new int[n];
        for (int i = 0; i < n; i++) {
            labels[i] = kmeans.predict(sample[i]);
        }

        double totalSilhouette = 0.0;
        for (int i = 0; i < n; i++) {
            double a = calculateAverageDistance(sample, i, labels[i], labels);
            double b = calculateMinAverageDistanceToOtherClusters(sample, i, labels[i], labels);
            totalSilhouette += (b - a) / Math.max(a, b);
        }

        return totalSilhouette / n;
    }

    
    private static double calculateAverageDistance(double[][] data, int index, int clusterId, int[] labels) {
        double sum = 0.0;
        int count = 0;
        
        for (int i = 0; i < data.length; i++) {
            if (i != index && labels[i] == clusterId) {
                sum += euclideanDistance(data[index], data[i]);
                count++;
            }
        }
        
        return count > 0 ? sum / count : 0.0;
    }
    
    private static double calculateMinAverageDistanceToOtherClusters(double[][] data, int index, 
                                                                      int clusterId, int[] labels) {
        ArrayList<Integer> otherClusters = new ArrayList<>();
        for (int label : labels) {
            if (label != clusterId && !otherClusters.contains(label)) {
                otherClusters.add(label);
            }
        }
        
        if (otherClusters.isEmpty()) {
            return 0.0;
        }
        
        double minAvgDist = Double.MAX_VALUE;
        
        for (int otherCluster : otherClusters) {
            double sum = 0.0;
            int count = 0;
            
            for (int i = 0; i < data.length; i++) {
                if (labels[i] == otherCluster) {
                    sum += euclideanDistance(data[index], data[i]);
                    count++;
                }
            }
            
            if (count > 0) {
                double avgDist = sum / count;
                if (avgDist < minAvgDist) {
                    minAvgDist = avgDist;
                }
            }
        }
        
        return minAvgDist;
    }
    
    private static double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}
