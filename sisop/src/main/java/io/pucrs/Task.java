package io.pucrs;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Task {
  private String file;
  private int arrivalTime;
  private boolean isFinished;
  private int executedCi;
  private int ci;
  private int pi;
  private Map<String, Double> dataDict = new HashMap<>();
  private Map<Integer, String[]> codeDict = new HashMap<>();
  private Map<String, Integer> jumpDict = new HashMap<>();
  private int pc;
  private double acc;
  private boolean isBlocked;
  private boolean isReady;
  private boolean isRunning;
  private int blockTime;

  public Task(String file, int arrivalTime, int ci, int pi) {
    this.file = file;
    this.arrivalTime = arrivalTime;
    this.ci = ci;
    this.pi = pi;

    this.isFinished = false;
    this.executedCi = 0;
    this.isBlocked = false;
    this.isReady = true;
    this.isRunning = false;
    this.blockTime = 0;
  }

  public void updateCi() {
    this.executedCi++;
    if (this.executedCi == this.ci) {
      this.executedCi = 0;
      this.isFinished = true;
    }
  }

  public void updateAcc(double newAcc) {
    this.acc = newAcc;
  }

  public void updatePc(int newPc) {
    this.pc = newPc;
  }

  public void block() {
    isBlocked = true;
    blockTime = new Random().nextInt(3);
  }

  public void unblock() {
    isBlocked = false;
    blockTime = 0;
  }

  public void decrementBlockTime() {
    if (isBlocked && blockTime > 0) {
      blockTime--;
      if (blockTime == 0) {
        unblock();
      }
    }
  }

  @Override
  public String toString() {
    return "Task [file=" + file + ", arrivalTime=" + arrivalTime + ", ci=" + ci + ", pi=" + pi + "]";
  }

}
