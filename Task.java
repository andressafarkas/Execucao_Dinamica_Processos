public class Task {
    String file;
    int arrivalTime;
    int ci;
    int pi;

    public Task (String file, int arrivalTime, int ci, int pi){
        this.file = file;
        this.arrivalTime = arrivalTime;
        this.ci = ci;
        this.pi = pi;
    }

    @Override
    public String toString() {
        return "Task [file=" + file + ", arrivalTime=" + arrivalTime + ", ci=" + ci + ", pi=" + pi + "]";
    }
    
}
