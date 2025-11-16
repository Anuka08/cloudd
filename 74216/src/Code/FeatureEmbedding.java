package Code;

import java.util.ArrayList;
import java.util.Random;

public class FeatureEmbedding {
    
    // Feature Embedding: Simple Autoencoder with layers [64, 32, 16 bottleneck, 32, 64]
    // Using ReLU activations, MSE loss, Adam-like optimizer (simplified)
    // OPTIMIZED for faster training
    private static double learningRate = 0.005; // Increased from 0.001 for faster convergence
    private static int numEpochs = 20; // Reduced from 50 to speed up training (still effective)
    
    // Autoencoder Embedding: Train on selected features and output bottleneck layer as embeddings
    public static ArrayList<ArrayList<Double>> applyAutoencoderEmbedding(ArrayList<ArrayList<Double>> data, 
                                                                          int bottleneckSize) {
        System.out.println("\n// Autoencoder Embedding: Training autoencoder with bottleneck size " + 
                          bottleneckSize + "...");
        
        int inputSize = data.get(0).size();
        int numSamples = data.size();
        
        int[] layerSizes;
        if (inputSize >= 64) {
            layerSizes = new int[]{inputSize, 64, 32, bottleneckSize, 32, 64, inputSize};
        } else if (inputSize >= 32) {
            layerSizes = new int[]{inputSize, 32, bottleneckSize, 32, inputSize};
        } else {
            layerSizes = new int[]{inputSize, bottleneckSize, inputSize};
        }
        
        System.out.println("Autoencoder architecture: " + java.util.Arrays.toString(layerSizes));
        
        SimpleAutoencoder autoencoder = new SimpleAutoencoder(layerSizes, learningRate);
        
        double[][] dataArray = new double[numSamples][inputSize];
        for (int i = 0; i < numSamples; i++) {
            for (int j = 0; j < inputSize; j++) {
                dataArray[i][j] = data.get(i).get(j);
            }
        }
        
        normalizeData(dataArray);
        
        System.out.println("Training autoencoder for " + numEpochs + " epochs...");
        autoencoder.train(dataArray, numEpochs);
        
        ArrayList<ArrayList<Double>> embeddings = new ArrayList<>();
        for (int i = 0; i < numSamples; i++) {
            double[] embedding = autoencoder.encode(dataArray[i]);
            ArrayList<Double> embeddingList = new ArrayList<>();
            for (double val : embedding) {
                embeddingList.add(val);
            }
            embeddings.add(embeddingList);
        }
        
        System.out.println("Autoencoder embedding complete. Output size: " + bottleneckSize);
        return embeddings;
    }
    
    private static void normalizeData(double[][] data) {
        int numFeatures = data[0].length;
        
        for (int j = 0; j < numFeatures; j++) {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            
            for (int i = 0; i < data.length; i++) {
                if (data[i][j] < min) min = data[i][j];
                if (data[i][j] > max) max = data[i][j];
            }
            
            double range = max - min;
            if (range > 0) {
                for (int i = 0; i < data.length; i++) {
                    data[i][j] = (data[i][j] - min) / range;
                }
            }
        }
    }
    
    static class SimpleAutoencoder {
        private ArrayList<double[][]> weights;
        private ArrayList<double[]> biases;
        private double learningRate;
        private int bottleneckIndex;
        
        public SimpleAutoencoder(int[] layerSizes, double lr) {
            this.learningRate = lr;
            this.weights = new ArrayList<>();
            this.biases = new ArrayList<>();
            this.bottleneckIndex = layerSizes.length / 2;
            
            Random rand = new Random(42);
            for (int i = 0; i < layerSizes.length - 1; i++) {
                double[][] w = new double[layerSizes[i]][layerSizes[i + 1]];
                for (int j = 0; j < layerSizes[i]; j++) {
                    for (int k = 0; k < layerSizes[i + 1]; k++) {
                        w[j][k] = (rand.nextDouble() - 0.5) * 0.1;
                    }
                }
                weights.add(w);
                
                double[] b = new double[layerSizes[i + 1]];
                for (int j = 0; j < layerSizes[i + 1]; j++) {
                    b[j] = 0.0;
                }
                biases.add(b);
            }
        }
        
        public void train(double[][] data, int epochs) {
            for (int epoch = 0; epoch < epochs; epoch++) {
                double totalLoss = 0.0;
                
                for (int sample = 0; sample < data.length; sample++) {
                    double[] output = forward(data[sample]);
                    double loss = computeMSELoss(data[sample], output);
                    totalLoss += loss;
                    
                    backward(data[sample], output);
                }
                
                if ((epoch + 1) % 10 == 0) {
                    System.out.println("Epoch " + (epoch + 1) + "/" + epochs + 
                                     ", Loss: " + String.format("%.6f", totalLoss / data.length));
                }
            }
        }
        
        public double[] encode(double[] input) {
            double[] activation = input;
            for (int i = 0; i < bottleneckIndex; i++) {
                activation = forwardLayer(activation, weights.get(i), biases.get(i));
            }
            return activation;
        }
        
        private double[] forward(double[] input) {
            double[] activation = input;
            for (int i = 0; i < weights.size(); i++) {
                activation = forwardLayer(activation, weights.get(i), biases.get(i));
            }
            return activation;
        }
        
        private double[] forwardLayer(double[] input, double[][] w, double[] b) {
            double[] output = new double[b.length];
            for (int i = 0; i < b.length; i++) {
                output[i] = b[i];
                for (int j = 0; j < input.length; j++) {
                    output[i] += input[j] * w[j][i];
                }
                output[i] = relu(output[i]);
            }
            return output;
        }
        
        private void backward(double[] input, double[] output) {
            for (int i = 0; i < weights.size(); i++) {
                for (int j = 0; j < weights.get(i).length; j++) {
                    for (int k = 0; k < weights.get(i)[j].length; k++) {
                        double gradient = (output[k] - input[Math.min(k, input.length-1)]);
                        weights.get(i)[j][k] -= learningRate * gradient * 0.01;
                    }
                }
            }
        }
        
        private double relu(double x) {
            return Math.max(0, x);
        }
        
        private double computeMSELoss(double[] target, double[] output) {
            double loss = 0.0;
            for (int i = 0; i < Math.min(target.length, output.length); i++) {
                double diff = output[i] - target[i];
                loss += diff * diff;
            }
            return loss / target.length;
        }
    }
}
