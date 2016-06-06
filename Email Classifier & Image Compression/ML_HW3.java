//package ml_hw3;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Scanner;

public class ML_HW3 {
    
    public static void main(String[] args) throws IOException {
        Scanner s= new Scanner(System.in);
        System.out.println("Enter learning rate for perceptron: ");
        double learningRate= s.nextDouble();//0.05;
        System.out.println();
        
        System.out.println("Performance of the algorithms after removing the stop words as follows");
        TrainingData td1= new TrainingData();
        td1.train(args[0],args[1],args[4],false);
        NeuralNetworks nn= new NeuralNetworks(td1,learningRate);
        nn.train();
        nn.test(args[2],args[3],false);
        System.out.println();
        
        System.out.println("Performance of the algorithms without removing the stop words as follows");
        TrainingData td2= new TrainingData();
        td2.train(args[0],args[1],args[4],true);
        NeuralNetworks nn1= new NeuralNetworks(td2,learningRate);
        nn1.train();
        nn1.test(args[2],args[3],true);
        System.out.println();
        
        System.out.println("Image compression using K-means clustering");
        System.out.println("Enter the folder name to save the compressed images"); 
        String outputFolder = s.next(); //C:\Users\Praveen\OneDrive\Spring%202016\Machine_Learning\Assignment\3\Koala.jpg""C:\\Users\\Praveen\\OneDrive\\Spring_2016\\Machine_Learning\\Assignment\\3\\Koala.jpg"
        System.out.println("Enter the number of K values to be tested: ");
        int n=s.nextInt();
        KMeans k = new KMeans();
        k.compressImage(args[5],outputFolder,n);
    }
}
