package io.pucrs;

public class App {
    public static void main(String[] args) {
        //String path = args.length > 1 ? args[1] : "./";
        String path = "/Users/luanathomas/Library/CloudStorage/OneDrive-PUCRS-BR/PUCRS/5 - Quinto Semestre/6 - Sistemas Operacionais/T1_SISOP/config.json";
        System.out.println(args[1]);
        new Simulation(path).Run();
    }
}
