//package ml_hw3;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class NeuralNetworks {

    HashMap<String, Double> features;

    TrainingData trainingData;
    double learningRate;
    int testSamples, truePositive;

    public NeuralNetworks(TrainingData td, double lr) {
        trainingData = td;
        features = new HashMap<>();
        testSamples = 0;
        truePositive = 0;
        learningRate = lr;
    }

    /*
     Method: findDistinctFeatures - used to find distinct features and number of times it got repeated
     i/p:
     emails: training data emails
     */
    void findDistinctFeatures(Email emails[]) {
        features.put("", 1.0); // Default feature - x0
        for (int i = 0; i < emails.length; i++) {
            for (String s : emails[i].processedMessageList) {
                if (!features.containsKey(s)) {
                    features.put(s, 0.3);
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

    double findPerceptronOutput(Email email) {
        Double perceptronOutput = 0.0;
        for (String s : email.processedMessageList) {
            double featureWeight = 0;
            if (features.containsKey(s)) {
                featureWeight = features.get(s);
            }
            double t = perceptronOutput;
            perceptronOutput = perceptronOutput + featureWeight * email.processedMessage.get(s);
            if(perceptronOutput.equals(Double.POSITIVE_INFINITY))
            {
                System.out.println("hello");
            }
        }
        //Adding feature x0
        perceptronOutput = perceptronOutput + features.get(""); //x0 is always 1

        return perceptronOutput;
    }

    void reAssignWeights(Email email, double target, double observed) {
        for (String s : email.processedMessageList) {
            Double featureWeight = features.get(s);
            double t = featureWeight;
            featureWeight = featureWeight + (target - observed) * learningRate * email.processedMessage.get(s);
            if(featureWeight.equals(Double.NaN) || featureWeight.equals(Double.NEGATIVE_INFINITY)|| featureWeight.equals(Double.POSITIVE_INFINITY))
            {
                System.out.println("hello"); 
            }
               
            features.put(s, featureWeight);
        }
        double featureWeight = features.get("");
        featureWeight = featureWeight + (target - observed) * learningRate;
        features.put("", featureWeight);
    }

    void trainWeights(Email emails[], EmailClass ec) {
        double target = 0;
        if (ec == EmailClass.ham) {
            target = 1;
        }
        for (Email e : emails) {
            double perceptronOutput = findPerceptronOutput(e);
            double observed = 0;
            if(perceptronOutput>=0)
                observed = 1;
            reAssignWeights(e, target, observed);
        }
    }

    /*
     Method: trainWeights. Used to train weights
     w0 = w0 - (learningRate/number of training Examples) Summation( h(x) - y) x0
     wj = wj - (learningRate/number of training Examples) Summation( h(x) - y) xj + (regularizationfactor) wj 
     */
    void trainWeights() {
        for(int i=0;i<250;i++)
        {
            trainWeights(trainingData.hamEmails, EmailClass.ham);
            trainWeights(trainingData.spamEmails, EmailClass.spam);
        }
    }

    /*
     Method: train - wrapper method for training weights
     */
    void train() {
        findFeatures();
        trainWeights();
    }

    EmailClass findClass(Email email) {
        double perceptronOutput = findPerceptronOutput(email);
        if (perceptronOutput >= 0) {
            return EmailClass.ham;
        } else {
            return EmailClass.spam;
        }
    }

    void findTruePositive(String dirName, EmailClass ec, boolean noStopWordsRemoval) throws IOException {
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

    void test(String hamEmailsDirectoryPath, String spamEmailsDirectoryPath, boolean noStopWordsRemoval) throws IOException {
        findTruePositive(hamEmailsDirectoryPath, EmailClass.ham, noStopWordsRemoval);
        findTruePositive(spamEmailsDirectoryPath, EmailClass.spam, noStopWordsRemoval);
        System.out.println("Accuracy of email clasification using Perceptron is: " + (truePositive / (double) testSamples) * 100);
    }
}
