package machinelearning_assignment1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/*
 Attribute class.
 Stores attribute name, the different values the attribute can have and other variables.
 */
class Attribute {

    String attributeName;    // Name of the attribute
    int[] attributeValues;   //Array of values that the attribute can have
    int positiveCount;       //Count of records that has decision value true below this attribute in the decison tree
    int negativeCount;      //Count of records that has decision value false below this attribute in the decison tree

    Attribute(String name, int val[]) {
        attributeName = name;
        attributeValues = new int[val.length];
        System.arraycopy(val, 0, attributeValues, 0, val.length);
        positiveCount = 0;
        negativeCount = 0;
    }
}

/*
 Class: TreeNode - Represents the structure of the tree.
 */
class TreeNode {

    int orderNumber = -1;   //order number is used by the pruning algorithm for the selection nodes to be removed. 
    Attribute attribute;
    TreeNode branches[];

    public TreeNode(Attribute a) {
        attribute = a;
        branches = new TreeNode[attribute.attributeValues.length];
    }
}

/*
 Record: used to model record of the sample data set.
 */
class Record {

    int recordIdentifier;
    HashMap<String, Integer> attibuteValues;
    boolean decisionValue;

    Record(int recordIdentifier, int recordvalues[], boolean decisionValue, List<Attribute> attributes) {
        attibuteValues = new HashMap<>();
        this.recordIdentifier = recordIdentifier;
        for (int i = 0; i < recordvalues.length; i++) {
            attibuteValues.put(attributes.get(i).attributeName, recordvalues[i]);
        }
        this.decisionValue = decisionValue;
    }
}

/*
 Class:RandomnessMeasureResult - Used to return the set of values.(postiveclasses count, negative class count & randomness measure)
 */
class RandomnessMeasureResult {

    int positiveDecisionCount;
    int negativeDecisionCount;
    double randomnessMeasure;

    public RandomnessMeasureResult(int p, int n, double r) {
        positiveDecisionCount = p;
        negativeDecisionCount = n;
        randomnessMeasure = r;
    }
}

/*
 Class: DecisionTree - Models the decision tree. Stores function and data necessary for decision tree
 */
public class DecisionTree {

    //Calculates the entropy measure
    RandomnessMeasureResult findEntropy(List<Record> t_records) {
        int trueCount = 0, falseCount = 0, totalRecords = t_records.size();
        for (Record r : t_records) {
            if (r.decisionValue == true) {
                trueCount++;
            }
        }
        falseCount = totalRecords - trueCount;
        double trueCountRatio = trueCount / (double) totalRecords;
        double falseCountRatio = falseCount / (double) totalRecords;
        if (trueCount != totalRecords && falseCount != totalRecords) {
            double entropy = (Math.log(trueCountRatio) / Math.log(2)) * trueCountRatio + (Math.log(falseCountRatio) / Math.log(2)) * falseCountRatio;
            entropy = entropy * -1;
            return new RandomnessMeasureResult(trueCount, falseCount, entropy);
        } else {
            return new RandomnessMeasureResult(trueCount, falseCount, 0);
        }
    }

    //Calculates the variance impurity measure
    RandomnessMeasureResult findVarianceImpurityMeasure(List<Record> t_records) {
        int K0 = 0, K1 = 0, totalRecords = t_records.size();
        for (Record r : t_records) {
            if (r.decisionValue == true) {
                K0++;
            }
        }
        K1 = totalRecords - K0;

        double varianceImpurity = (K0 / (double) totalRecords) * (K1 / (double) totalRecords);
        return new RandomnessMeasureResult(K0, K1, varianceImpurity);
    }

    //Filters the records that needs to be considered for finding the next node in the tree construction
    List<Record> filterRecords(List<Record> t_records, Attribute a, int attributeValue) {
        List<Record> rs = new ArrayList<>();
        for (Record r : t_records) {
            if (r.attibuteValues.get(a.attributeName).equals(attributeValue)) {
                rs.add(r);
            }
        }
        return rs;
    }

    //Chooses the best attribute at every step in teh tree construction
    Attribute chooseAttribute(List<Record> t_records, List<Attribute> attributes, HashMap<Attribute, Boolean> attributesHash, Boolean isEntropy) {
        double largestInfoGain = Integer.MIN_VALUE;
        Attribute chosenAttribute = null;
        //Find entropy of the entire set of trainingRecords
        RandomnessMeasureResult measureOfRandomness;
        if (isEntropy) {
            measureOfRandomness = findEntropy(t_records);
        } else {
            measureOfRandomness = findVarianceImpurityMeasure(t_records);
        }

        //If measureOfRandomness = 0, then decision values is reached. So no need find the new attribute
        if (measureOfRandomness.randomnessMeasure == 0) {
            int t[] = {-1, -1, -1};
            if (t_records.get(0).decisionValue) {
                chosenAttribute = new Attribute("True", t);
            } else {
                chosenAttribute = new Attribute("False", t);
            }
            return chosenAttribute;
        }

        for (Attribute a : attributes) {
            if (!attributesHash.containsKey(a)) {
                List<Record> rs;

                //Summation of entropies of all values of a particular attribute
                double sum = 0;
                int totalPositiveCount = 0;
                int totalNegativeCount = 0;
                for (int i = 0; i < a.attributeValues.length; i++) {
                    int currentAttibuteValue = a.attributeValues[i];
                    rs = filterRecords(t_records, a, currentAttibuteValue);
                    RandomnessMeasureResult measureOfRandomnessOnAttribute;
                    if (isEntropy) {
                        measureOfRandomnessOnAttribute = findEntropy(rs);
                    } else {
                        measureOfRandomnessOnAttribute = findVarianceImpurityMeasure(rs);
                    }
                    sum = sum + (rs.size() / (double) t_records.size()) * measureOfRandomnessOnAttribute.randomnessMeasure;
                    totalPositiveCount += measureOfRandomnessOnAttribute.positiveDecisionCount;
                    totalNegativeCount += measureOfRandomnessOnAttribute.negativeDecisionCount;
                }
                if ((measureOfRandomness.randomnessMeasure - sum) > largestInfoGain) {
                    largestInfoGain = measureOfRandomness.randomnessMeasure - sum;
                    a.positiveCount = totalPositiveCount;
                    a.negativeCount = totalNegativeCount;
                    chosenAttribute = a;
                }
            }
        }
        return chosenAttribute;
    }

    //This method recursively build the decision tree
    TreeNode buildDecisionTree(List<Record> records, List<Attribute> attributes, HashMap<Attribute, Boolean> attributesHash, boolean isEntropyMeasure) {
        if (!records.isEmpty()) {
            Attribute chosenAttribute = chooseAttribute(records, attributes, attributesHash, isEntropyMeasure);
            if (chosenAttribute != null) {
                TreeNode t = new TreeNode(chosenAttribute);
                if (!(chosenAttribute.attributeName.equals("True") || chosenAttribute.attributeName.equals("False"))) {
                    HashMap<Attribute, Boolean> t_attributesHash = new HashMap<>(attributesHash);
                    t_attributesHash.put(chosenAttribute, Boolean.TRUE);
                    for (int i = 0; i < t.branches.length; i++) {
                        List<Record> rs = filterRecords(records, chosenAttribute, chosenAttribute.attributeValues[i]);
                        TreeNode temp = buildDecisionTree(rs, attributes, t_attributesHash, isEntropyMeasure);

                        if (temp == null) {
                            if (t.attribute.attributeName.equals("True") || t.attribute.attributeName.equals("False")) {
                                t.branches[i] = null;
                            } else {
                                int val[] = {-1, -1, -1};
                                if (t.attribute.positiveCount > t.attribute.negativeCount) {
                                    t.branches[i] = new TreeNode(new Attribute("True", val));
                                } else {
                                    t.branches[i] = new TreeNode(new Attribute("False", val));
                                }
                            }
                        } else {
                            t.branches[i] = temp;
                        }

                    }
                }
                return t;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    void printRecord(Record r) {
        System.out.println(r.recordIdentifier);
        System.out.println(r.attibuteValues);
        System.out.println(r.decisionValue);
    }

    //method compares whether the decision value of the actual record and  the classification done by the constructed tree
    boolean checkClassificationOfRecord(TreeNode tree, Record r) {
        while (tree != null) {
            Attribute currentAttribute = tree.attribute;
            if (!(currentAttribute.attributeName.equals("True") || currentAttribute.attributeName.equals("False"))) {
                int attributeValueOfRecord = r.attibuteValues.get(currentAttribute.attributeName);
                tree = tree.branches[attributeValueOfRecord];
            } else {
                if (r.decisionValue && currentAttribute.attributeName.equals("True") || !r.decisionValue && currentAttribute.attributeName.equals("False")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /*
    Method: findAccuracy. Finds the accuracy of the tree built
    */
    double findAccuracy(TreeNode t, List<Record> testRecords) {
        int correctlyClassified = 0, mistakenlyClassified = 0;
        for (Record r : testRecords) {
            if (checkClassificationOfRecord(t, r)) {
                correctlyClassified++;
            } else {
                mistakenlyClassified++;
            }
        }
        return correctlyClassified / (double) (correctlyClassified + mistakenlyClassified);
    }

    /*
    Method: makeCopy. Creates a exact copy of the current tree.
    */
    TreeNode makeCopy(TreeNode tree) {
        if (tree.attribute.attributeName.equals("True") || tree.attribute.attributeName.equals("False")) {
            TreeNode t = new TreeNode(tree.attribute);
            return t;
        } else {
            TreeNode t = new TreeNode(tree.attribute);
            for (int i = 0; i < tree.branches.length; i++) {
                t.branches[i] = makeCopy(tree.branches[i]);
            }
            return t;
        }
    }

    int order = 0; // Used to give numbers to tree's node

    /*
    Method: orderNodes. Assigning order numbers to only non-leaf nodes
    */
    void orderNodes(TreeNode tree) {
        if (tree.attribute.attributeName.equals("True") || tree.attribute.attributeName.equals("False")) {
            return;
        } else {
            tree.orderNumber = order;
            order++;
            for (int i = 0; i < tree.branches.length; i++) {
                orderNodes(tree.branches[i]);
            }
        }
    }

    Random r = new Random();

    //method replaces the subtree having the root node that has a randomly generated order number with the appropriate leaf node
    static TreeNode replaceSubTree(TreeNode tree, int position) {
        if (!(tree.attribute.attributeName.equals("True") || tree.attribute.attributeName.equals("False"))) {
            if (tree.orderNumber == position) {
                int val[] = {-1, -1, -1};
                if (tree.attribute.positiveCount > tree.attribute.negativeCount) {
                    return new TreeNode(new Attribute("True", val));
                } else {
                    return new TreeNode(new Attribute("False", val));
                }
            } else {
                for (int i = 0; i < tree.branches.length; i++) {
                    tree.branches[i] = replaceSubTree(tree.branches[i], position);
                }
                return tree;
            }

        } else {
            return tree;
        }
    }

    //Method: Prunes the tree
    TreeNode prune(TreeNode originalTree, int l, int k, List<Record> vrecords) {
        double bestAccuracy = findAccuracy(originalTree, vrecords);   //dummy. have to be changed
        TreeNode bestTree = originalTree;
        for (int i = 1; i < l; i++) {

            TreeNode dTree = makeCopy(originalTree);
            int m = r.nextInt(k) + 1;
            for (int j = 1; j < m; j++) {
                order = 0;
                orderNodes(dTree);
                int p = r.nextInt(order) + 1;
                dTree = replaceSubTree(dTree, p);
            }
            double accuracy = findAccuracy(dTree, vrecords); //dummy - have to be changed
            if (accuracy > bestAccuracy) {
                bestTree = dTree;
            }
        }
        return bestTree;
    }

    /*
    Method: readInput. Reads the input records from different files
    */
    void readInput(List<Record> records, String fileName, boolean isTraining, List<Attribute> attributes) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        String frecord = "";

        int recordIdentifier = 0;
        while ((frecord = br.readLine()) != null) {
            String frecordArray[] = frecord.split(",");

            if (recordIdentifier == 0) {
                if (isTraining) {
                    int values[] = {0, 1};
                    for (int i = 0; i < frecordArray.length - 1; i++) {
                        attributes.add(new Attribute(frecordArray[i], values));
                    }
                }
            } else {
                int recordValues[] = new int[frecordArray.length - 1];
                for (int i = 0; i < frecordArray.length - 1; i++) {
                    recordValues[i] = Integer.parseInt(frecordArray[i]);
                }
                String t_decisionValue = frecordArray[frecordArray.length - 1];
                boolean decisionValue = false;
                if (t_decisionValue.equals("1")) {
                    decisionValue = true;
                } else {
                    decisionValue = false;
                }
                Record r = new Record(recordIdentifier, recordValues, decisionValue, attributes);
                records.add(r);

            }
            recordIdentifier++;
        }
    }

    void printTree(TreeNode tree) throws IOException {
        Queue<TreeNode> q = new ArrayDeque<>();
        q.add(tree);
        while (!q.isEmpty()) {
            TreeNode temp = q.poll();

            for (int i = 0; i < temp.branches.length; i++) {
                TreeNode t = temp.branches[i];
                if (!(t.attribute.attributeName.equals("True") || t.attribute.attributeName.equals("False"))) {
                    q.add(t);
                }
                System.out.println(temp.attribute.attributeName + " on reading " + i + " moves to " + t.attribute.attributeName);
            }
        }
    }
    /*
     Method: _printTree - Prints the tree with proper indentation so as to represent the tree hierarchy.  
     */

    void _printTree(TreeNode tree, int numberOfSpaces) {
        if (tree.attribute.attributeName.equals("True")) {
            System.out.print(" : 1");
        } else if (tree.attribute.attributeName.equals("False")) {
            System.out.print(" : 0");
        } else {
            for (int i = tree.branches.length - 1; i >= 0; i--) {
                System.out.println();
                for (int j = 0; j < numberOfSpaces; j++) {
                    System.out.print("| ");
                }
                System.out.print(tree.attribute.attributeName + " : " + i);
                _printTree(tree.branches[i], numberOfSpaces + 1);
            }
        }
    }
}
