package com.example.chatbot1.kb;

import com.example.chatbot1.model.Topic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnowledgeBase {

    private final Map<String, Topic> topicsMap;
    private final List<Topic> topicsList;

    public KnowledgeBase() {
        this.topicsMap = new HashMap<>();
        this.topicsList = new ArrayList<>();
        initKnowledgeBase();
    }

    private void addTopic(Topic topic) {
        topicsMap.put(topic.getId(), topic);
        topicsList.add(topic);
    }

    public Topic getTopic(String topicId) {
        return topicsMap.get(topicId);
    }

    public List<Topic> getAllTopics() {
        return topicsList;
    }

    private void initKnowledgeBase() {
        // ==========================================
        // 1. MACHINE LEARNING FUNDAMENTALS
        // ==========================================

        // Topic: What is Machine Learning?
        Topic ml = new Topic("what_is_ml", "Machine Learning", "Fundamentals");
        ml.addKeyword("machine learning");
        ml.addKeyword("ml");
        ml.addKeyword("what is ml");
        ml.addKeyword("what is machine learning");
        ml.addKeyword("define ml");
        ml.addKeyword("concept of ml");
        ml.setShortAnswer("Machine Learning (ML) is a branch of Artificial Intelligence that enables computer systems to learn statistical patterns from data and make accurate predictions without being explicitly programmed.");
        ml.setNormalAnswer("Machine Learning (ML) is a subset of Artificial Intelligence (AI) focused on building algorithms that learn and improve from historical data experience. Instead of hand-coding explicit rigid rules, programmers feed training datasets to ML models, enabling them to discover hidden statistical patterns and accurately predict outcomes on novel unseen data.\n\n"
                + "• **Key Goal**: Generalize from historical training data to make precise predictions on new inputs.\n"
                + "• **Core Loop**: Input Data ➔ Feature Extraction ➔ Model Training ➔ Metric Evaluation ➔ Prediction Inference.");
        ml.setWorking("1. **Data Collection**: Gather relevant data samples (e.g., historical sales, images, logs).\n"
                + "2. **Data Preprocessing**: Clean missing values, scale numerical features, encode categorical text.\n"
                + "3. **Feature Selection**: Identify input variables that strongly correlate with the target output.\n"
                + "4. **Model Selection & Training**: Feed features into an algorithm to mathematically optimize internal model parameters (weights/biases).\n"
                + "5. **Evaluation**: Measure precision, recall, and error on validation/test sets.\n"
                + "6. **Inference**: Deploy model to score real-time production queries.");
        ml.setDeepAnswer("### Comprehensive Guide to Machine Learning (ML)\n\n"
                + "#### 1. Definition\n"
                + "Machine Learning is a field of computer science that gives computers the capability to learn from data using mathematical and statistical techniques, improving task performance automatically with experience (Tom Mitchell's classic definition).\n\n"
                + "#### 2. How Machine Learning Works\n"
                + "1. **Data Collection**: Gathering relevant training samples (e.g., historical sales, medical records).\n"
                + "2. **Data Preprocessing**: Cleaning missing entries, scaling numerical attributes, and encoding categorical variables.\n"
                + "3. **Feature Selection**: Identifying variables that strongly correlate with the target outcome.\n"
                + "4. **Model Selection & Training**: Feeding features into an algorithm (e.g., Random Forest) to optimize model parameters.\n"
                + "5. **Evaluation**: Measuring accuracy, precision, and recall on validation sets.\n"
                + "6. **Inference / Deployment**: Deploying the trained model to process new user inputs in real-time.\n\n"
                + "#### 3. Core Paradigm Shift\n"
                + "• **Traditional Programming**: Rules + Data ➔ Answers\n"
                + "• **Machine Learning**: Data + Answers ➔ Rules (Model)\n\n"
                + "#### 4. Real-World Applications\n"
                + "• **Healthcare**: Early cancer detection in radiological images.\n"
                + "• **Finance**: Real-time credit card fraud detection.\n"
                + "• **E-commerce**: Personalized product recommendations on Amazon/Netflix.\n"
                + "• **Automotive**: Autonomous self-driving vehicles.\n\n"
                + "#### 5. Advantages & Limitations\n"
                + "• **Advantages**: Handles complex multi-dimensional data, scales automatically, adapts to dynamic environments.\n"
                + "• **Limitations**: Requires huge datasets, susceptible to historical bias, black-box interpretability issues.");
        ml.addExample("Email Spam Filter: Classifies incoming emails as 'Spam' or 'Inbox' based on historical spam word patterns.");
        ml.addExample("Predictive Maintenance: Predicts factory machine failure before breakdown occurs.");
        ml.addAdvantage("Automates decision-making processes across vast data scales.");
        ml.addAdvantage("Discovers non-linear relationships hidden from human intuition.");
        ml.addLimitation("Completely dependent on training data quality ('Garbage in, Garbage out').");
        ml.addLimitation("High computational resources required for deep models.");
        ml.addApplication("Recommendation Engines (Spotify, Netflix)");
        ml.addApplication("Fraud Detection Systems in Banking");
        ml.addApplication("Medical Diagnosis & Image Segmentation");
        ml.addRelatedTopic("ml_types");
        ml.addRelatedTopic("ai_vs_ml");
        ml.addRelatedTopic("supervised_learning");
        addTopic(ml);

        // Topic: Types of Machine Learning
        Topic mlTypes = new Topic("ml_types", "Types of Machine Learning", "Fundamentals");
        mlTypes.addKeyword("types of machine learning");
        mlTypes.addKeyword("types of ml");
        mlTypes.addKeyword("categories of machine learning");
        mlTypes.addKeyword("categories of ml");
        mlTypes.addKeyword("different types of machine learning");
        mlTypes.addKeyword("different types of ml");
        mlTypes.addKeyword("how many types of machine learning");
        mlTypes.addKeyword("how many types of ml");
        mlTypes.addKeyword("explain different types of ml");
        mlTypes.addKeyword("explain types of ml");
        mlTypes.setShortAnswer("Machine Learning is categorized into 4 major types: 1. Supervised Learning, 2. Unsupervised Learning, 3. Semi-Supervised Learning, and 4. Reinforcement Learning.");
        mlTypes.setNormalAnswer("Machine Learning algorithms are broadly divided into 4 fundamental categories based on how they learn from data:\n\n"
                + "1. **Supervised Learning**: Algorithms train on labeled data containing both input features (X) and target outputs (Y). Example: Spam filtering, House price prediction.\n\n"
                + "2. **Unsupervised Learning**: Algorithms discover hidden patterns or clusters in unlabeled data (X only). Example: Customer segmentation using K-Means, PCA.\n\n"
                + "3. **Semi-Supervised Learning**: Algorithms combine a small amount of labeled data with a large volume of unlabeled data during training. Example: Medical imaging diagnosis.\n\n"
                + "4. **Reinforcement Learning**: Autonomous agents learn decision-making strategies through trial-and-error interactions with rewards and penalties in an environment. Example: Self-driving cars, Game engines (AlphaGo).");
        mlTypes.setWorking("1. **Supervised Learning Setup**: Define feature vector $X$ and target label $Y$. Algorithm computes mapping function $Y = f(X)$.\n"
                + "2. **Unsupervised Learning Setup**: Feed unlabelled features $X$. Algorithm groups samples based on similarity metrics or variance maximization.\n"
                + "3. **Semi-Supervised Setup**: Train initial model on small labeled set $L$, generate pseudo-labels for large unlabeled set $U$, and retrain on combined set.\n"
                + "4. **Reinforcement Setup**: Agent observes state $S$, executes action $A$, receives reward $R$, updates policy $\\pi(a|s)$.");
        mlTypes.setDeepAnswer("### The 4 Major Types of Machine Learning\n\n"
                + "#### 1. Supervised Learning\n"
                + "• **Concept**: The model learns from labeled historical data $(X, Y)$ with known correct targets.\n"
                + "• **Subtypes**: Classification (discrete outcomes) and Regression (continuous numerical values).\n"
                + "• **Popular Algorithms**: Linear Regression, Logistic Regression, Decision Tree, Random Forest, SVM.\n"
                + "• **Real-world Example**: Bank fraud detection system.\n\n"
                + "#### 2. Unsupervised Learning\n"
                + "• **Concept**: The algorithm works on unlabelled data $(X)$ to find intrinsic structures, groupings, or compressed representations.\n"
                + "• **Subtypes**: Clustering, Dimensionality Reduction, Association Rule Mining.\n"
                + "• **Popular Algorithms**: K-Means Clustering, PCA, DBSCAN, Apriori.\n"
                + "• **Real-world Example**: E-commerce customer behavior segmentation.\n\n"
                + "#### 3. Semi-Supervised Learning\n"
                + "• **Concept**: Bridges the gap between Supervised and Unsupervised Learning by leveraging a tiny set of expensive expert-labeled data alongside massive cheap unlabeled data.\n"
                + "• **Real-world Example**: Speech recognition, Medical X-ray diagnostics.\n\n"
                + "#### 4. Reinforcement Learning\n"
                + "• **Concept**: An autonomous agent interacts with an environment, executing actions to maximize cumulative reward signals.\n"
                + "• **Key Components**: Agent, Environment, State, Action, Reward, Policy.\n"
                + "• **Real-world Example**: Self-driving navigation, Robotic automation, AlphaGo.");
        mlTypes.addExample("Supervised: Spam email classification.");
        mlTypes.addExample("Unsupervised: Grouping online shoppers into 5 buyer personas.");
        mlTypes.addExample("Semi-Supervised: Analyzing 100 annotated cancer scans with 10,000 unannotated scans.");
        mlTypes.addExample("Reinforcement Learning: Training a quadruped robot to walk across rough terrain.");
        mlTypes.addAdvantage("Supervised provides high accuracy when labeled data exists.");
        mlTypes.addAdvantage("Unsupervised requires zero human manual data annotation.");
        mlTypes.addAdvantage("Reinforcement Learning solves complex sequential decision-making tasks.");
        mlTypes.addLimitation("Supervised requires expensive manual labeling.");
        mlTypes.addLimitation("Unsupervised clusters can be difficult for humans to validate.");
        mlTypes.addApplication("Banking Credit Scoring");
        mlTypes.addApplication("E-commerce Customer Segmentation");
        mlTypes.addApplication("Autonomous Robotics & Gaming AI");
        mlTypes.addRelatedTopic("supervised_learning");
        mlTypes.addRelatedTopic("unsupervised_learning");
        mlTypes.addRelatedTopic("semi_supervised_learning");
        mlTypes.addRelatedTopic("reinforcement_learning");
        addTopic(mlTypes);

        // Topic: Supervised Learning
        Topic supervised = new Topic("supervised_learning", "Supervised Learning", "Types of ML");
        supervised.addKeyword("supervised learning");
        supervised.addKeyword("supervised");
        supervised.addKeyword("labeled data learning");
        supervised.setShortAnswer("Supervised Learning is an ML paradigm where models are trained using labeled datasets containing both input features (X) and target outputs (Y).");
        supervised.setNormalAnswer("In Supervised Learning, the algorithm learns from a dataset that includes known correct answers (labels). By comparing its predicted outputs with ground truth labels during training, the model optimizes its mapping function to accurately predict outcomes for new, unseen data.\n\n"
                + "• **Primary Subtypes**: Classification (discrete outcomes) and Regression (continuous numerical outcomes).\n"
                + "• **Popular Algorithms**: Linear Regression, Logistic Regression, Decision Trees, Random Forest, SVM.");
        supervised.setWorking("1. **Dataset Pairing**: Provide input features $X$ paired with ground truth labels $Y$.\n"
                + "2. **Forward Pass**: Model computes prediction $\\hat{Y} = f(X, w)$.\n"
                + "3. **Loss Computation**: Measure error $L(Y, \\hat{Y})$ between predicted and actual target.\n"
                + "4. **Weight Update**: Optimization algorithm (e.g., Gradient Descent) updates weights $w$ to minimize loss.\n"
                + "5. **Convergence**: Repeat until loss minimizes on validation set.");
        supervised.setDeepAnswer("### Comprehensive Guide to Supervised Learning\n\n"
                + "#### 1. Fundamental Principle\n"
                + "The goal is to learn a mapping function $f: X \\rightarrow Y$ that maps input features $X$ to target labels $Y$ such that $f(x)$ accurately approximates $y$ on novel samples.\n\n"
                + "#### 2. Workflow Steps\n"
                + "1. Collect dataset with explicit target ground-truth labels.\n"
                + "2. Split into Train, Validation, and Test sets.\n"
                + "3. Choose supervised algorithm (e.g., Random Forest Classifier).\n"
                + "4. Train model by minimizing Loss Function ($L(y, \\hat{y})$).\n"
                + "5. Validate and evaluate using metric like F1-Score or RMSE.\n\n"
                + "#### 3. Main Categories\n"
                + "• **Classification**: Target is a categorical class label (Spam vs. Ham, Fraud vs. Legitimate).\n"
                + "• **Regression**: Target is a continuous real value (Stock Price, Temperature, Sales Revenue).\n\n"
                + "#### 4. Pros and Cons\n"
                + "• **Advantages**: Highly accurate, explicit performance evaluation, direct control over desired task.\n"
                + "• **Limitations**: Requires expensive human manual data annotation/labeling.");
        supervised.addExample("House Price Prediction: Using square footage, location, and rooms (X) to predict market value (Y).");
        supervised.addExample("Medical Image Diagnosis: Training on labeled X-rays to identify presence of pneumonia.");
        supervised.addAdvantage("Direct guidance during training leads to high precise accuracy.");
        supervised.addAdvantage("Easy to evaluate metrics against known labels.");
        supervised.addLimitation("High cost and labor required to manually label big datasets.");
        supervised.addApplication("Credit Risk Assessment");
        supervised.addApplication("Email Spam Detection");
        supervised.addRelatedTopic("unsupervised_learning");
        supervised.addRelatedTopic("classification");
        supervised.addRelatedTopic("regression");
        addTopic(supervised);

        // Topic: Unsupervised Learning
        Topic unsupervised = new Topic("unsupervised_learning", "Unsupervised Learning", "Types of ML");
        unsupervised.addKeyword("unsupervised learning");
        unsupervised.addKeyword("unsupervised");
        unsupervised.addKeyword("unlabeled data");
        unsupervised.setShortAnswer("Unsupervised Learning is an ML paradigm where models discover hidden patterns, clusters, or structures in unlabeled datasets without human guidance.");
        unsupervised.setNormalAnswer("In Unsupervised Learning, the dataset consists of input features (X) without any target labels (Y). The algorithm automatically groups similar data points or reduces data dimensions by discovering underlying distribution structure.\n\n"
                + "• **Primary Subtypes**: Clustering (K-Means, DBSCAN) and Dimensionality Reduction (PCA, t-SNE).\n"
                + "• **Key Benefit**: Works on vast amounts of raw, unlabelled internet data.");
        unsupervised.setDeepAnswer("### Deep Dive into Unsupervised Learning\n\n"
                + "#### 1. Main Objectives\n"
                + "• **Clustering**: Grouping data points into distinct clusters based on similarity (e.g., Euclidean distance).\n"
                + "• **Dimensionality Reduction**: Compressing hundreds of features into fewer principal components while preserving dataset variance.\n"
                + "• **Association Rule Mining**: Finding relationships between variables (e.g., Market Basket Analysis: 'Customers who buy diapers also buy beer').\n\n"
                + "#### 2. Popular Algorithms\n"
                + "• **Clustering**: K-Means, Hierarchical Clustering, DBSCAN.\n"
                + "• **Dimensionality Reduction**: Principal Component Analysis (PCA), Autoencoders.\n"
                + "• **Association**: Apriori Algorithm.");
        unsupervised.addExample("Customer Market Segmentation: Grouping online shoppers based on purchasing behavior without prior categories.");
        unsupervised.addExample("Anomaly Detection: Identifying unusual network traffic spikes indicating cyberattacks.");
        unsupervised.addAdvantage("No manual labeling cost required.");
        unsupervised.addAdvantage("Discovers unknown, surprising patterns in raw data.");
        unsupervised.addLimitation("Output clusters can be hard for humans to interpret or validate.");
        unsupervised.addApplication("Customer Segmentation");
        unsupervised.addApplication("Image Compression");
        unsupervised.addRelatedTopic("supervised_learning");
        unsupervised.addRelatedTopic("kmeans");
        unsupervised.addRelatedTopic("pca");
        addTopic(unsupervised);

        // Topic: Support Vector Machine
        Topic svm = new Topic("svm", "Support Vector Machine (SVM)", "Algorithms");
        svm.addKeyword("svm");
        svm.addKeyword("support vector machine");
        svm.addKeyword("support vector machines");
        svm.addKeyword("kernel trick");
        svm.addKeyword("margin");
        svm.addKeyword("hyperplane");
        svm.addKeyword("support vectors");
        svm.setShortAnswer("Support Vector Machine (SVM) is a powerful supervised learning algorithm that finds an optimal decision boundary (hyperplane) maximizing the margin between target classes.");
        svm.setNormalAnswer("Support Vector Machine (SVM) is a supervised classification and regression algorithm. It finds a decision boundary called a **hyperplane** in high-dimensional space that separates data points into classes with the maximum possible margin.\n\n"
                + "• **Hyperplane**: Decision boundary dividing target classes.\n"
                + "• **Support Vectors**: Data points located closest to the hyperplane that define its position.\n"
                + "• **Margin**: Perpendicular distance between hyperplane and closest support vectors.\n"
                + "• **Kernel Trick**: Mathematical functions (RBF, Polynomial) projecting non-linear data into higher dimensions for linear separation.");
        svm.setWorking("1. **Feature Mapping**: Plot data samples as vectors in n-dimensional feature space.\n"
                + "2. **Hyperplane Evaluation**: Evaluate candidate hyperplanes separating positive and negative classes.\n"
                + "3. **Margin Maximization**: Compute perpendicular distance to closest support vectors and select hyperplane maximizing margin width ($2/||w||$).\n"
                + "4. **Kernel Function**: If classes are non-linearly separable, apply Kernel function $K(x_i, x_j)$ (e.g. RBF) to implicitly project data into higher dimension where linear separation exists.\n"
                + "5. **Inference**: Classify new point $x$ using decision function $f(x) = sign(w^T x + b)$.");
        svm.setDeepAnswer("### Comprehensive Guide to Support Vector Machine (SVM)\n\n"
                + "#### 1. Definition\n"
                + "Support Vector Machine (SVM) is a supervised machine learning algorithm used primarily for classification and regression. It constructs a high-dimensional hyperplane that maximizes the margin between dataset classes.\n\n"
                + "#### 2. Key Mathematical Components\n"
                + "• **Hyperplane**: Linear boundary equation $w^T x + b = 0$.\n"
                + "• **Support Vectors**: Points on the margin boundaries $w^T x_i + b = +1$ and $w^T x_i + b = -1$.\n"
                + "• **Margin Width**: $M = \\frac{2}{||w||}$. Maximizing margin is equivalent to minimizing $\\frac{1}{2} ||w||^2$.\n"
                + "• **Kernel Trick**: Computes dot products in high-dimensional feature space without explicitly calculating coordinates $K(x_i, x_j) = \\phi(x_i)^T \\phi(x_j)$. Common kernels: Radial Basis Function (RBF), Linear, Polynomial, Sigmoid.\n\n"
                + "#### 3. Real-World Example\n"
                + "• **Cancer Classification**: Categorizing gene expression profiles into Malignant vs Benign tumors based on high-dimensional genomic features.\n\n"
                + "#### 4. Advantages & Limitations\n"
                + "• **Advantages**: Effective in high-dimensional spaces, memory efficient (uses only support vectors), robust against overfitting in high dimensions.\n"
                + "• **Limitations**: Computationally heavy on massive datasets (>100,000 samples), sensitive to noise/outliers, complex hyperparameter tuning ($C, \\gamma$).");
        svm.addExample("Medical Tumor Diagnosis: Classifying biopsy samples as Benign or Malignant using high-dimensional genomic features.");
        svm.addExample("Handwritten Digit Recognition: Classifying ZIP code digits on mail packages.");
        svm.addAdvantage("High accuracy in high-dimensional spaces where feature count exceeds sample count.");
        svm.addAdvantage("Memory efficient because predictions rely strictly on support vector data points.");
        svm.addAdvantage("Versatile non-linear decision boundaries using Kernel functions.");
        svm.addLimitation("Slow training speed on very large datasets.");
        svm.addLimitation("Sensitive to noise and overlapping target class samples.");
        svm.addLimitation("Does not directly output probability estimates.");
        svm.addApplication("Bioinformatics & Gene Classification");
        svm.addApplication("Text & Document Categorization");
        svm.addApplication("Face Recognition & Image Detection");
        svm.addRelatedTopic("classification");
        svm.addRelatedTopic("logistic_regression");
        addTopic(svm);

        // Topic: K-Nearest Neighbors
        Topic knn = new Topic("knn", "K-Nearest Neighbors (KNN)", "Algorithms");
        knn.addKeyword("knn");
        knn.addKeyword("k nearest neighbors");
        knn.addKeyword("k-nearest neighbors");
        knn.addKeyword("nearest neighbors");
        knn.setShortAnswer("K-Nearest Neighbors (KNN) is a simple instance-based algorithm that classifies a new sample based on majority vote among its K closest neighboring training points.");
        knn.setNormalAnswer("K-Nearest Neighbors (KNN) is a non-parametric, supervised algorithm used for classification and regression. It makes predictions without explicit training by storing all historical training samples and finding the $K$ closest data points using distance metrics like Euclidean distance.\n\n"
                + "• **Instance-Based Learning**: Does not build an explicit model function during training ('Lazy Learner').\n"
                + "• **Choice of K**: Determines boundary smoothness ($K=1$ is noisy; large $K$ is smooth).\n"
                + "• **Distance Metrics**: Euclidean, Manhattan, Minkowski.");
        knn.setWorking("1. **Store Training Data**: KNN stores the entire training dataset in memory.\n"
                + "2. **Select Distance Metric**: Choose metric like Euclidean distance $d(p, q) = \\sqrt{\\sum_{i=1}^n (p_i - q_i)^2}$.\n"
                + "3. **Calculate Distances**: Compute distance between the new unlabelled input sample and all stored training samples.\n"
                + "4. **Identify K Nearest Neighbors**: Sort distances in ascending order and select the $K$ data points with smallest distance values.\n"
                + "5. **Majority Voting / Averaging**: For classification, assign the most frequent class label among the K neighbors. For regression, return the mean target value of the K neighbors.");
        knn.setDeepAnswer("### Comprehensive Guide to K-Nearest Neighbors (KNN)\n\n"
                + "#### 1. Definition\n"
                + "KNN is a non-parametric, lazy-learning algorithm that classifies new data based on distance similarity to its K nearest neighbors in feature space.\n\n"
                + "#### 2. Key Mechanics\n"
                + "• **Lazy Learning**: Zero training phase ($O(1)$ training complexity); all compute cost occurs during inference ($O(N \\cdot D)$).\n"
                + "• **Distance Metrics**: Euclidean $d = \\sqrt{\\sum(x_i-y_i)^2}$, Manhattan $d = \\sum|x_i-y_i|$.\n"
                + "• **Hyperparameter K**: Small K causes high variance (overfitting); large K causes high bias (underfitting).\n\n"
                + "#### 3. Real-World Example\n"
                + "• **Customer Recommendation**: Recommending movies to a new user based on top 5 users with similar rating histories.\n\n"
                + "#### 4. Advantages & Limitations\n"
                + "• **Advantages**: Extremely simple, no training time, easy to implement.\n"
                + "• **Limitations**: High inference computational cost, memory intensive, highly sensitive to unscaled features and irrelevant dimensions.");
        knn.addExample("Movie Recommendation System: Suggesting movies to a user by finding 5 users with similar rating patterns.");
        knn.addAdvantage("Zero training phase required; instantly adapts as new training data arrives.");
        knn.addAdvantage("Simple and intuitive non-parametric algorithm.");
        knn.addLimitation("Computationally expensive during inference on large datasets ($O(N)$ per query).");
        knn.addLimitation("Highly sensitive to feature scaling and the Curse of Dimensionality.");
        knn.addApplication("Recommender Systems");
        knn.addApplication("Pattern Recognition & Handwriting Analysis");
        knn.addRelatedTopic("classification");
        knn.addRelatedTopic("normalization");
        addTopic(knn);

        // Topic: Decision Tree
        Topic decTree = new Topic("decision_tree", "Decision Tree", "Algorithms");
        decTree.addKeyword("decision tree");
        decTree.addKeyword("decision trees");
        decTree.addKeyword("cart algorithm");
        decTree.addKeyword("gini impurity");
        decTree.addKeyword("entropy information gain");
        decTree.setShortAnswer("A Decision Tree is a flowchart-like model that recursively splits data based on feature conditions to make predictions.");
        decTree.setNormalAnswer("Decision Trees break down a complex decision task into a sequence of simple if-else feature questions. They can perform both Classification and Regression.\n\n"
                + "• **Splitting Criteria**: Gini Impurity or Information Gain (Entropy).\n"
                + "• **Key Feature**: Highly interpretable flowchart structure.\n"
                + "• **Major Risk**: High tendency to overfit without pruning.");
        decTree.setWorking("1. **Start at Root**: Begin with entire training dataset at root node.\n"
                + "2. **Evaluate Feature Splits**: For each feature, test all possible split thresholds $X_j \\le k$.\n"
                + "3. **Calculate Purity Gain**: Compute Gini Impurity ($1 - \\sum p_i^2$) or Information Gain ($H(Parent) - \\sum w_i H(Child_i)$) for candidate splits.\n"
                + "4. **Select Best Split**: Choose the feature and threshold yielding maximum purity gain.\n"
                + "5. **Recursive Sub-tree Construction**: Repeat process on child nodes until stopping criteria (`max_depth`, `min_samples_split`) are reached.");
        decTree.setDeepAnswer("### Decision Tree Mechanics\n\n"
                + "#### 1. Node Types\n"
                + "• **Root Node**: Top node containing all initial training data.\n"
                + "• **Decision/Internal Nodes**: Sub-splits based on specific feature threshold ($X_i > k$).\n"
                + "• **Leaf/Terminal Nodes**: Final nodes containing predicted target class or value.\n\n"
                + "#### 2. Splitting Math\n"
                + "• **Gini Impurity**: $Gini = 1 - \\sum p_i^2$ (Measures purity of split).\n"
                + "• **Entropy**: $H(S) = -\\sum p_i \\log_2(p_i)$ (Measures randomness).\n"
                + "• **Information Gain**: $IG = H(Parent) - \\sum W_i H(Child_i)$.\n\n"
                + "#### 3. Overfitting Prevention\n"
                + "Controlled via `max_depth`, `min_samples_split`, `min_samples_leaf`, and cost-complexity pruning.");
        decTree.addExample("Loan Approval: Splitting candidates on Credit Score > 700, then Income > $50,000.");
        decTree.addAdvantage("Highly visual and easily interpretable flowchart structure.");
        decTree.addAdvantage("Requires minimal data preprocessing (handles numerical and categorical features without scaling).");
        decTree.addLimitation("High risk of overfitting if allowed to grow deep without pruning.");
        decTree.addLimitation("Sensitive to minor fluctuations in training data.");
        decTree.addApplication("Credit Risk Assessment");
        decTree.addApplication("Medical Diagnostic Flowcharts");
        decTree.addRelatedTopic("random_forest");
        decTree.addRelatedTopic("overfitting");
        addTopic(decTree);

        // Topic: Convolutional Neural Network (CNN)
        Topic cnn = new Topic("cnn", "Convolutional Neural Network (CNN)", "Deep Learning");
        cnn.addKeyword("cnn");
        cnn.addKeyword("convolutional neural network");
        cnn.addKeyword("computer vision");
        cnn.addKeyword("convolution");
        cnn.addKeyword("pooling");
        cnn.setShortAnswer("CNN is a specialized deep neural network architecture designed for grid-like data (images, video) using convolutional kernels to extract spatial features.");
        cnn.setNormalAnswer("Convolutional Neural Networks (CNN) revolutionize Computer Vision by applying sliding mathematical filters (kernels) across input image matrices to automatically detect edges, textures, shapes, and complex object features.\n\n"
                + "• **Convolutional Layers**: Apply 2D filter kernels to generate feature maps.\n"
                + "• **Pooling Layers** (Max/Average): Downsample spatial dimensions while preserving key features.\n"
                + "• **Fully Connected (FC) Layers**: Flatten features to output class probabilities.");
        cnn.setWorking("1. **Input Layer**: Takes raw image pixel matrix (Width x Height x Channels).\n"
                + "2. **Convolution**: Feature filters slide across image performing element-wise dot products to extract feature maps.\n"
                + "3. **ReLU Activation**: Applies non-linearity $f(x) = max(0, x)$.\n"
                + "4. **Max Pooling**: Reduces spatial height and width while maintaining feature invariance.\n"
                + "5. **Classification**: Dense layers map extracted features to final class probabilities via Softmax.");
        cnn.addExample("Object Detection in Self-Driving Cars: Identifying pedestrians, traffic signs, and vehicles.");
        cnn.addAdvantage("Automates spatial feature extraction directly from raw pixels.");
        cnn.addAdvantage("Parameter sharing reduces total weight count compared to dense networks.");
        cnn.addLimitation("Requires large labeled image datasets and GPU hardware.");
        cnn.addApplication("Medical Imaging Analysis");
        cnn.addApplication("Facial Recognition");
        cnn.addRelatedTopic("neural_networks");
        addTopic(cnn);

        // Topic: Recurrent Neural Network (RNN & LSTM)
        Topic rnn = new Topic("rnn", "Recurrent Neural Networks (RNN & LSTM)", "Deep Learning");
        rnn.addKeyword("rnn");
        rnn.addKeyword("recurrent neural network");
        rnn.addKeyword("lstm");
        rnn.addKeyword("long short term memory");
        rnn.addKeyword("sequence model");
        rnn.setShortAnswer("RNNs and LSTMs are deep learning architectures tailored for sequential data (time-series, text, audio) using internal hidden state memory.");
        rnn.setNormalAnswer("Recurrent Neural Networks (RNNs) maintain an internal memory state $h_t$ that carries information across sequence time steps. **Long Short-Term Memory (LSTM)** networks solve the vanishing gradient problem using Forget, Input, and Output gates.\n\n"
                + "• **LSTM Gates**: Forget Gate (what to discard), Input Gate (what to store), Output Gate (what to predict).");
        rnn.addRelatedTopic("transformers");
        rnn.addRelatedTopic("neural_networks");
        addTopic(rnn);

        // Topic: Transformers & Attention
        Topic transformers = new Topic("transformers", "Transformers & Attention Mechanism", "Deep Learning");
        transformers.addKeyword("transformer");
        transformers.addKeyword("transformers");
        transformers.addKeyword("attention mechanism");
        transformers.addKeyword("bert");
        transformers.addKeyword("gpt");
        transformers.addKeyword("llm");
        transformers.setShortAnswer("Transformers are state-of-the-art neural architectures relying on Self-Attention to process entire sequences in parallel, powering modern LLMs.");
        transformers.setNormalAnswer("Transformers replace recurrent connections with **Self-Attention** ($Attention(Q,K,V) = softmax(\\frac{QK^T}{\\sqrt{d_k}})V$), enabling parallel processing of text tokens and capturing long-range contextual dependencies.");
        transformers.addRelatedTopic("rnn");
        transformers.addRelatedTopic("neural_networks");
        addTopic(transformers);

        // Topic: Python ML Ecosystem
        Topic pythonMl = new Topic("python_ml", "Python Machine Learning Ecosystem", "Programming");
        pythonMl.addKeyword("python ml");
        pythonMl.addKeyword("scikit learn");
        pythonMl.addKeyword("pandas");
        pythonMl.addKeyword("numpy");
        pythonMl.addKeyword("pytorch");
        pythonMl.addKeyword("tensorflow");
        pythonMl.setShortAnswer("Python is the standard language for ML, supported by NumPy (arrays), Pandas (dataframes), Scikit-Learn (classical ML), and PyTorch/TensorFlow (deep learning).");
        pythonMl.setNormalAnswer("### Core Python ML Stack\n\n"
                + "• **NumPy**: High-performance multi-dimensional array operations.\n"
                + "• **Pandas**: Structured dataframe manipulation, filtering, and CSV/JSON parsing.\n"
                + "• **Scikit-Learn**: Classical ML algorithms, feature scaling, model metrics, and cross-validation.\n"
                + "• **PyTorch & TensorFlow**: GPU-accelerated deep learning frameworks with automatic differentiation.");
        pythonMl.addRelatedTopic("linear_regression");
        pythonMl.addRelatedTopic("random_forest");
        addTopic(pythonMl);

        // Populate comparisons
        supervised.addComparison("unsupervised_learning", "### Supervised vs Unsupervised Learning\n\n"
                + "| Aspect | Supervised Learning | Unsupervised Learning |\n"
                + "| :--- | :--- | :--- |\n"
                + "| **Data Type** | Requires Labeled Data ($X, Y$) | Works on Unlabeled Data ($X$ only) |\n"
                + "| **Goal** | Predict target output for new inputs | Discover hidden patterns/clusters |\n"
                + "| **Tasks** | Classification, Regression | Clustering, Dimensionality Reduction |\n"
                + "| **Evaluation** | Direct against ground-truth labels | Unsupervised metrics (Silhouette score, Variance) |\n"
                + "| **Algorithms** | Linear Regression, Decision Tree, SVM | K-Means, PCA, DBSCAN |");

        decTree.addComparison("rand_forest", "### Decision Tree vs Random Forest\n\n"
                + "| Aspect | Decision Tree | Random Forest |\n"
                + "| :--- | :--- | :--- |\n"
                + "| **Model Type** | Single Flowchart Model | Ensemble of hundreds of trees |\n"
                + "| **Overfitting Risk** | High (tends to memorize training data) | Low (averages out variance across trees) |\n"
                + "| **Speed** | Extremely Fast training & prediction | Slower training, higher memory requirement |\n"
                + "| **Interpretability** | Easy to visualize decision flowchart | Black-box ensemble voting |");

        svm.addComparison("knn", "### Support Vector Machine (SVM) vs K-Nearest Neighbors (KNN)\n\n"
                + "| Aspect | Support Vector Machine (SVM) | K-Nearest Neighbors (KNN) |\n"
                + "| :--- | :--- | :--- |\n"
                + "| **Algorithm Type** | Parametric Hyperplane Classifier | Non-parametric Instance-based Lazy Learner |\n"
                + "| **Training Phase** | Slow $O(N^2)$ training; optimizes support vectors | Zero training $O(1)$; stores data in memory |\n"
                + "| **Inference Phase** | Fast $O(D \\cdot N_{sv})$; evaluates dot products | Slow $O(N \\cdot D)$; computes distances to all points |\n"
                + "| **Memory Usage** | Compact (stores only support vectors) | Heavy (stores entire training dataset) |\n"
                + "| **High Dimensions** | Exceptionally strong with Kernel Trick | Weak due to Curse of Dimensionality |");
    }
}