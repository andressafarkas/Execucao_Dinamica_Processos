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
  private List<Task> tasks;
  private ConfigParser config;

  public Simulation(String path) {
    this.path = path;
  }

  public void Run() {
    // Get config information
    this.config = ReadConfigFile();

    // Set initial states
    this.tasks = new ArrayList<>();

    for (int i = 0; i < config.getFiles().size(); i++) {
      tasks.add(
          new Task(
              config.getFiles().get(i),
              config.getArrivalTimes().get(i),
              config.getExecTimes().get(i),
              config.getDeadlines().get(i)));
    }

    // Simulation loop
    this.currentTime = 0;
    while (this.currentTime <= this.config.getTotalTime()) {
      // TODO Compute program information
      // TODO Update states
      // TODO Display current time information

      break;
    }

    // TODO End simulation
  }

  public ConfigParser ReadConfigFile() {
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
