package io.pucrs;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        String path = args.length > 1 ? args[1] : "./";
        new Simulation(path).Run();
    }
}