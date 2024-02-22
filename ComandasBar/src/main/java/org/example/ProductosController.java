package org.example;

import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ProductosController {

    @FXML
    private ListView listView;
    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private ArrayList<Producto> lista;
    @FXML
    private ImageView imageViewAgua;
    @FXML
    private ImageView imageViewFanta;
    @FXML
    private ImageView imageViewCola;
    @FXML
    private ImageView bocataCalamares;
    @FXML
    private ImageView plato_jamon;
    @FXML
    private ImageView paella;
    private int mesa_comanda_id;
    private int mesa_id;


    @FXML
    public void initialize() {
        lista = App.productos;
        mesa_id= App.mesaAct.getId();
        mesa_comanda_id = App.br.id_mesa_comanda(mesa_id);
        reiniciarLista();
        listener();

    }
    private void listener() {
        imageViewAgua.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                listenerProducts(1);
            }
        });
        imageViewAgua.setOnMouseClicked(event -> listenerProducts(1));
        imageViewFanta.setOnMouseClicked(event -> listenerProducts(2));
        imageViewCola.setOnMouseClicked(event -> listenerProducts(3));
        bocataCalamares.setOnMouseClicked(event -> listenerProducts(4));
        plato_jamon.setOnMouseClicked(event -> listenerProducts(5));
        paella.setOnMouseClicked(event -> listenerProducts(6));
    }
    public void listenerProducts (int i){
        Producto producto = App.br.recibirProducto(i);
        addList(producto);
        //App.br.aniadirCant(mesa_comanda_id,1);
        App.br.insertarComanda(mesa_comanda_id,producto.getId(),producto.precioTotal());
    }

    private void addList(Producto producto) {
        boolean encontrado = false;
        for (Producto p : lista) {
            if (p.getId() == producto.getId()) {
                p.addCant();
                encontrado = true;
                break;
            }
        }
        if (!encontrado)
            lista.add(producto);

        reiniciarLista();

    }
    private void reiniciarLista(){
        listaProductos.clear();
        listaProductos.addAll(lista);
        listView.setItems(listaProductos);
    }

    @FXML
    private void pagar() throws IOException{

        double precioTotal = 0 ;
        for (Producto producto: listaProductos) {
                precioTotal += producto.precioTotal();
                //App.br.insertarComanda(mesa_comanda_id,producto.getId(),producto.getCant(),producto.precioTotal());
        }
        App.br.actualizarMesaComanda(mesa_id, precioTotal);
        lista.clear();
        App.mesaAct.clearProducts();
        listaProductos.clear();
        reiniciarLista();
        App.setRoot("mesas");
    }


    @FXML
    private void switchToPrimary() throws IOException {
        App.mesaAct.clearProducts();
        for (Producto prod: lista) {
            App.mesaAct.aniadirProducto(prod);
        }
        lista.clear();
        App.setRoot("mesas");
    }



}