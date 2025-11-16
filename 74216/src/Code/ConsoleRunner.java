package Code;

import java.util.Scanner;

public class ConsoleRunner {
    
    public static void main(String[] args) {
        try {
            System.out.println("=========================================================");
            System.out.println("  Cloud Intrusion Detection System - Console Mode");
            System.out.println("  Dual Pipeline Architecture:");
            System.out.println("    - FCM for: FCM-ANN, ANFIS, SVM, SFDO-DRNN");
            System.out.println("    - Advanced (Autoencoder+K-Means) for: H-GWJA-DRNN");
            System.out.println("=========================================================\n");
            
            Scanner scanner = new Scanner(System.in);
            
            // Get user inputs
            System.out.print("Enter Group Size (e.g., 5): ");
            GUI.gp = scanner.nextInt();
            
            System.out.print("Enter Training Data Percentage (e.g., 80): ");
            GUI.tr_per = scanner.nextInt();
            
            System.out.println("\n=========================================================");
            System.out.println("Starting Intrusion Detection Analysis...");
            System.out.println("=========================================================\n");
            
            // Initialize and run
            Run.callmain();
            
            // Display results in a formatted table
            System.out.println("\n=========================================================");
            System.out.println("                   RESULTS SUMMARY");
            System.out.println("=========================================================");
            System.out.println(String.format("%-20s | %-10s | %-15s | %-10s", 
                "Algorithm", "Accuracy", "Detection Rate", "FPR"));
            System.out.println("---------------------------------------------------------");
            
            String[] algorithms = {"FCM-ANN", "ANFIS", "SVM", "SFDO-DRNN", "H-GWJA-DRNN (Proposed)"};
            
            for (int i = 0; i < Run.Accuracy.size(); i++) {
                System.out.println(String.format("%-20s | %-10.4f | %-15.4f | %-10.4f",
                    algorithms[i],
                    Run.Accuracy.get(i),
                    Run.Detection_Rate.get(i),
                    Run.FPR.get(i)));
            }
            
            System.out.println("=========================================================\n");
            
            System.out.println("Analysis complete!");
            System.out.println("\nFeature files generated:");
            System.out.println("  - Processed/Fused_Feature_FCM.csv (for first 4 algorithms)");
            System.out.println("  - Processed/Fused_Feature_HGWJA.csv (for H-GWJA-DRNN)");
            System.out.println("  - Processed/Selected_Features.csv");
            System.out.println("  - Processed/Embedded_Features.csv");
            System.out.println("  - Processed/Grouped_Features.csv");
            
            scanner.close();
            
        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
