import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BETUL_SEYHAN_S017566 {
    public static void main(String[] args) {
        ArrayList<String> readList = new ArrayList<>();
        BufferedReader reader;


        try {
            reader = new BufferedReader(new FileReader("BETUL_SEYHAN_S017566.txt"));
            String line = reader.readLine();
            while (line != null) {
                readList.add(line);
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        int inumOfVariablesInput = readList.indexOf("NUMBER-OF-VARIABLES-IN_INPUT");
        int iinputAlphabet = readList.indexOf("INPUT-ALPHABET");
        int inumberOfVariablesInTape = readList.indexOf("NUMBER-OF-VARIABLES-IN-TAPE");
        int itapeAlphabet = readList.indexOf("TAPE-ALPHABET");
        int iblankSymbol = readList.indexOf("BLANK-SYMBOL");
        int inumberOfStates = readList.indexOf("NUMBER-OF-STATE");
        int istates = readList.indexOf("STATES");
        int istart = readList.indexOf("START");
        int iaccept = readList.indexOf("ACCEPT");
        int ireject = readList.indexOf("REJECT");
        int iconfigurations = readList.indexOf("TRANSITIONS");
        int idetect = readList.indexOf("DETECT");
        int iend =  readList.size();

// Creating necessary attributes 
        int numberOfVariable = Integer.parseInt(readList.subList(inumOfVariablesInput+1,iinputAlphabet).get(0));
        List<String> inputAlphabet = new ArrayList<>(readList.subList(iinputAlphabet+1,inumberOfVariablesInTape));
        int numberOfVariablesInTape = Integer.parseInt(readList.subList(inumberOfVariablesInTape+1,itapeAlphabet).get(0));
        List<String> tapeAlphabet = new ArrayList<>(readList.subList(itapeAlphabet+1,iblankSymbol));
        String blankSymbol = readList.subList(iblankSymbol+1,inumberOfStates).get(0);
        int numberOfState = Integer.parseInt(readList.subList(inumberOfStates+1,istates).get(0));
        List<String> states = new ArrayList<>(readList.subList(istates+1,istart));
        String startState = readList.subList(istart+1,iaccept).get(0);
        String acceptState = readList.subList(iaccept+1,ireject).get(0);
        String rejectState = readList.subList(ireject+1,iconfigurations).get(0);
        List<String>configurations = new ArrayList<>(readList.subList(iconfigurations+1,idetect));
        String detect = readList.subList(idetect + 1, iend).get(0);
        tapeAlphabet.add(blankSymbol);

//Create Configurations
        ArrayList<TransitionFunction> transitionFunctionList = new ArrayList<>();
        for(String configuration : configurations){
            String[] words = configuration.split(" ");
            TransitionFunction transitionFunction_ = new TransitionFunction(words[0],words[1],words[2],words[3],words[4]);
            transitionFunctionList.add(transitionFunction_);
        }

        TuringMachine turingMachine = new TuringMachine(startState,acceptState,rejectState, transitionFunctionList,detect,blankSymbol);
        turingMachine.turingAcceptorReject();
    }
}
class TransitionFunction {
    private String currentState;
    private String tapeAlphabet;
    private String writeAlphabet;
    private String move;
    private String nextState;


    public TransitionFunction(String currentState, String tapeAlphabet, String writeAlphabet, String move, String nextState) {
        this.currentState = currentState;
        this.tapeAlphabet = tapeAlphabet;
        this.writeAlphabet = writeAlphabet;
        this.move = move;
        this.nextState = nextState;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getTapeAlphabet() {
        return tapeAlphabet;
    }

    public String getWriteAlphabet() {
        return writeAlphabet;
    }

    public String getMove() {
        return move;
    }

    public String getNextState() {
        return nextState;
    }

}
class TuringMachine {
    private ArrayList<String>tape;
    private int tapePointer;
    private String startState;
    private String acceptState;
    private String rejectState;
    private ArrayList<TransitionFunction> transitionFunctionList;
    private String detect;
    private String blankSymbol;

    private String currentState;

    int counter = 10000;
    public TuringMachine(String startState, String acceptState, String rejectState,
                         ArrayList<TransitionFunction> transitionFunctionList, String detect, String blankSymbol) {
        this.tape =new ArrayList<>();
        this.acceptState=acceptState;
        this.detect=detect;
        this.tapePointer=0;
        this.transitionFunctionList = transitionFunctionList;
        this.rejectState=rejectState;
        this.startState=startState;
        this.blankSymbol=blankSymbol;
        this.currentState = this.startState;
    }

    public void turingAcceptorReject(){
        ArrayList<String>routeTaken = new ArrayList<>();
        for (int index = 0; index < this.detect.length(); index++) {
            tape.add(Character.toString(detect.charAt(index)));
        }
        tape.add(blankSymbol);
        while (counter-->0 && this.tapePointer < this.tape.size() && !currentState.equals(this.acceptState)&&!currentState.equals(this.rejectState)) {
            for (TransitionFunction transitionFunction : this.transitionFunctionList) {
                if (currentState.equals(transitionFunction.getCurrentState()) && tape.get(tapePointer).equals(transitionFunction.getTapeAlphabet())) {
                    routeTaken.add(currentState);
                    currentState = transitionFunction.getNextState();
                    tape.set(tapePointer, transitionFunction.getWriteAlphabet());
                    if (transitionFunction.getMove().equals("R"))
                        tapePointer++;
                    if (transitionFunction.getMove().equals("L")) {
                        if(tapePointer >0) {
                            tapePointer--;
                        }
                    }
                }
            }
            routeTaken.add(currentState);
        }
        print(routeTaken,currentState);
    }

    public void print(ArrayList<String> routeTaken, String currentState){
        System.out.print("ROUTE: ");
        for(String route: routeTaken){
            System.out.print(route+" ");
        }
        System.out.print("\n"+"RESULT: ");
        if (currentState.equals(this.acceptState)) {
            System.out.print("accepted");
        }
        else if (currentState.equals(this.rejectState)) {
            System.out.print("rejected");
        }
        else if (counter<=0){
            System.out.print("loop");
        }
    }
}
