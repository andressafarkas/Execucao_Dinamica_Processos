package io.pucrs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Task {
  private int id;
  private String programFile;

  private int arrivalTime;
  private int executionTime;
  private int currentExecutionTime;
  private int deadline;
  private int currentDeadline;
  private boolean isFinished;
  private boolean isBlocked;

  private double taskAcc;
  private Map<String, Double> taskDataDict;
  private ProgramParser parser;
  private List<Integer> lostDeadlines;

  public Task(int id, String programFile, int arrivalTime, int executionTime, int deadline, ProgramParser parser) {
    this.id = id;
    this.programFile = programFile;

    this.arrivalTime = arrivalTime;
    this.executionTime = executionTime;
    this.currentExecutionTime = 0;
    this.deadline = deadline;
    this.currentDeadline = deadline;
    this.isFinished = false;
    this.isBlocked = false;

    this.taskAcc = 0;
    this.taskDataDict = new HashMap<>();
    this.parser = parser;
    this.parser.setTaskId(id);
    this.lostDeadlines = new ArrayList<>();
  }

  public void updateTask() {
    /*
     * Verifica se o tempo de execução da tarefa foi concluído;
     * Se sim, zera o tempo e marca a flag isFinish como true para indicar que
     * essa tarefa não pode mais ocorrer dentro do deadline atual;
     */
    this.currentExecutionTime++;

    if (this.getParser().execute()) {
      this.currentExecutionTime = 0;
      this.isFinished = true;

      this.getParser().restoreValues();
    }

    if (this.getParser().getAditionalTime() != 0)
      this.isBlocked = true;
  }

  public void addLostDeadline(int currentGlobalTime) {
    lostDeadlines.add(currentGlobalTime);
  }

  @Override
  public String toString() {
    return "Task [file=" + this.programFile + ", arrivalTime=" + arrivalTime + ", ci=" + this.executionTime + ", pi="
        + this.deadline + "]";
  }

}
