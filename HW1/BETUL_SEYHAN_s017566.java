import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;

public class BETUL_SEYHAN_s017566 {
    public static void main(String[] args) {

        //Reading necessary input from txt file
        ArrayList<String> readList = new ArrayList<>();
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader("NFA1.txt"));
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

        int iAlphabet = readList.indexOf("ALPHABET");
        int iStates = readList.indexOf("STATES");
        int iStart = readList.indexOf("START");
        int iFinal = readList.indexOf("FINAL");
        int iTransitions = readList.indexOf("TRANSITIONS");
        int iEnd = readList.indexOf("END");

        // Creating necessary attributes of automaton by reading from txt
        ArrayList<String> alphabetList = new ArrayList<>(readList.subList(iAlphabet + 1, iStates));
        ArrayList<String> stateList = new ArrayList<>(readList.subList(iStates + 1, iStart));
        String start = readList.subList(iStart + 1, iFinal).get(0);
        ArrayList<String> finalList = new ArrayList<>(readList.subList(iFinal + 1, iTransitions));
        ArrayList<String> transitionsList = new ArrayList<>(readList.subList(iTransitions + 1, iEnd));

        //Crate NFA's Start States
        State startState = new State(start);
        FiniteAutomaton NFA = new FiniteAutomaton(startState);
        NFA.checkAndAdd(startState);

        //Set NFA's Alphabet
        NFA.setAlphabetList(alphabetList);

        //Creating NFA's Final State
        for (String state : finalList) {
            State finalState = new State(state);
            NFA.addFinalState(finalState);
            NFA.checkAndAdd(finalState);
        }

        // Creating and adding NFA's states
        for (String stateName : stateList) {
            State newState = new State(stateName);
            NFA.checkAndAdd(newState);
        }

        // Creating NFA's Transitions
        for (String str : transitionsList) {
            String[] list = str.split(" ");
            for (State state : NFA.getStateList()) {
                if (list[0].equals(state.getStateName())) {
                    Transition transition = new Transition(list[1], NFA.isThereExistsState(list[2]));
                    state.addToTransitionList(transition);

                }

            }

        }

        // Creating and Printing DFA
        System.out.println("Non-Deterministic Finite Automaton Results "+"\n");
        NFA.print();
        FiniteAutomaton DFA = NFAtoDFA_converter(NFA);
        System.out.println("\n"+"------------------------------------------------------"+"\n");
        System.out.println("Deterministic Finite Automaton Results "+"\n");
        DFA.print();



    }
    public static FiniteAutomaton NFAtoDFA_converter(FiniteAutomaton NFA) {

        State startState = new State(NFA.getStartState().getStateName());
        FiniteAutomaton DFA = new FiniteAutomaton(startState);
        DFA.setAlphabetList(NFA.getAlphabetList());
        DFA.checkAndAdd(startState);
        createDFAStates(DFA, startState, NFA);

        return DFA;
    }

    private static void createDFAStates(FiniteAutomaton DFA, State state, FiniteAutomaton NFA) {

        // look each alphabet in the alphabet list
        for (String alphabet : NFA.getAlphabetList()) {
            String nameOfStateRepeated = "";
            // take one by nfa states
            for (State nfaState : NFA.getStateList()) {
                // check if our DFA states contains nfa state
                if (state.getStateName().contains(nfaState.getStateName())) {
                    //calculate where we can go from our current state using given alphabet
                    for (Transition transition : nfaState.getTransitionsList()) {
                        if (transition.getAlphabet().equals(alphabet))
                            nameOfStateRepeated += transition.getNextState().getStateName();
                    }
                }
            }
            String nameOfState = "";
            if (!nameOfStateRepeated.isEmpty()) {
                //Remove the repeated char from the string to prevent creating incorrect states
                char str[] = nameOfStateRepeated.toCharArray();
                HashSet<Character> set = new LinkedHashSet<>(str.length - 1);
                // Repetition is not allowed in HashSet
                for (char x : str)
                    set.add(x);
                //create a state which consist of unique states
                for (char s : set)
                    nameOfState += s;
            }
            // If name of State is empty,it means it is going to SINK state
            else {
                nameOfState = "SINK";
            }
            State newState = DFA.isThereExistsState(nameOfState);
            //Check state is already exists or not
            if (Objects.isNull(newState)) {
                newState = new State(nameOfState);
                //Checking the state is in the final state of NFA or not
                for (State finalState : NFA.getFinalState()) {
                    if (newState.getStateName().contains(finalState.getStateName())) {
                        DFA.addFinalState(newState);
                    }
                }
                // Adding newly created state to DFA
                DFA.checkAndAdd(newState);
            }
            //Create and Add transition for DFA state
            Transition newTransition = new Transition(alphabet, newState);
            state.addToTransitionList(newTransition);

            //check the newState transition if empty call the function recursively to calculate new states
            if (newState.getTransitionsList().isEmpty()) {
                createDFAStates(DFA, newState, NFA);
            }
        }
    }
}

class State {
    private String stateName;

    private ArrayList<Transition> transitionsList;

    State(String stateName){

        this.stateName = stateName;
        this.transitionsList = new ArrayList<>();

    }

    public String getStateName() {
        return this.stateName;
    }

    public ArrayList<Transition> getTransitionsList() {
        return this.transitionsList;
    }

    public void addToTransitionList(Transition transition){

        this.transitionsList.add(transition);

    }
}
class Transition {
    private String alphabet;

    private State nextState;

    Transition(String alphabet, State nextState){

        this.alphabet = alphabet;
        this.nextState = nextState;

    }
    public String getAlphabet() {
        return alphabet;
    }

    public State getNextState() {
        return nextState;
    }

}
class FiniteAutomaton {

    private State startState;
    private ArrayList<String> alphabetList;
    private ArrayList<State> stateList;
    private ArrayList<State> finalState;

    FiniteAutomaton(State startState) {

        this.alphabetList = new ArrayList<>();
        this.stateList = new ArrayList<>();
        this.finalState = new ArrayList<>();
        this.startState = startState;

    }

    void checkAndAdd(State state) {

        if (Objects.isNull(isThereExistsState(state.getStateName()))){
            stateList.add(state);
        }
    }
    void addFinalState(State state) {
        finalState.add(state);
    }

    State isThereExistsState(String name) {
        return stateList.stream().filter(state -> name.equals(state.getStateName())).findFirst().orElse(null);
    }

    public ArrayList<String> getAlphabetList() {
        return alphabetList;
    }

    public void setAlphabetList(ArrayList<String> alphabetList) {
        this.alphabetList = alphabetList;
    }

    public ArrayList<State> getStateList() {
        return stateList;
    }

    public ArrayList<State> getFinalState() {
        return finalState;
    }

    public State getStartState() {
        return startState;
    }

    public void print() {

        System.out.println("ALPHABET");
        for (String alphabet : alphabetList)
            System.out.println(alphabet);

        System.out.println("STATE");
        for (State states : stateList)
            System.out.println(states.getStateName());

        System.out.println("START" + "\n" + startState.getStateName());

        System.out.println("FINAL");

        for (State finals : finalState)
            System.out.println(finals.getStateName());

        System.out.println("TRANSITION");

        for (State state : stateList) {

            for (Transition transition : state.getTransitionsList()) {

                System.out.println(state.getStateName() + " " + transition.getAlphabet() + " " + transition.getNextState().getStateName());

            }
        }
    }

}

