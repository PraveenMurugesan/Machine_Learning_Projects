
//package ml_hw2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/*
Driver class. 
The class contains the main function used to perform Naive Bayes classification 
and Logistic regression classification
*/
public class ML_HW2 {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner s= new Scanner(System.in);
        
        System.out.println("Enter learning rate for logistic regression: ");
        
        double learningRate= s.nextDouble();//0.05;
        
        System.out.println("Enter regularization factor for logistic regression: ");
        double regularizationFactor=s.nextDouble();//0.01;
        System.out.println();
        System.out.println("Performance of the algorithms without removing the stop words as follows");
        
        System.out.println();
        TrainingData td1= new TrainingData();
        td1.train(args[0],args[1],args[4],true);
        NaiveBayesClassifier nb1= new NaiveBayesClassifier(td1);
        nb1.test(args[2],args[3],true);//"C:\\Users\\Praveen\\OneDrive\\Spring 2016\\Machine_Learning\\Assignment\\2\\mytest\\ham","C:\\Users\\Praveen\\OneDrive\\Spring 2016\\Machine_Learning\\Assignment\\2\\mytest\\spam",true);//args[2],args[3],true);
        System.out.println();
        LogisticRegression lr1= new LogisticRegression(td1,learningRate,regularizationFactor);
        lr1.train();
        lr1.test(args[2],args[3],true);
        System.out.println();
        
        System.out.println("Performance of the algorithms after removing the stop words as follows");
        TrainingData td= new TrainingData();
        td.train(args[0],args[1],args[4],false);
        
        System.out.println();
        NaiveBayesClassifier nb= new NaiveBayesClassifier(td);
        nb.test(args[2],args[3],false);
        System.out.println();
        LogisticRegression lr= new LogisticRegression(td,learningRate,regularizationFactor);
        lr.train();
        lr.test(args[2],args[3],false);
        
    }
}
