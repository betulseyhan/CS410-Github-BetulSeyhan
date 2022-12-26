import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class Rule {
    String variable;
    ArrayList<String> terminals;

    public Rule(String variable) {
        this.variable = variable;
        terminals = new ArrayList<>();
    }

    public void addRule(String terminal){
        terminals.add(terminal);
    }

    public String getVariable() {
        return variable;
    }

    public ArrayList<String> getTerminals() {
        return terminals;
    }
}
class BETUL_SEYHAN_S017566 {
    public static void main(String[] args) {
        //Reading necessary input from txt file
        ArrayList<String> readList = new ArrayList<>();
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader("G2.txt"));
            String line = reader.readLine();
            while (line != null) {
                readList.add(line);
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int inonTerminal = readList.indexOf("NON-TERMINAL");
        int iterminal = readList.indexOf("TERMINAL");
        int irules = readList.indexOf("RULES");
        int istart = readList.indexOf("START");
        int iend =  readList.size();


        // Creating necessary attributes of automaton by reading from txt
        ArrayList<String> nonTerminal = new ArrayList<>(readList.subList(inonTerminal + 1, iterminal));
        ArrayList<String> terminal = new ArrayList<>(readList.subList(iterminal + 1, irules));
        ArrayList<String> rule = new ArrayList<>(readList.subList(irules + 1, istart));
        String start = readList.subList(istart + 1, iend).get(0);

        ArrayList<Rule> rulesArrayList = new ArrayList<>();
        for (String nonTerminals :nonTerminal) {
            Rule rule1 = new Rule(nonTerminals); //Don't pick up the phone
            for (String rules : rule) {
                String splitted[] =rules.split(":",2);
                if(splitted[0].contains(nonTerminals)){
                    rule1.addRule(splitted[1]);
                }
            }
            rulesArrayList.add(rule1);
        }

        Chomsky chomsky = new Chomsky(nonTerminal,rulesArrayList,terminal,start);
        chomsky.createChomsky();
    }
}

 class Chomsky {
    ArrayList<String> nonTerminals;
    ArrayList<Rule> rules;
    ArrayList<String> terminals;
    String start;

    ArrayList<String>newNonTerminals;


    public Chomsky(ArrayList<String> nonTerminals, ArrayList<Rule> rules, ArrayList<String> terminals, String start) {
        this.nonTerminals = nonTerminals;
        this.rules = rules;
        this.terminals = terminals;
        this.start = start;
        this.newNonTerminals = new ArrayList<>();
    }

    void createChomsky() {
        System.out.println("NON-TERMINAL");
        for(String n :nonTerminals)
            System.out.println(n);
        System.out.println("TERMINAL");
        for(String t : terminals)
            System.out.println(t);
        System.out.println("RULES");
        for (Rule s : rules) {
            for(String m :s.getTerminals()){
                System.out.println(s.variable + " : " + m);
            }
        }
        System.out.println("START");
        System.out.println(this.start);
        //First step crate new start variable
        Rule startRule = new Rule("S0");
        startRule.addRule(start);
        rules.add(startRule);
        nonTerminals.add(startRule.getVariable());
        this.start = startRule.getVariable();

        char c;
        for(c = 'A'; c <= 'Z'; ++c){
            if(!nonTerminals.contains(String.valueOf(c))){
                newNonTerminals.add(String.valueOf(c));
            }
        }

        addTerminalRules();


        eliminate();


        findEpsilon();

        removeUnitRule();

        System.out.println("-------------------------");
        System.out.println("NON-TERMINAL");
        for(String n :nonTerminals)
            System.out.println(n);
        System.out.println("TERMINAL");
        for(String t : terminals)
            System.out.println(t);
        System.out.println("RULES");
        for (Rule s : rules) {
            for(String m :s.getTerminals()){
                System.out.println(s.variable + " : " + m);
            }
        }
        System.out.println("START");
        System.out.println(startRule.getVariable());
    }

    private void addTerminalRules() {
        // In production rule A->aAS |aS and B-> SbS|aAS|aS, terminals a and b exist on RHS with non-terminates. Removing them from RHS:
     ArrayList<String>list= new ArrayList<>();
      for(int i = 0; i<rules.size(); i++){
          Rule r1 = rules.get(i);
          for(int j= 0; j<r1.getTerminals().size();j++){
            String m = r1.getTerminals().get(j);
            if(m.length()>1){
                int a= 0;
                int b= 0;
                for(int x= 0; x<m.length();x++){
                    if(terminals.contains(String.valueOf(m.charAt(x)))){
                        ++a;
                    }
                    if(nonTerminals.contains(String.valueOf(m.charAt(x)))){
                        ++b;
                    }
                }
                if((a>0 && b>0)|| (a>0 && b==0)){
                    for(int x= 0; x<m.length();x++){
                        if(terminals.contains(String.valueOf(m.charAt(x)))){
                            if(!list.contains(String.valueOf(m.charAt(x))))
                                list.add(String.valueOf(m.charAt(x)));
                        }
                    }
                }
            }
          }
      }
      ArrayList<Rule>newRules= new ArrayList<>();
      for(String s : list) {
          Rule newRule = new Rule(newNonTerminals.remove(0));
          newRule.addRule(String.valueOf(s));
          nonTerminals.add(newRule.getVariable());
          newRules.add(newRule);
          rules.add(newRule);
      }
        for(int i = 0; i<rules.size(); i++) {
            Rule r1 = rules.get(i);
            ArrayList<String>addList =new ArrayList<>();
            ArrayList<String>removeList = new ArrayList<>();
            for (int j = 0; j < r1.getTerminals().size(); j++) {
                String m = r1.getTerminals().get(j);
                if (m.length() > 1) {
                    for(int k= 0 ;k<newRules.size();k++){
                        if(m.contains(newRules.get(k).getTerminals().get(0))){
                            String str= "";
                            for(int s=0; s<m.length();s++){
                                if(String.valueOf(m.charAt(s)).equals(newRules.get(k).getTerminals().get(0))){
                                    str= str+ newRules.get(k).getVariable();
                                }
                                else
                                    str = str+ String.valueOf(m.charAt(s));
                            }

                            removeList.add(m);
                            addList.add(str);
                        }
                    }
                }
            }
           for(String m : removeList){
               r1.getTerminals().remove(m);
           }
            for(String m : addList){
                r1.getTerminals().add(m);
            }
        }

    }


    private void eliminate() {
        ArrayList<Rule> newRules = new ArrayList<>();
        for(int i=0;i<rules.size();i++){
            Rule rule= rules.get(i);
           for(int j=0 ; j<rule.getTerminals().size();j++){
               String m = rule.getTerminals().get(j);
                    while(m.length()>2){
                        rule.getTerminals().remove(m);
                        j--;
                        String s= (String.valueOf(m.charAt(0)))+(String.valueOf(m.charAt(1)));
                        int z = 0;
                        for(int a=0; a<newRules.size();a++){
                           if(newRules.get(a).getTerminals().get(0).equals(s)){
                               m=newRules.get(a).getVariable()+m.substring(2);
                               z++;
                           }
                        }
                        if(z==0) {
                            Rule newRule = new Rule(newNonTerminals.remove(0));
                            String c = newRule.getVariable();
                            nonTerminals.add(c);
                            m = c + m.substring(2);
                            newRule.addRule(s);
                            rules.add(newRule);
                            newRules.add(newRule);
                        }
                            rule.getTerminals().add(m);
                        }
                    }
                }
        }
    private void removeUnitRule() {

        for(int i = 0; i<rules.size(); i++) {
            Rule rule2 =rules.get(i);
            for(int j = 0; j<rule2.getTerminals().size();j++) {
                String ilu = rule2.getTerminals().get(j);
                if (rule2.getVariable().equals(ilu)) {
                    rule2.getTerminals().remove(ilu);
                } else {
                    for(int k = 0; k<rules.size(); k++) {
                        if (rules.get(k).getVariable().equals(ilu)&& !rules.get(k).getVariable().equals(rule2.getVariable())) {
                            //rule2.getTerminals().remove(ilu);
                            for(String s : rules.get(k).getTerminals()){
                                if(!rule2.getTerminals().contains(s))
                                    rule2.addRule(s);
                            }
                        }
                    }
                }
            }
        }
        for(int i = 0; i<rules.size(); i++) {
            for (String m : nonTerminals) {
                if (rules.get(i).getTerminals().contains(m))
                    rules.get(i).getTerminals().remove(m);
            }
        }
    }

    private void findEpsilon() {
        ArrayList<String> epsilon = new ArrayList<>();
        for (int i = 0; i < rules.size(); i++) {
            Rule rule1 = rules.get(i); //Don't pick up the phone
            for (int j = 0; j < rule1.getTerminals().size(); j++) {
                if (rule1.getTerminals().get(j).contains("e")) {
                    epsilon.add(rule1.getVariable());
                    rule1.getTerminals().remove(rule1.getTerminals().get(j));
                }
            }
        }

        if (!epsilon.isEmpty()) {
            int size = rules.size();
            for (String m : epsilon) {
                for (int k =0;k<size;k++) {
                    int sugar = rules.get(k).getTerminals().size();
                    if (!m.equals(rules.get(k).getVariable())) {
                        for (int j = 0; j <sugar; j++) {
                            if (rules.get(k).getTerminals().get(j).contains(m) ) {
                                List<List<Integer>> wtf= findIndex(rules.get(k).getTerminals().get(j), m);
                                for (int i = 0;i <wtf.size();i++){
                                    StringBuilder
                                            str
                                            = new StringBuilder(rules.get(k).getTerminals().get(j));
                                    int no = 0;
                                    for(int a =0;a<wtf.get(i).size(); a++){
                                        int x = (wtf.get(i).get(a))-no;
                                        str.deleteCharAt(x);
                                        ++no;
                                    }
                                    StringBuilder e
                                            = new StringBuilder("e");
                                    if(str.length()==0) {
                                        str = e;
                                    }
                                    if(!rules.get(k).getTerminals().contains(String.valueOf(str)))
                                        rules.get(k).addRule(String.valueOf(str));
                                }
                            }
                        }
                    }
                }
            }
            findEpsilon();
        }
    }

    public List<List<Integer>> findIndex(String rule, String m) {
        ArrayList<Integer> indexList = new ArrayList<>();

        String word = rule;
        String letter = m;
        int indx = word.indexOf(letter);

        while (indx != -1) {
            indexList.add(indx);
            indx = word.indexOf(letter, indx + 1);
        }

        return createSubset(indexList);
    }
    private static List<List<Integer>> createSubset(ArrayList<Integer> setArrayList) {
        List<List<Integer>> subsets = new ArrayList<>();
        int setSize = setArrayList.size();
        for (int i1 = 0; i1 < (1 << setSize); i1++) {
            List<Integer> subsetList = new ArrayList<Integer>();
            for (int i = 0; i < setSize; i++) {
                if ((i1 & (1 << i)) != 0) {
                    subsetList.add(setArrayList.get(i));
                }
            }
            subsets.add(subsetList);
        }
        return subsets;
    }

}

