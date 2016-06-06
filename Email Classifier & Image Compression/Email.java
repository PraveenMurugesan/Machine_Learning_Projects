//package ml_hw3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

enum EmailClass
{
    ham,spam;
}

public class Email {

    StringBuilder message;
    HashMap<String,Integer> processedMessage;
    LinkedList<String> processedMessageList;
    EmailClass ec;
    Email(StringBuilder s) throws IOException
    {
        message=new StringBuilder(s);
    }

    Email(File emailFile,EmailClass ec) throws IOException {
        message = new StringBuilder();
        processedMessage=new HashMap<>();
        processedMessageList =  new LinkedList<>();
        readEmail(emailFile);
        this.ec=ec;
    }

    void readEmail(File emailFile) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(emailFile));
        String emailLine = new String();
        while ((emailLine = br.readLine()) != null) {
            message.append(emailLine);
            message.append(" ");
        }
    }

    void writeEmail(Email email,BufferedWriter bw) throws IOException
    {
        for(String s : email.processedMessageList)
        {
            bw.write(s+" ");
        }
        bw.newLine();
        bw.write("------------------------------------------------------------");
        bw.newLine();
    }
    void preprocessEmail(HashSet<String> stopwords, boolean noRemovalOfStopWords) throws IOException {
        StringBuilder message = this.message;
        for (int i = 0; i < message.length(); i++) {
            if (!((message.charAt(i) >= 65 && message.charAt(i) <= 90) || (message.charAt(i) >= 97 && message.charAt(i) <= 122))) {
                message.replace(i, i + 1, " ");
            }
        }
        message.append(" ");
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c != ' ') {
                temp.append(c);
            } else {
                if (temp.length() != 0)
                {
                    String tempString=temp.toString().toLowerCase();
                    if (noRemovalOfStopWords || !stopwords.contains(tempString)) {
                        if(this.processedMessage.containsKey(tempString))
                        {
                            int count=this.processedMessage.get(tempString);
                            this.processedMessage.put(tempString, count+1);
                        }
                        else
                        {
                            this.processedMessage.put(tempString, 1);
                            this.processedMessageList.add(tempString);
                        }
                    }
                    temp = new StringBuilder();
                }
            }
        }
    }
}
