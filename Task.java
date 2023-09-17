import java.util.HashMap;
import java.util.Map;

public class Task {
    String file;
    int arrivalTime;
    int ci;
    int pi;
    Map<String, Double> dataDict = new HashMap<>();
    Map<Integer, String[]> codeDict = new HashMap<>();
    Map<String, Integer> jumpDict = new HashMap<>();
    int pc;
    double acc;

    public Task (String file, int arrivalTime, int ci, int pi){
        this.file = file;
        this.arrivalTime = arrivalTime;
        this.ci = ci;
        this.pi = pi;
    }

    public void updateAcc(double newAcc) {
        this.acc = newAcc;
    }
    
    public void updatePc(int newPc) {
        this.pc = newPc;
    }

    @Override
    public String toString() {
        return "Task [file=" + file + ", arrivalTime=" + arrivalTime + ", ci=" + ci + ", pi=" + pi + "]";
    }
    
}
