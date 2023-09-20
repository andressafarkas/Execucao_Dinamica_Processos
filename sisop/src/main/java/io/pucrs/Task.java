package io.pucrs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Task {
    private String file;
    private int arrivalTime;
    private int ci;
    private int pi;
    private boolean isFinished;
    private int executedCi;
    private int currentPi;
    private ProgramParser parser;

    public Task(String file, int arrivalTime, int ci, int pi, ProgramParser parser) {
        this.file = file;
        this.arrivalTime = arrivalTime;
        this.ci = ci;
        this.pi = pi;
        this.parser = parser;

        this.isFinished = false;
        this.executedCi = 0;
        this.currentPi = pi;
    }

    public void updateCi() {
        /*
         * Verifica se o tempo de execução da tarefa foi concluído;
         * Se sim, zera o tempo e marca a flag isFinish como true para indicar que
         * essa tarefa não pode mais ocorrer dentro do deadline atual;
         */

        this.executedCi++;
        if (this.executedCi == this.ci) {
            this.executedCi = 0;
            this.isFinished = true;
        }
    }

    @Override
    public String toString() {
        return "Task [file=" + file + ", arrivalTime=" + arrivalTime + ", ci=" + ci + ", pi=" + pi + "]";
    }

}
