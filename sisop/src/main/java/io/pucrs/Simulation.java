package io.pucrs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    // Pega as informações do arquivo de configurações config.json
    ReadConfigFile();
    Reader reader = new Reader();

    // Atualiza os estados iniciais
    this.ready = new ArrayList<>();
    this.blocked = new ArrayList<>();
    this.running = new ArrayList<>();

    for (int i = 0; i < config.getFiles().size(); i++) {
        ProgramParser parser = reader.ReadFile(config.getFiles().get(i));
        this.ready.add(
          new Task(config.getFiles().get(i), 
              config.getArrivalTimes().get(i),
              config.getExecTimes().get(i),
              config.getDeadlines().get(i),
              parser
              )
          );
    }

    // Loop da simulação
    this.currentTime = 0;
    while (this.currentTime <= this.config.getTotalTime()) {
      // Executa a tarefa com maior prioridade (menor deadline)
      GetSmallestDeadlineTask();

      // TODO : Executar linha código refente ao PC atual
      // TODO : Atualizar o Acc e Pc da atividade que está sendo executada

      // Atualiza o tempo atual
      this.currentTime++;

      // Atualiza o tempo da atividade que está sendo executada
      if (!this.running.isEmpty())
        this.running.get(0).updateCi();

      // Imprime informação da atividade que está sendo executada
      DisplaySimulation(currentTime);

      // Atualiza deadlines e verifica se a atividade corrente já finalizou
      // no período atual
      UpdateDeadlines();
    }
  }

  private void UpdateDeadlines() {
    /*
     * Se o tempo atual alcançar o deadline das atividades na fila de prontas
     * então atualiza o novo deadline dessas atividades e marca a flag isFinished
     * como falsa para que elas possam ser executadas no novo deadline delas
     */
    for (int i = 0; i < this.ready.size(); i++) {
      if (this.ready.get(i).getPi() == this.currentTime) {
        this.ready.get(i).setFinished(false);
        int newPi = this.ready.get(i).getCurrentPi() + this.ready.get(i).getPi();
        this.ready.get(i).setPi(newPi);
      }
    }

    /*
     * Se a atividade que estava sendo executada chegar ao fim dentro do seu
     * deadline atual, então ela é removida da fila de executando e passa para a
     * fila de prontos
     */
    if (!this.running.isEmpty() && this.running.get(0).isFinished()) {
      this.ready.add(this.running.remove(0));
    }
  }

  private void GetSmallestDeadlineTask() {
    /*
     * Busca a tarefa, que esteja disponível para ser executada, que tenha o menor
     * deadline entre as tarefas que estão sendo executadas e as que estão prontas
     */
    int smallestDealine = this.running.size() > 0 ? this.running.get(0).getPi() : Integer.MAX_VALUE;
    int taskIndex = 0;
    for (int i = 0; i < this.ready.size(); i++) {
      if (this.ready.get(i).isFinished() == false && this.ready.get(i).getPi() < smallestDealine) {
        smallestDealine = this.ready.get(i).getPi();
        taskIndex = i;
      }
    }

    /*
     * Preempta a atividade com menor deadline para a lista de tarefas em execução
     * e atualiza a lista de tarefas prontas
     */
    if (this.running.size() > 0 && this.running.get(0).getPi() > smallestDealine) {
      this.ready.add(this.running.remove(0));
      this.running.add(this.ready.remove(taskIndex));
    } else if (this.running.isEmpty() && smallestDealine < Integer.MAX_VALUE) {
      this.running.add(this.ready.remove(taskIndex));
    }
  }

  private void DisplaySimulation(int currentTime) {
    String displayTasks = Integer.toString(currentTime);

    if (this.running.isEmpty()) {
      displayTasks += "\t-";
    } else {
      displayTasks += "\t" + this.running.get(0).getFile();
    }
    System.out.println(displayTasks);
  }

  private void ReadConfigFile() {
    /*
     * Realiza a leitura do arquivo de configuração e faz o parser das informações
     * para a classe ConfigParser
     */
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      File configFile = new File(this.path);
      this.config = objectMapper.readValue(configFile, ConfigParser.class);
    } catch (IOException e) {
      e.printStackTrace();
      System.out
          .println("\nSomething happened while trying to read information from the config file! Please try again.\n");
    }
  }
}
