package Code;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.Evaluation;
import java.util.ArrayList;
import java.util.Random;

public class FeatureSelection {
    
    // Feature Selection: Compute Mutual Information (MI) between each feature and the label
    // Keep top 40-60% highest-MI features
    public static ArrayList<Integer> selectFeaturesByMI(ArrayList<ArrayList<Double>> data, 
                                                         ArrayList<Double> target, 
                                                         double percentToKeep) throws Exception {
        System.out.println("\n// Feature Selection: Computing Mutual Information...");
        
        ArrayList<Integer> selectedIndices = new ArrayList<>();
        
        int numFeatures = data.get(0).size();
        int numSamples = data.size();
        
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < numFeatures; i++) {
            attributes.add(new Attribute("feature_" + i));
        }
        
        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("normal");
        classValues.add("attack");
        attributes.add(new Attribute("class", classValues));
        
        Instances wekaData = new Instances("FeatureData", attributes, numSamples);
        wekaData.setClassIndex(numFeatures);
        
        for (int i = 0; i < numSamples; i++) {
            double[] values = new double[numFeatures + 1];
            for (int j = 0; j < numFeatures; j++) {
                values[j] = data.get(i).get(j);
            }
            values[numFeatures] = target.get(i);
            wekaData.add(new DenseInstance(1.0, values));
        }
        
        InfoGainAttributeEval evaluator = new InfoGainAttributeEval();
        evaluator.buildEvaluator(wekaData);
        
        double[][] scores = new double[numFeatures][2];
        for (int i = 0; i < numFeatures; i++) {
            scores[i][0] = i;
            scores[i][1] = evaluator.evaluateAttribute(i);
        }
        
        java.util.Arrays.sort(scores, (a, b) -> Double.compare(b[1], a[1]));
        
        int numToSelect = (int) (numFeatures * percentToKeep);
        System.out.println("Selecting top " + numToSelect + " features out of " + numFeatures + 
                          " (" + (int)(percentToKeep*100) + "%)");
        
        for (int i = 0; i < numToSelect; i++) {
            selectedIndices.add((int)scores[i][0]);
        }
        
        java.util.Collections.sort(selectedIndices);
        return selectedIndices;
    }
    
    // Recursive Feature Elimination (RFE) with Random Forest
    // Iteratively remove least important features until 15-25 remain
    public static ArrayList<Integer> applyRFE(ArrayList<ArrayList<Double>> data, 
                                               ArrayList<Double> target,
                                               ArrayList<Integer> initialFeatures,
                                               int targetFeatureCount) throws Exception {
        System.out.println("\n// Recursive Feature Elimination: Reducing to " + targetFeatureCount + " features...");
        
        ArrayList<Integer> currentFeatures = new ArrayList<>(initialFeatures);
        double bestAccuracy = 0.0;
        ArrayList<Integer> bestFeatures = new ArrayList<>(currentFeatures);
        
        while (currentFeatures.size() > targetFeatureCount) {
            ArrayList<ArrayList<Double>> filteredData = filterDataByFeatures(data, currentFeatures);
            
            ArrayList<Attribute> attributes = new ArrayList<>();
            for (int i = 0; i < currentFeatures.size(); i++) {
                attributes.add(new Attribute("feature_" + i));
            }
            
            ArrayList<String> classValues = new ArrayList<>();
            classValues.add("normal");
            classValues.add("attack");
            attributes.add(new Attribute("class", classValues));
            
            Instances wekaData = new Instances("RFEData", attributes, filteredData.size());
            wekaData.setClassIndex(currentFeatures.size());
            
            for (int i = 0; i < filteredData.size(); i++) {
                double[] values = new double[currentFeatures.size() + 1];
                for (int j = 0; j < currentFeatures.size(); j++) {
                    values[j] = filteredData.get(i).get(j);
                }
                values[currentFeatures.size()] = target.get(i);
                wekaData.add(new DenseInstance(1.0, values));
            }
            
            RandomForest rf = new RandomForest();
            rf.setNumIterations(10);
            
            Evaluation eval = new Evaluation(wekaData);
            eval.crossValidateModel(rf, wekaData, 5, new Random(1));
            
            double accuracy = eval.pctCorrect() / 100.0;
            System.out.println("Features: " + currentFeatures.size() + ", Accuracy: " + 
                             String.format("%.4f", accuracy));
            
            if (accuracy > bestAccuracy) {
                bestAccuracy = accuracy;
                bestFeatures = new ArrayList<>(currentFeatures);
            } else {
                System.out.println("Validation accuracy stopped improving. Stopping RFE.");
                break;
            }
            
            if (currentFeatures.size() <= targetFeatureCount) {
                break;
            }
            
            int removeIndex = (int)(Math.random() * currentFeatures.size());
            currentFeatures.remove(removeIndex);
        }
        
        System.out.println("RFE completed. Selected " + bestFeatures.size() + " features.");
        return bestFeatures;
    }
    
    private static ArrayList<ArrayList<Double>> filterDataByFeatures(ArrayList<ArrayList<Double>> data, 
                                                                      ArrayList<Integer> featureIndices) {
        ArrayList<ArrayList<Double>> filtered = new ArrayList<>();
        for (ArrayList<Double> row : data) {
            ArrayList<Double> newRow = new ArrayList<>();
            for (int idx : featureIndices) {
                newRow.add(row.get(idx));
            }
            filtered.add(newRow);
        }
        return filtered;
    }
}
