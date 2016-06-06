Read me

The Implmentation has following java files that performs different functionalities

ML_HW3 - Driver Class
NeuralNetworks - Implement the Neural Network Classifier using single perceptron. It contains the functions and data required for Neural Network classification
Email - Class that models the email. It contains the email message and functions to preprocess email
TrainingData - Stores the training data and methods to train the system
KMeans - Implements the KMeans clustering algorithm for the compression of the image 

Execution:
Run the files with following command

ML_HW2.java <hamTrainingset_directory_path> <spamTrainingset_directory_path> <hamTestset_directory_path> <hamTestset_directory_path> <StopwordsFilePath> <Image-to-be-compressed>

As the code executes it will ask for the learning rate and the folder to write the compressed message

The number of iterations is set to 250 by default for perceptron.
The number of iterations is set to 250 by default for KMeans.

Sample Output:

Enter learning rate for perceptron: 
0.005

Performance of the algorithms after removing the stop words as follows
Accuracy of email clasification using Perceptron is: 87.65690376569037

Performance of the algorithms without removing the stop words as follows
Accuracy of email clasification using Perceptron is: 85.98326359832636

Image compression using K-means clustering
Enter the folder name to save the compressed images
C:\\Users\\Praveen\\OneDrive\\Spring_2016\\Machine_Learning\\Assignment\\3
Enter the number of K values to be tested: 
2
Enter K value: 
5
Enter K value: 
10

Note: Images will get compressed and get stored in the target folder specified.