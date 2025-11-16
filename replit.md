# Overview

This is a cloud computing intrusion detection system that combines machine learning and cloud simulation. The project implements a hybrid optimization algorithm (H-GWJA: Hybrid Grey Wolf and JAYA Algorithm) with Deep Recurrent Neural Networks (DRNN) for network intrusion detection in cloud environments. It uses CloudSim for cloud infrastructure simulation and includes VM load balancing, task scheduling, and feature processing pipelines for big data analysis.

The system features a **DUAL PIPELINE ARCHITECTURE**:
- **Pipeline 1 (FCM-based)**: Used by FCM-ANN, ANFIS, SVM, and SFDO-DRNN algorithms
- **Pipeline 2 (Advanced)**: Uses Autoencoder + Mutual Information + K-Means clustering, exclusively for H-GWJA-DRNN (proposed algorithm)

The system processes network traffic data through different feature processing stages before applying machine learning classifiers to detect intrusions. It was originally developed in NetBeans 8.1 for Windows 10 with Java 8.

# User Preferences

Preferred communication style: Simple, everyday language.

# System Architecture

## GUI Layer
- **Swing-based Desktop Interface**: Main entry point through `GUI.java` with form-based user input for configuration parameters (group size, training data percentage)
- **Event-Driven Architecture**: AWT event queue handles user interactions and triggers backend processing
- **Originally designed for Windows 10 desktop environment**: Uses NetBeans AbsoluteLayout for GUI components

## Processing Pipeline

### Feature Processing (Multi-Stage)
1. **Feature Selection**: Mutual Information + Recursive Feature Elimination (RFE) to identify relevant features from network traffic data
2. **Feature Embedding**: Autoencoder-based dimensionality reduction for high-dimensional feature spaces
3. **Feature Grouping**: K-Means clustering replaces older FCM (Fuzzy C-Means) approach
4. **Rationale**: This pipeline reduces computational overhead while maintaining detection accuracy for big data scenarios

### Optimization Layer - **COMPARISON MODE ACTIVE**
- **System runs BOTH algorithms for performance comparison**:
  1. **SFDO (Original)**: Sailfish-Dolphin Optimizer with 2 agents, 10 iterations
  2. **H-GWJA (Proposed)**: Hybrid Grey Wolf + JAYA Algorithm with 10 agents, 15 iterations

- **H-GWJA Hybrid Algorithm**: Combines Grey Wolf Optimizer (60%) with JAYA algorithm (40%)
  - **GWO Component**: Hierarchical hunting strategy with Alpha/Beta/Delta wolf roles for exploitation
  - **JAYA Component**: Moves solutions toward global best and away from worst solutions
  - **Population**: 10 agents, 15 iterations maximum (vs SFDO: 2 agents, 10 iterations)
  - **Fitness Function**: Evaluates solutions using DRNN loss/accuracy metrics
  - **Implementation Files**: `HGWJA_update.java`, `HGWJA_fitness.java` (November 2025)
  - **Both algorithms enabled** in `Feature_fusion.java` - runs sequentially for side-by-side comparison

### Machine Learning Classifiers (5 Total)
1. **FCM_ANN**: Fuzzy C-Means with Artificial Neural Network integration
2. **ANFIS**: Adaptive Neuro-Fuzzy Inference System (hybrid neural networks + fuzzy logic)
3. **SVM**: Support Vector Machine classifier
4. **SFDO-DRNN**: Original Sailfish-Dolphin Optimizer + Deep Recurrent Neural Network
5. **H-GWJA-DRNN (Proposed)**: Hybrid Grey Wolf-JAYA + Deep Recurrent Neural Network

**Output Display**: GUI table and charts show all 5 algorithms side-by-side for direct performance comparison

## Cloud Simulation Infrastructure

### CloudSim Framework
- **VM Management**: Virtual machine initialization, parameter generation, task assignment
- **Load Balancing**: Dynamic load calculation and optimization across VMs
- **Task Scheduling**: Distributes intrusion detection workload across cloud resources
- **Simulation Stats**: GridSim statistical tracking for performance metrics

### Design Decision
CloudSim was chosen over real cloud deployment for:
- **Cost-effective experimentation**: No cloud provider fees
- **Reproducible results**: Controlled simulation environment
- **Scalability testing**: Simulate large-scale deployments without infrastructure

## Data Processing

### Input Data
- **Network traffic datasets**: 370,443 samples with 34 features per sample
- **Training/Test Split**: Configurable percentage (default 80% training)
- **Format**: CSV/text-based numerical data

### Processing Flow
1. Data loading and preprocessing
2. Feature selection pipeline
3. Optimization-based feature fusion (H-GWJA)
4. DRNN classification
5. Performance metric calculation (accuracy, detection rate, false positive rate)

# External Dependencies

## Core Java Libraries
- **JDK 1.8 (Java 8)**: Runtime environment requirement
- **Swing/AWT**: GUI framework (javax.swing, java.awt packages)
- **Currently using GraalVM 22.3**: Java 8-compatible runtime on Replit

## Cloud Simulation
- **cloudanalyst.jar** (1.9MB): CloudSim cloud simulation framework
- **gridsim.jar** (305KB): Grid computing simulation engine
- **simjava2.jar** (123KB): Discrete event simulation foundation

## Machine Learning & Data Science
- **weka.jar** (9.4MB): Weka machine learning library for classification algorithms
- **smile-core.jar** (627KB): Statistical Machine Intelligence and Learning Engine (clustering)
- **smile-math.jar** (497KB): Mathematical utilities for Smile library
- **dl4j-core.jar** (75KB): DeepLearning4J framework for DRNN implementation
- **nd4j-api.jar** (6.8MB): N-dimensional array processing for deep learning
- **nd4j-native-platform.jar** (3KB): Native platform bindings for ND4J

## Visualization & Reporting
- **jfreechart-1.0.19.jar** (1.5MB): Chart generation for performance visualization
- **jcommon-1.0.23.jar** (323KB): Common utilities for JFreeChart
- **iText-2.1.5.jar** (1.1MB): PDF report generation

## GUI Components
- **AbsoluteLayout.jar** (11KB): NetBeans-specific layout manager for GUI positioning

## Known Environment Constraints
- **Font Configuration Issue**: Java AWT font system fails in Replit's Linux VNC environment
  - Original development on Windows 10 desktop with native font support
  - VNC headless environment lacks proper fontconfig initialization
  - Error: `NullPointerException in sun.awt.FontConfiguration.head`
  - Attempted fixes: Metal Look & Feel, font cache rebuild, custom font configs (unsuccessful)
  - **Impact**: GUI cannot initialize, but core logic and compilation are functional

## Build System
- **Custom shell scripts**: `compile.sh` and `run.sh` manage compilation and execution
- **No Maven/Gradle**: Original NetBeans project uses manual classpath configuration
- **All 13 JAR libraries**: Configured in classpath with colon-separated paths