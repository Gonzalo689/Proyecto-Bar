package org.example;

/**
 * Clase de producto
 */
public class Producto {
    private  int id ;
    private String nombre ;
    private double precio;
    private int cant ;

    /**
     * Constuctor de la clase producto
     * @param id id del producto
     * @param nombre nombre del producto
     * @param precio precio del producto
     */
    public Producto(int id ,String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cant = 1;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCant() {
        return cant;
    }

    public void setCant(int cant) {
        this.cant = cant;
    }

    public void addCant(){
        cant++;
    }
    public double precioTotal(){
        return cant*precio;
    }

    @Override
    public String toString() {
        return this.nombre + "  " + this.cant +  "  " + precioTotal();
    }
}
