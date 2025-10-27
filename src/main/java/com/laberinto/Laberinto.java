package com.laberinto;

import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Laberinto extends JPanel {

    static final int PIXEL_SIZE = 50;
    static final String PARED = "p";
    static final String CAMINO = "c";
    static final String ENTRADA = "e";
    static final String SALIDA = "s";
    static final int DELAY = 200;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No se proporcionó ningún archivo. especifique la ruta del archivo como argumento.");
            System.out.println("especifique la ruta del archivo como argumento. Ejemplo:");
        System.out.println("\tjava -jar laberinto.jar laberinto.txt");
            return;
        }
        
        var lineas = new ArrayList<String>();

        try {
            // leo un archivo y agrego las lineas al ArrayList
            try (var file = new Scanner(new File(args[0]))) {
                while (file.hasNext()) {
                    lineas.add(file.nextLine());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No se encontró el archivo.");
            return;
        }

        // reviso si todas las lineas tienen la misma longitud
        var longitud = 0;
        for (var linea : lineas) {
            if (linea.length() > longitud) {
                longitud = linea.length();
            }
        }

        // agrego paredes a las lineas que son mas cortas
        for (var i = 0; i < lineas.size(); i++) {
            var linea = lineas.get(i);
            linea += PARED.repeat(longitud - linea.length());
            lineas.set(i, linea);
        }

        // reviso si hay entrada y salida
        var entrada = false;
        var salida = false;
        for (var linea : lineas) {
            var entradas = linea.length() - linea.replace(ENTRADA, "").length();
            if (entradas > 1) {
                if (entrada) {
                    System.out.println("Error: hay mas de una entrada.");
                    return;
                }
                entrada = true;
                if (entradas > 2) {
                    System.out.println("Error: hay mas de una entrada.");
                    return;
                }
            }
            if (linea.contains(SALIDA)) {
                salida = true;
            }
        }
        if (!entrada || !salida) {
            System.out.println("Error: no hay entrada o salida.");
            return;
        }

        // convertirlo en matriz
        var matriz = new ArrayList<ArrayList<Cell>>();
        for (var linea : lineas) {
            var vector = new ArrayList<Cell>();
            for (var c : linea.split("")) {
                switch (c) {
                    case PARED ->
                        vector.add(new Wall());
                    case CAMINO ->
                        vector.add(new Path());
                    case ENTRADA ->
                        vector.add(new Start());
                    case SALIDA ->
                        vector.add(new End());
                    default ->
                        vector.add(new Wall());
                }
            }
            matriz.add(vector);
        }

        // creo canvas
        var canvas = new Laberinto(matriz);
        // abro la interfaz
        var frame = new JFrame("Laberinto");
        frame.add(canvas); // llama automacticamennte a paint()
        frame.setSize((matriz.get(0).size() + 1) * PIXEL_SIZE, (matriz.size() + 1) * PIXEL_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        canvas.resolve();
    }

    ArrayList<ArrayList<Cell>> matriz;
    Start start;

    public Laberinto(ArrayList<ArrayList<Cell>> matriz) {
        this.matriz = matriz;
        // imprimo la matriz en consola
        for (var x = 0; x < matriz.size(); x++, System.out.println()) {
            for (var y = 0; y < matriz.get(x).size(); y++) {
                var c = matriz.get(x).get(y);
                switch (c.type) {
                    case PATH ->
                        System.out.print("\u001B[47m" + "c" + "\u001B[0m");
                    case START ->
                        System.out.print("\u001B[42m" + "e" + "\u001B[0m");
                    case END ->
                        System.out.print("\u001B[41m" + "s" + "\u001B[0m");
                    case WALL ->
                        System.out.print("\u001B[40m" + "p" + "\u001B[0m");
                    default ->
                        System.out.print("\u001B[90m" + "p" + "\u001B[0m");
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // recorro la matriz
        for (var y = 0; y < matriz.size(); y++) {
            for (var x = 0; x < matriz.get(y).size(); x++) {
                // establezco el color
                switch (matriz.get(y).get(x).type) {
                    case WALL ->
                        g.setColor(java.awt.Color.BLACK);
                    case START ->
                        g.setColor(java.awt.Color.GREEN);
                    case END ->
                        g.setColor(java.awt.Color.RED);
                    case PATH -> {
                        if (((Path) matriz.get(y).get(x)).visited) {
                            g.setColor(java.awt.Color.YELLOW);
                        } else {
                            g.setColor(java.awt.Color.WHITE);
                        }
                    }
                    default ->
                        g.setColor(java.awt.Color.GRAY);
                }
                // dibujo el pixel
                g.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
            }
        }
    }

    public void connect() {
        for (var y = 0; y < matriz.size(); y++) {
            for (var x = 0; x < matriz.get(y).size(); x++) {
                var c = matriz.get(y).get(x);
                if (c.type == CellType.PATH || c.type == CellType.START || c.type == CellType.END) {
                    var path = (Path) c;
                    // busco y conecto los vecinos
                    if (x > 0) {
                        var neighbor = matriz.get(y).get(x - 1);
                        if (neighbor.type != CellType.WALL) {
                            path.neighbors.add((Path) matriz.get(y).get(x - 1));
                        }
                    }
                    if (x < matriz.get(y).size() - 1) {
                        var neighbor = matriz.get(y).get(x + 1);
                        if (neighbor.type != CellType.WALL) {
                            path.neighbors.add((Path) matriz.get(y).get(x + 1));
                        }
                    }
                    if (y > 0) {
                        var neighbor = matriz.get(y - 1).get(x);
                        if (neighbor.type != CellType.WALL) {
                            path.neighbors.add((Path) matriz.get(y - 1).get(x));
                        }
                    }
                    if (y < matriz.size() - 1) {
                        var neighbor = matriz.get(y + 1).get(x);
                        if (neighbor.type != CellType.WALL) {
                            path.neighbors.add((Path) matriz.get(y + 1).get(x));
                        }
                    }
                    if (c.type == CellType.START) {
                        start = (Start) c;
                    }
                }
            }
        }
    }

    public void resolve() {
        // crea el grafo
        this.connect();
        // resuelve el laberinto
        var resolved = start.resolve(this);
        // muestra el resultado
        if (resolved) {
            JOptionPane.showMessageDialog(
                    null,
                    "Laberinto resuelto con éxito.",
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "No se encontro solucion",
                    "Resultado",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

enum CellType {
    WALL,
    PATH,
    START,
    END
}

class Cell {
    CellType type;

    public Cell(CellType type) {
        this.type = type;
    }
}

class Wall extends Cell {
    public Wall() {
        super(CellType.WALL);
    }
}

class Path extends Cell {
    public boolean visited = false;
    public ArrayList<Path> neighbors = new ArrayList<Path>();

    public Path() {
        super(CellType.PATH);
    }

    public Path(CellType type) {
        super(type);
    }

    // busca la salida de manera recursiva
    public boolean resolve(Laberinto laberinto) {
        // marca la celda como visitada
        this.visited = true;
        laberinto.repaint();
        try {
            Thread.sleep(Laberinto.DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // registra las celdas que visita
        var visiteds = new ArrayList<Path>();
        for (Path path : neighbors) {
            // si es la salida, retorna true
            if (path instanceof End) {
                return true;
            }
            // si no ha sido visitada, la visita
            if (!path.visited) {
                var resolved =path.resolve(laberinto);
                if (resolved) {
                    return true;
                }
                visiteds.add(path);
            }
        }
        // desmarca solo las celdas que visito
        for (var visited : visiteds) {
            visited.visited = false;
        }
        // desmarca la celda actual
        this.visited = false;
        return false;
    }
}

class Start extends Path {
    public Start() {
        super(CellType.START);
    }
}

class End extends Path {
    public End() {
        super(CellType.END);
    }
}