//package ml_hw2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/*
Email class: Enum that lists the differenet email classes
*/
enum EmailClass {
    spam, ham;
}

/*
Class: LexiconCount.
Used to store the lexicon along with the count.
*/
class LexiconCount {

    long numberOfOccurencesInHamDocuments;
    long numberOfOccurencesInSpamDocuments;

    LexiconCount() {
        numberOfOccurencesInHamDocuments = 1;
        numberOfOccurencesInSpamDocuments = 1;
    }

    public LexiconCount(long numberOfOccurencesInHamDocuments, long numberOfOccurencesInSpamDocument) {
        this.numberOfOccurencesInHamDocuments = numberOfOccurencesInHamDocuments;
        this.numberOfOccurencesInSpamDocuments = numberOfOccurencesInSpamDocument;
    }
}

/*
Class: NaiveBayesClassifier
Comtains the data and methods required for naive bayes classifier
*/
public class NaiveBayesClassifier {

    long totalNumberOfWords = 0;
    long totalNumberOfWordsInHamDocuments = 0;
    long totalNumberOfWordsInSpamDocuments = 0;
    long truePositive=0;
    long testSamples=0;
    HashMap<String, LexiconCount> words = new HashMap(); //Map that stores the lexicon along with the count
    TrainingData trainingData;

    public NaiveBayesClassifier(TrainingData td) {
        trainingData = td;
    }

    /*
    Method: hashLexicons -  used to count the lexicon occurences in ham and spam documents
    i/p:
    email: the input that has lexicons
    */
    void hashLexicons(Email email) {
        for (String s : email.processedMessage.keySet()) {
            int tempCount = email.processedMessage.get(s);
            
            if(email.ec==EmailClass.ham)
            {
                totalNumberOfWordsInHamDocuments= totalNumberOfWordsInHamDocuments+tempCount;
            }
            else
            {
                totalNumberOfWordsInSpamDocuments = totalNumberOfWordsInSpamDocuments +tempCount;
            }
            //totalNumberOfWords = totalNumberOfWords + tempCount;
            if (words.containsKey(s)) {
                LexiconCount lc = words.get(s);
                long hc = lc.numberOfOccurencesInHamDocuments;
                long sc = lc.numberOfOccurencesInSpamDocuments;

                if (email.ec == EmailClass.ham) {
                    hc = hc + tempCount;
                } else {
                    sc = sc + tempCount;
                }
                words.put(s, new LexiconCount(hc, sc));
            } else {
                long hc=0,sc=0;
                if(email.ec==EmailClass.ham)
                {
                    hc=tempCount;
                }
                else
                {
                    sc=tempCount;
                }
                words.put(s, new LexiconCount(hc,sc));
            }
        }
    }

    /*
    Method: hashLexicons- wrapper method to hash lexicons
    */
    void hashLexicons() {
        for (int i = 0; i < trainingData.hamEmails.length; i++) {
            hashLexicons(trainingData.hamEmails[i]);
        }
        for (int i = 0; i < trainingData.spamEmails.length; i++) {
            hashLexicons(trainingData.spamEmails[i]);
        }
    }

    /*
    Method: findAccuracy. used to find the accuracy  the test set.
    i/p:
    dirName: directory name of hte test set.
    ec: Class that the test set emails belong to
    */
    void findTruePositive(String dirName, EmailClass ec,boolean noStopWordsRemoval) throws IOException {
        Email testEmails[];
        File dir = new File(dirName);
        File files[] = dir.listFiles();

        testSamples+=files.length;
        //long truePositive = 0;
        for (int j = 0; j < files.length;) {
            int batchLimit = Math.min(20, files.length - j);
            testEmails = new Email[batchLimit];
            int k = 0;
            for (k = 0; k < batchLimit; k++) {
                testEmails[k] = new Email(files[j + k], ec);
            }
            for (k = 0; k < batchLimit; k++) {
                testEmails[k].preprocessEmail(trainingData.stopwords,noStopWordsRemoval);
                if (testEmails[k].ec == findClass(testEmails[k],noStopWordsRemoval)) {
                    truePositive++;
                }
            }
            j = j + k;
        }
        
    }

    /*
    Method: findClass. Used to find the clss for the test email.
    i/p: 
    email
    */
    EmailClass findClass(Email email,boolean noStopWordsRemoval) {
        //email.preprocessEmail(trainingData.stopwords,noStopWordsRemoval);
        double hamProbability = 0;
        double spamProbability = 0;

        for (String s : email.processedMessage.keySet()) {
            long n = email.processedMessage.get(s);
            long hc, sc;
            if (words.containsKey(s)) {
                LexiconCount lcTemp = words.get(s);
                hc = lcTemp.numberOfOccurencesInHamDocuments;
                sc = lcTemp.numberOfOccurencesInSpamDocuments;
            } else {
                hc = 0;
                sc = 0;
            }
            double cProbabilityForHam = (hc + 1) / (double) (totalNumberOfWordsInHamDocuments + words.size());
            cProbabilityForHam = Math.pow(cProbabilityForHam, n);
            hamProbability += (Math.log(cProbabilityForHam)) / Math.log(2);

            double cProbabilityForSpam = (sc + 1) / (double) (totalNumberOfWordsInSpamDocuments + words.size());
            cProbabilityForSpam = Math.pow(cProbabilityForSpam, n);
            spamProbability += (Math.log(cProbabilityForSpam)) / Math.log(2);
        }
        double priorProbabilityForHam = trainingData.numberOfHamDocuments / (double) (trainingData.numberOfHamDocuments + trainingData.numberOfSpamDocuments);
        hamProbability += (Math.log(priorProbabilityForHam)) / Math.log(2);

        double priorProbabilityForSpam = trainingData.numberOfSpamDocuments / (double) (trainingData.numberOfHamDocuments + trainingData.numberOfSpamDocuments);
        spamProbability += (Math.log(priorProbabilityForSpam)) / Math.log(2);
        if (hamProbability > spamProbability) {
            return EmailClass.ham;
        } else {
            return EmailClass.spam;
        }
    }

    /*
    Method: test. wrapper method that calls the necesssary methods for testing the emails
    */
    void test(String hamEmailsDirectoryPath, String spamEmailsDirectoryPath,boolean noStopWordsRemoval) throws IOException {
        hashLexicons();
        //String hamEmailsDirectoryPath = "C:\\Users\\Praveen\\OneDrive\\Spring 2016\\Machine_Learning\\Assignment\\2\\test\\ham";
        findTruePositive(hamEmailsDirectoryPath, EmailClass.ham,noStopWordsRemoval);
        findTruePositive(spamEmailsDirectoryPath, EmailClass.spam,noStopWordsRemoval);
        //System.out.println("The accuracy in classifying the ham emails using Naive Bayes classifier is "+d*100);
        //String spamEmailsDirectoryPath = "C:\\Users\\Praveen\\OneDrive\\Spring 2016\\Machine_Learning\\Assignment\\2\\test\\spam";
        //System.out.println("The accuracy in classifying the spam emails using Naive Bayes classifier is "+findAccuracy(spamEmailsDirectoryPath, EmailClass.spam,noStopWordsRemoval)*100);
        System.out.println("Accuracy of email clasification using Naive Bayes Algorithm  is: " + (truePositive / (double) testSamples)*100);
    }
}
