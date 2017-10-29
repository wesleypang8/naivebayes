package naivebayes;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class NaiveBayes {
    
    //  This function reads in a file and returns a 
    //  set of all the tokens. It ignores the subject line
    //
    //  If the email had the following content:
    //
    //  Subject: Get rid of your student loans
    //  Hi there ,
    //  If you work for us, we will give you money
    //  to repay your student loans . You will be 
    //  debt free !
    //  FakePerson_22393
    //
    //  This function would return to you
    //  [hi, be, student, for, your, rid, we, get, of, free, if, you, us, give, !, repay, will, loans, work, fakeperson_22393, ,, ., money, there, to, debt]
    public static HashSet<String> tokenSet(File filename) throws IOException {
        HashSet<String> tokens = new HashSet<String>();
        Scanner filescan = new Scanner(filename);
        filescan.next(); //Ignoring "Subject"
        while(filescan.hasNextLine() && filescan.hasNext()) {
            tokens.add(filescan.next());
        }
        filescan.close();
        return tokens;
    }
    
    public static void main(String[] args) throws IOException {
        File test = new File(args[0]+"/test");
        File ham = new File(args[0]+"/train/ham");
        File spam = new File(args[0]+"/train/spam");
        
        int numSpam = 0;
        int numHam = 0;
        
        //map words to number of times they appear
        Map<String, Double> spamMap = new HashMap<String,Double>();
        Map<String, Double> hamMap = new HashMap<String,Double>();
        
        //count up words
        for(File f: spam.listFiles()){
            for(String s : tokenSet(f)){
                if(!spamMap.containsKey(s)){
                    spamMap.put(s, 0.0);
                }
                spamMap.put(s,spamMap.get(s)+1);
            }
            numSpam++;
        }
        
        for(File f: ham.listFiles()){
            for(String s : tokenSet(f)){
                if(!hamMap.containsKey(s)){
                    hamMap.put(s, 0.0);
                }
                hamMap.put(s,hamMap.get(s)+1);
            }
            numHam++;
        }
        
        double pSpam = ((double)numSpam)/(numSpam+numHam);
        double pHam = ((double)numHam)/(numSpam+numHam);
        
        //change to map words to p(w|(spam/ ham))
        for(String s : spamMap.keySet()){
            spamMap.put(s, (spamMap.get(s)+1.0)/(numSpam+2.0));

        }

        for(String s : hamMap.keySet()){
            hamMap.put(s, (hamMap.get(s)+1.0)/(numHam+2.0));
        }


        //calculate for each test file
        for(File f : test.listFiles()){
            double givenSpam = Math.log(pSpam);
            double givenHam = Math.log(pHam);
            
            Set<String> testWords = tokenSet(f);
            for(String s : testWords){
                if(spamMap.containsKey(s)){
                    givenSpam+=Math.log(spamMap.get(s));
                } else {
                    givenSpam+=Math.log(1.0/(numSpam+2.0));
                }
            }
            

            for(String s : testWords){
                if(hamMap.containsKey(s)){
                    givenHam+=Math.log(hamMap.get(s));
                }else {
                    givenHam+=Math.log(1.0/(numHam+2.0));
                }
            }
            
            
            System.out.print(f.getName() );
            if(givenSpam>givenHam){
                System.out.println(" spam");
            }else{
                System.out.println(" ham");
            }
        }
        
        
        
        
    }
}
