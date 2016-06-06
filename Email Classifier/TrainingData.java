//package ml_hw2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/*
Class: TainingData
Reads and stores the training data
Reads and stores the stop words
*/
public class TrainingData {

    
    Email hamEmails[], spamEmails[]; //Array stores the training emails 
    long numberOfHamDocuments = 0, numberOfSpamDocuments = 0;

    HashSet<String> stopwords;       // Set that stores the stop words

    /*
    Method: readTrainingData - method that reads the training data.
    i/p:
    dirName: directory name that contains the  spam/ham emails (training data)
    */
    void readTrainingData(String dirName, EmailClass ec,boolean noStopWordsRemoval) throws IOException {
        File dir = new File(dirName);
        File files[] = dir.listFiles();
        if (ec == EmailClass.ham) {
            hamEmails = new Email[files.length];
        } else {
            spamEmails = new Email[files.length];
        }
        
        for (int j = 0; j < files.length;j++) {
            if (ec == EmailClass.ham) {
                numberOfHamDocuments++;
                hamEmails[j] = new Email(files[j], ec);
                hamEmails[j].preprocessEmail(stopwords,noStopWordsRemoval);
            } else {
                numberOfSpamDocuments++;
                spamEmails[j] = new Email(files[j], ec);
                spamEmails[j].preprocessEmail(stopwords,noStopWordsRemoval);
            }
        }
    }

    /*
    Method: populateWords. method that reads and populate the stop words hash set.
    */
    private void populateStopWords(String stopWordsPath) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(stopWordsPath)); //"C:\\Users\\Praveen\\OneDrive\\Spring 2016\\Machine_Learning\\Assignment\\2\\stopwords.txt"
        String s = "";
        while ((s = br.readLine()) != null) {
            stopwords.add(s);
        }
    }

    /*
    Method: readTrainingData. wrapper method to read thetraining data.
    */
    void readTrainingData(String hamEmailsDirectoryPath, String spamEmailsDirectoryPath,boolean noStopWordsRemoval) throws IOException {
        readTrainingData(hamEmailsDirectoryPath, EmailClass.ham, noStopWordsRemoval);
        readTrainingData(spamEmailsDirectoryPath, EmailClass.spam, noStopWordsRemoval);
    }

    /*
    Method: train.  method that calls the necessary the function to complete training
    */
    void train(String hamEmailsDirectoryPath,String spamEmailsDirectoryPath,String stopWordsPath,boolean noStopWordsRemoval) throws IOException {
        if(!noStopWordsRemoval)
        {
            stopwords = new HashSet<>();
            populateStopWords(stopWordsPath);
        }
        
        readTrainingData(hamEmailsDirectoryPath, spamEmailsDirectoryPath,noStopWordsRemoval);
    }

}
