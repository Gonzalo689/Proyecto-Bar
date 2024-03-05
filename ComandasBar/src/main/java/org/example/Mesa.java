package org.example;

import java.util.ArrayList;


public class Mesa {
    private int id;
    private String nombre;
    private ArrayList<Producto> productos;
    private boolean ocupada;

    public Mesa(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.productos = new ArrayList<>();
        this.ocupada = false;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void aniadirProducto(Producto p){
        productos.add(p);
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    public int sizeProductos(){
        return productos.size();
    }
    public Producto getProducto(int i){
        return productos.get(i);
    }

    public void clearProducts(){
        productos.clear();
    }

}
