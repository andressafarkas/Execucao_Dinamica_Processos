package io.pucrs;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        String configPath = args.length > 1 ? args[1] : "./";
        new Simulation(configPath).Run();
    }
}
