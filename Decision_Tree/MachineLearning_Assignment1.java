package machinelearning_assignment1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
Driver Class
*/
public class MachineLearning_Assignment1 {

    public static void main(String[] args) throws IOException {
        
        /*
        <L> <K> <training-set> <validation-set> <test-set> <to-print>
L: integer (used in the post-pruning algorithm)
K: integer (used in the post-pruning algorithm)
to-print:{yes,no}
        */
        
        int l=Integer.parseInt(args[0]);
        int k=Integer.parseInt(args[1]);
        String trainingSetFileName=args[2]; //"C:\\Users\\Praveen\\OneDrive\\Spring 2016\\Machine_Learning\\Assignment\\1\\data_sets1\\training_set.csv"
        String validationSetFileName=args[3]; //"C:\\Users\\Praveen\\OneDrive\\Spring 2016\\Machine_Learning\\Assignment\\1\\data_sets1\\validation_set.csv"
        String testSetFileName=args[4];  //"C:\\Users\\Praveen\\OneDrive\\Spring 2016\\Machine_Learning\\Assignment\\1\\data_sets1\\test_set.csv"
        String toPrint=args[5];
        
        boolean isPrint=false;
        if(toPrint.equals("yes"))
        {
            isPrint=true;
        }
        
        //List to store records of different data sets 
        List<Record> trainingRecords = new ArrayList<>();
        List<Record> testRecords = new ArrayList<>();
        List<Record> validationRecords=new ArrayList<>();
        
        //Instantiate teh decision tree class
        DecisionTree dt= new DecisionTree();

        List<Attribute> attributes = new ArrayList<>(); // Stores a list of attributes of the dataset
        HashMap<Attribute, Boolean> attributeHash = new HashMap<>();  // Used to keep track of attributes that are already used in constructing a tree
        
        //Reads the training data
        dt.readInput(trainingRecords,trainingSetFileName,true,attributes);
        
        // Building decision tree with Entropy measure
        TreeNode decisionTree = dt.buildDecisionTree(trainingRecords, attributes, attributeHash,true);  // Using entropy measure
       
        dt.orderNodes(decisionTree);
        System.out.println("Decsion Tree using entropy measure");
        System.out.println("Number of non-leaf nodes before pruning: "+dt.order); 
        
        //Reads the test data
        dt.readInput(testRecords,testSetFileName,false,attributes);
        System.out.println("Accuracy before pruning using entropy measure: "+ dt.findAccuracy(decisionTree,testRecords)*100+"%");
        
        if(isPrint)
        {
            System.out.println("Decision tree using entropy measure before pruning");
            dt._printTree(decisionTree,0);
            System.out.println();
        }
        //Reads the validation data
        dt.readInput(validationRecords,validationSetFileName, false, attributes);
        
        TreeNode pruned = dt.prune(decisionTree, l, k,validationRecords);
        System.out.println("Number of non-leaf nodes after complete pruning: "+dt.order);
        System.out.println("Accuracy of the best tree after pruning using entropy measure: "+dt.findAccuracy(pruned, testRecords)*100+"%");

        if(isPrint)
        {
            System.out.println("Decision tree using entropy measure after pruning");
            dt._printTree(pruned,0); 
            System.out.println();
        }
        System.out.println();
        //Building decision tree with variance impurity measure
        
        TreeNode decisionTreeVI = dt.buildDecisionTree(trainingRecords, attributes, attributeHash,false);  // Using entropy measure
        dt.order=0;
        dt.orderNodes(decisionTreeVI);
        System.out.println("Decsion Tree using variance impurity measure");
        System.out.println("Number of non-leaf nodes before pruning: "+dt.order);
        System.out.println("Accuracy before pruning using variance impurity measure: "+ dt.findAccuracy(decisionTreeVI,testRecords)*100+"%");

        if(isPrint)
        {
            System.out.println("Decision tree using variance impurity measure before pruning");
            dt._printTree(decisionTreeVI,0); 
            System.out.println();
        }
        
        TreeNode prunedVI = dt.prune(decisionTreeVI, l, k,validationRecords);
        System.out.println("Number of non-leaf nodes after complete pruning: "+dt.order);
        System.out.println("Accuracy of the best tree after pruning using variance impurity measure: "+dt.findAccuracy(prunedVI, testRecords)*100+"%");
        
        if(isPrint)
        {
            System.out.println("Decision tree using variance impurity measure after pruning");
            dt._printTree(prunedVI,0); 
            System.out.println();
        }
    }
    
}
