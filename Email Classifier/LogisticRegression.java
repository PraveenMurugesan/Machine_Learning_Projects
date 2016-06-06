//package ml_hw2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/*
 Class: Logistic Regression
 This class contains all the necessary data and methods needed to implement logistic regression
 */
public class LogisticRegression {

    TrainingData trainingData;
    double learningRate;    //Learning rate: alpha
    double regularizationFactor;    //regularization parameter eta
    HashMap<String, Double> features; // Map that features and its count

    long truePositive = 0;
    long testSamples = 0;

    public LogisticRegression(TrainingData td, double learningRate, double regularizationFactor) {
        trainingData = td;
        this.learningRate = learningRate;
        this.regularizationFactor = regularizationFactor;
    }

    /*
     Method: findDistinctFeatures - used to find distinct features and number of times it got repeated
     i/p:
     emails: training data emails
     */
    void findDistinctFeatures(Email emails[]) {
        features.put("", 0.0); // Default feature - x0
        for (int i = 0; i < emails.length; i++) {
            for (String s : emails[i].processedMessage.keySet()) {
                if (!features.containsKey(s)) {
                    features.put(s, 0.0);
                }
            }
        }
    }

    /*
     Method: findFeatures. Wrapper method to find features
     */
    void findFeatures() {
        features = new HashMap<>();
        findDistinctFeatures(trainingData.hamEmails);
        findDistinctFeatures(trainingData.spamEmails);
    }

    /*
     Method: findPredictedValue. finds h(x) value of logistic regression formula.
     h(x) = 1/1+ exp(-z); z= w0+ Summation(wi*xi); i ranges from 1 to n
     */
    double findPredictedValue(Email email) {
        double hOfX = features.get("");
        for (String s : email.processedMessage.keySet()) {
            double featureWeight = 0;
            if (features.containsKey(s)) {
                featureWeight = features.get(s);
            }
            hOfX = hOfX + featureWeight * email.processedMessage.get(s);   //Summation of wi*xi
        }
        hOfX = 1 / (1 + Math.exp(-hOfX));
        return hOfX;
    }

    /*
     Method: trainWeights. Used to train weights
     w0 = w0 - (learningRate/number of training Examples) Summation( h(x) - y) x0
     wj = wj - (learningRate/number of training Examples) Summation( h(x) - y) xj + (regularizationfactor) wj 
     */
    void trainWeights() {
        for (int t = 0; t < 5; t++) {
            for (String s : features.keySet()) {
                boolean isDefaultFeature = false;
                if (s.equals("")) {
                    isDefaultFeature = true;
                }
                double sumValue = 0;
                Email emails[] = trainingData.hamEmails;
                for (int i = 0; i < emails.length; i++) {
                    double hOfX = findPredictedValue(emails[i]);
                    double featureCount = 0;

                    if (emails[i].processedMessage.containsKey(s)) {
                        featureCount = emails[i].processedMessage.get(s);
                    }
                    if (!isDefaultFeature) {
                        sumValue += (1-hOfX) * featureCount; //hOfX - 1 since ham documents
                    } else {
                        sumValue += (1-hOfX) * 1;  //x0 is always 1
                    }
                }
                emails = trainingData.spamEmails;
                for (int i = 0; i < emails.length; i++) {
                    double hOfX = findPredictedValue(emails[i]);
                    double featureCount = 0;

                    if (emails[i].processedMessage.containsKey(s)) {
                        featureCount = emails[i].processedMessage.get(s);
                    }
                    if (!isDefaultFeature) {
                        sumValue += (0-hOfX) * featureCount; // hOFX - 0 since spam documents
                    } else {
                        sumValue += (0-hOfX) * 1;  //x0 is always 1
                    }
                }
                double secondPart = learningRate / (double) (trainingData.numberOfHamDocuments + trainingData.numberOfSpamDocuments);
                double featureWeight = features.get(s);
                if (!isDefaultFeature) {
                    secondPart = secondPart * (sumValue + regularizationFactor * featureWeight);
                    featureWeight = featureWeight + secondPart;
                } else {
                    secondPart = secondPart * sumValue;
                    featureWeight = featureWeight + secondPart;
                }
                features.put(s, featureWeight);
            }
        }
    }

    /*
     Method: train - wrapper method for training weights
     */
    void train() {
        findFeatures();
        trainWeights();
    }

    /*
     Method: findClass. Used to find the clss for the test email.
     i/p: 
     email
     */
    EmailClass findClass(Email email) {
        double hOfX = findPredictedValue(email);
        //double probabilityOfSpam = 1 / (double) (1 + Math.exp(hOfX));
        //double probabilityOfHam = Math.exp(hOfX) / (double) (1 + Math.exp(hOfX));
        if (hOfX>=0.5) {
            return EmailClass.ham;
        } else {
            return EmailClass.spam;
        }
    }

    /*
     Method: findAccuracy. Used to find the accuracy of the test set.
     dirName: directory name for test set emails
     Email class: Class that the test set emails belong to
     */
    void findTruePostive(String dirName, EmailClass ec, boolean noStopWordsRemoval) throws IOException {
        Email testEmails[];
        File dir = new File(dirName);
        File files[] = dir.listFiles();
        testSamples += files.length;
        //long truePositive = 0;
        for (int j = 0; j < files.length;) {
            int batchLimit = Math.min(20, files.length - j);
            testEmails = new Email[batchLimit];
            int k = 0;
            for (k = 0; k < batchLimit; k++) {
                testEmails[k] = new Email(files[j + k], ec);
            }
            for (k = 0; k < batchLimit; k++) {
                testEmails[k].preprocessEmail(trainingData.stopwords, noStopWordsRemoval);
                if (testEmails[k].ec == findClass(testEmails[k])) {
                    truePositive++;
                }
            }
            j = j + k;
        }
    }

    /*
     Method: test. wrapper method that calls the necesssary methods for testing the emails
     */
    void test(String hamEmailsDirectoryPath, String spamEmailsDirectoryPath, boolean noStopWordsRemoval) throws IOException {
        // String hamEmailsDirectoryPath = "C:\\Users\\Praveen\\OneDrive\\Spring 2016\\Machine_Learning\\Assignment\\2\\test\\ham";
        //System.out.println("The accuracy in classifying the ham emails using Logistic Regression is "+findTruePostive(hamEmailsDirectoryPath, EmailClass.ham, noStopWordsRemoval)*100);
        findTruePostive(hamEmailsDirectoryPath, EmailClass.ham, noStopWordsRemoval);
        // String spamEmailsDirectoryPath = "C:\\Users\\Praveen\\OneDrive\\Spring 2016\\Machine_Learning\\Assignment\\2\\test\\spam";
        //System.out.println("The accuracy in classifying the spam emails using Logistic Regrssion is "+findTruePostive(spamEmailsDirectoryPath, EmailClass.spam, noStopWordsRemoval)*100);
        findTruePostive(spamEmailsDirectoryPath, EmailClass.spam, noStopWordsRemoval);
        System.out.println("Accuracy of email clasification using Logistic Regression is: " + (truePositive / (double) testSamples)*100);
    }
}
