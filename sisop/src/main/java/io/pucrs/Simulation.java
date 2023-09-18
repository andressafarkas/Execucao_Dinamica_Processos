package io.pucrs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Simulation {
  private String path;
  private int currentTime;
  private Task currentTask;
  private ConfigParser config;

  private List<Task> blocked;
  private List<Task> ready;
  private List<Task> running;

  public Simulation(String path) {
    this.path = path;

    this.blocked = new ArrayList<>();
    this.ready = new ArrayList<>();
    this.running = new ArrayList<>();
  }

  public void Run() {
    // Get config information
    this.config = ReadConfigFile();

    // Set initial states
    this.ready = new ArrayList<>();
    this.blocked = new ArrayList<>();
    this.running = new ArrayList<>();

    for (int i = 0; i < config.getFiles().size(); i++) {
      this.ready.add(
          new Task(
              config.getFiles().get(i),
              config.getArrivalTimes().get(i),
              config.getExecTimes().get(i),
              config.getDeadlines().get(i)));
    }

    // Simulation loop
    DisplayHeader();
    this.currentTime = 0;
    while (this.currentTime <= this.config.getTotalTime()) {
      // analisa menor deadline

      Task task = GetSmallestDeadlineTask();

      // executar linha código dela
      // atualiza acc e pc

      this.currentTime++;

      DisplaySimulation(currentTime, task);

      UpdateDeadlines();
    }

    // TODO End simulation
  }

  private void UpdateDeadlines() {
    for (int i = 0; i < this.ready.size(); i++) {
      if (this.ready.get(i).getPi() == this.currentTime) {
        this.ready.get(i).setFinished(false);
        int newCi = this.ready.get(i).getCi() + this.ready.get(i).getCi();
        this.ready.get(i).setPi(newCi);
      }
    }
  }

  private Task GetSmallestDeadlineTask() {
    int smallestDealine = this.running.size() > 0 ? this.running.get(0).getPi() : Integer.MAX_VALUE;
    int taskIndex = 0;
    int priority = 0; // 0 - running; 1 - ready; 2 - blocked
    for (int i = 0; i < this.ready.size(); i++) {
      if (this.ready.get(i).isFinished() == false && this.ready.get(i).getPi() < smallestDealine) {
        smallestDealine = this.ready.get(i).getPi();
        taskIndex = i;
      }
    }

    if (this.running.size() > 0 && this.running.get(0).getPi() > smallestDealine) {
      this.ready.add(this.running.remove(0));
      this.running.add(this.ready.get(taskIndex));
    } else if (this.running.isEmpty()) {
      this.running.add(this.ready.get(taskIndex));
    }

    if (taskIndex < 0)
      return this.running.get(0);
    return this.ready.get(taskIndex);
  }

  private void DisplayHeader() {
    String displayTasks = "";
    for (int i = 0; i < this.config.getFiles().size(); i++) {
      displayTasks += "\tT" + i;
    }
    System.out.println(displayTasks);
  }

  private void DisplaySimulation(int currentTime, Task task) {
    String displayTasks = Integer.toString(currentTime);

    if (this.running.size() == 0) {
      displayTasks += "\t-";
    } else {
      displayTasks += "\t" + this.currentTask.getFile();
    }
    System.out.println(displayTasks);
  }

  private ConfigParser ReadConfigFile() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      File configFile = new File(this.path);
      return objectMapper.readValue(configFile, ConfigParser.class);
    } catch (IOException e) {
      e.printStackTrace();
      System.out
          .println("\nSomething happened while trying to read information from the config file! Please try again.\n");
    }
    return null;
  }
}
