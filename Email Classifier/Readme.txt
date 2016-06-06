Read me

The Implmentation has following java files that performs different functionalities

ML_HW2 - Driver Class
NaiveBayesClassifier - Implements the Naive Bayes classfier. It contains various functions and data required for Naive Bayes Classification
LogisticRegression - Implement the Logistic Regression Classifier. It contains the functions and data required for Logistic regression classification
Email - Class that models the email. It contains the email message and functions to preprocess email
TrainingData - Stores the training data and methods to train the system

Execution:
Run the files with following command

ML_HW2.java <hamTrainingset_directory_path> <spamTrainingset_directory_path> <hamTestset_directory_path> <hamTestset_directory_path> <StopwordsFilePath>

As the code executes it will ask for the learning rate and the regularization factor value

The number of iterations is set to 5 by default.

Sample output:
Enter learning rate for logistic regression: 
0.001
Enter regularization factor for logistic regression: 
5

Performance of the algorithms without removing the stop words as follows

Accuracy of email clasification using Naive Bayes Algorithm  is: 95.60669456066945

Accuracy of email clasification using Logistic Regression is: 72.80334728033473

Performance of the algorithms after removing the stop words as follows

Accuracy of email clasification using Naive Bayes Algorithm  is: 95.60669456066945

Accuracy of email clasification using Logistic Regression is: 73.43096234309623

Note: Since the logistic regression imlplemetation has huge computation. It takes a bit of extra time. approximately(6 mins) 