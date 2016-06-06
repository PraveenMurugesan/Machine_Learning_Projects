//package ml_hw2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class Email {

    StringBuilder message;
    HashMap<String,Integer> processedMessage;
    EmailClass ec;
    
    Email(StringBuilder s)
    {
        message=new StringBuilder(s);
    }

    Email(File emailFile,EmailClass ec) throws IOException {
        message = new StringBuilder();
        processedMessage=new HashMap<>();
        readEmail(emailFile);
        this.ec=ec;
    }

    void readEmail(File emailFile) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(emailFile));
        String emailLine = new String();
        while ((emailLine = br.readLine()) != null) {
            message.append(emailLine);
        }
    }

    void preprocessEmail(HashSet<String> stopwords, boolean noRemovalOfStopWords) {
        StringBuilder message = this.message;
        for (int i = 0; i < message.length(); i++) {
            if (!((message.charAt(i) >= 65 && message.charAt(i) <= 90) || (message.charAt(i) >= 97 && message.charAt(i) <= 122))) {
                message.replace(i, i + 1, " ");
            }
        }
        message.append(" ");
        StringBuilder temp = new StringBuilder();
        this.processedMessage = new HashMap<>();
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
                        }
                    }
                    temp = new StringBuilder();
                }
            }
        }
    }
}
