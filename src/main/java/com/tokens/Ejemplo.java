package com.tokens;

/*
 * Ejemplo.java
 * --------------
 * Ejemplo simple de un programa en Java
 * Imprime "Hello, World!" en la consola
 */
public class Ejemplo {
    public static void main(String[] args) {
        String sufijo = "!";
        int cantidad = 1;
        var mensaje = "Hello";
        if (cantidad > 1)
            mensaje += ", World" + sufijo;
        else
            mensaje += sufijo;
        // Se imprime el mensaje en la consola
        System.out.println(mensaje);

    }
}
