package com.laberinto;

import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Laberinto extends JPanel {

    static final int PIXEL_SIZE = 30;
    static final String PARED = "p";
    static final String CAMINO = "c";
    static final String ENTRADA = "e";
    static final String SALIDA = "s";

    public static void main(String[] args) {
        var lineas = new ArrayList<String>();

        try {
            // leo un archivo y agrego las lineas al ArrayList
            try (var file = new Scanner(new File("./laberinto1.txt"))) {
                while (file.hasNext())
                    lineas.add(file.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("No se encontró el archivo.");
            return;
        }

        // reviso si todas las lineas tienen la misma longitud
        var longitud = 0;
        for (var linea : lineas)
            if (linea.length() > longitud)
                longitud = linea.length();

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
            if (linea.contains(ENTRADA))
                entrada = true;
            if (linea.contains(SALIDA))
                salida = true;
            if (entrada && salida)
                break;
        }

        // convertirlo en matriz
        var matriz = new ArrayList<ArrayList<String>>();
        for (var linea : lineas) {
            var vector = new ArrayList<String>();
            for (var caracter : linea.split(""))
                vector.add(caracter);
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
    }

    ArrayList<ArrayList<String>> matriz;

    public Laberinto(ArrayList<ArrayList<String>> matriz) {
        this.matriz = matriz;
        // imprimo la matriz en consola
        for (var x = 0; x < matriz.size(); x++, System.out.println())
            for (var y = 0; y < matriz.get(x).size(); y++) {
                var c = matriz.get(x).get(y);
                if (c.equals(CAMINO))
                    System.out.print("\u001B[47m" + c + "\u001B[0m");
                else if (c.equals(ENTRADA))
                    System.out.print("\u001B[42m" + c + "\u001B[0m");
                else if (c.equals(SALIDA))
                    System.out.print("\u001B[41m" + c + "\u001B[0m");
                else
                    System.out.print("\u001B[40m" + c + "\u001B[0m");
            }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // recorro la matriz
        for (var y = 0; y < matriz.size(); y++)
            for (var x = 0; x < matriz.get(y).size(); x++) {
                // establezco el color
                if (matriz.get(y).get(x).equals(PARED))
                    g.setColor(java.awt.Color.BLACK);
                else if (matriz.get(y).get(x).equals(ENTRADA))
                    g.setColor(java.awt.Color.GREEN);
                else if (matriz.get(y).get(x).equals(SALIDA))
                    g.setColor(java.awt.Color.RED);
                else
                    g.setColor(java.awt.Color.WHITE);
                // dibujo el pixel
                g.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
            }
    }
}
