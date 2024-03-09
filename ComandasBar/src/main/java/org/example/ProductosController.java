package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Clase la cual es el controlador de la vista de los productos
 */

public class ProductosController {

    @FXML
    private TableView<Producto> tableView;
    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private ArrayList<Producto> lista;
    @FXML
    private ImageView imageViewAgua;
    @FXML
    private ImageView imageViewFanta;
    @FXML
    private ImageView imageViewCola;
    @FXML
    private ImageView bocataLomo;
    @FXML
    private ImageView bocarajamon;
    @FXML
    private ImageView bocataTorilla;
    @FXML
    private ImageView tartaChocolate;
    @FXML
    private ImageView cafe;
    @FXML
    private ImageView tartaQueso;
    private int mesa_comanda_id;
    private int mesa_id;


    @FXML
    public void initialize() {
        lista = App.productos;
        mesa_id= App.mesaAct.getId();
        mesa_comanda_id = App.br.id_mesa_comanda(mesa_id);
        columns();
        reiniciarLista();
        listener();

    }

    /**
     * Método que crea las columnas de la talbe view
     */
    public void columns(){
        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");
        TableColumn<Producto, Integer> colStock = new TableColumn<>("Cant");
        TableColumn<Producto, Void> colBotones = new TableColumn<>("Acciones");

        tableView.getColumns().addAll(colNombre, colPrecio, colStock, colBotones);
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("cant"));
        colBotones.setCellFactory(param -> new TableCell<>() {
            private final Button agregar = new Button(" + ");
            private final Button eliminar = new Button(" - ");

            {
                agregar.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    agregarProducto(producto.getId());
                });

                eliminar.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    eliminarProducto(producto);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttonsBox = new HBox(5,agregar, eliminar);

                    setGraphic(buttonsBox);
                }
            }
        });
    }


    /**
     * Metodo que activa los lisener de los botones
     */
    private void listener() {
        imageViewAgua.setOnMouseClicked(event -> agregarProducto(1));
        imageViewFanta.setOnMouseClicked(event -> agregarProducto(2));
        imageViewCola.setOnMouseClicked(event -> agregarProducto(3));
        bocataLomo.setOnMouseClicked(event -> agregarProducto(4));
        bocarajamon.setOnMouseClicked(event -> agregarProducto(5));
        bocataTorilla.setOnMouseClicked(event -> agregarProducto(6));
        tartaChocolate.setOnMouseClicked(mouseEvent -> agregarProducto(9));
        tartaQueso.setOnMouseClicked(mouseEvent -> agregarProducto(8));
        cafe.setOnMouseClicked(mouseEvent -> agregarProducto(7));
    }

    /**
     * Método para añadir un producto en concreto a la mesa con comanda
     * @param i id del producto que se desea
     */
    public void agregarProducto (int i){
        Producto producto = App.br.recibirProducto(i);
        addList(producto);
        App.br.insertarComanda(mesa_comanda_id,producto.getId(),producto.precioTotal());
    }

    /**
     * Metodo para añadir un producto en concreto de la mesa con comanda
     * @param p
     */
    public void eliminarProducto(Producto p){
        removeList(p);
        App.br.eliminarComanda(mesa_comanda_id, p.getId());
    }

    /**
     * Método que añadir un producto a la lista
     * @param producto producto para añadir
     */
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
    /**
     * Método que eliminar un producto a la lista
     * @param producto producto para añadir
     */
    private void removeList(Producto producto) {
        for (Producto p : lista) {
            if (p.getId() == producto.getId()) {
                p.removeCant();
                if (p.getCant() <=0)
                    lista.remove(p);
                break;
            }
        }
        reiniciarLista();

    }

    /**
     * Método que reinicia las listas
     */
    private void reiniciarLista(){
        listaProductos.clear();
        listaProductos.addAll(lista);
        tableView.setItems(listaProductos);
    }

    /**
     * Método para el botón de pagar el cual servira para finalizar una mesa con comandas y volvera a la vista de mesas
     * @throws IOException
     */

    @FXML
    private void pagar() throws IOException{

        double precioTotal = 0 ;
        for (Producto producto: listaProductos) {
                precioTotal += producto.precioTotal();
        }
        App.br.actualizarMesaComanda(mesa_id, precioTotal);
        lista.clear();
        App.mesaAct.setOcupada(false);
        reiniciarLista();
        App.setRoot("mesas");
    }

    /**
     * Método que crea un jasper de la mesa comanda en la que esta simulando un ticket
     */
    @FXML
    private void crearJasper(){
        double precioTotal = 0 ;
        for (Producto producto: listaProductos) {
            precioTotal += producto.precioTotal();
        }
        Map<String, Object> parametros = new HashMap<>();

        parametros.put("mesa_id", mesa_id);
        parametros.put("total_Mesa", precioTotal + " €");
        parametros.put("nombre_Mesa", "Informe de la mesa " +  App.mesaAct.getId());
        InputStream reportFile = getClass().getResourceAsStream("BarJasper.jrxml");
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(reportFile);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, App.br.crearConexionJasper());
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para cancelar la mesa por si los clientes no piden nada
     * @throws IOException
     */
    @FXML
    private void cancelar() throws IOException {
        App.br.borrarMesaComandaMesa(App.mesaAct.getId());
        lista.clear();
        App.mesaAct.setOcupada(false);
        reiniciarLista();
        App.setRoot("mesas");
    }

    /**
     * Método que sirve para ir a la vista de las mesas pero guardando todos los datos por si queremos volver a la
     * mesa que esten todos los productos anteriores pedidos
     * @throws IOException
     */

    @FXML
    private void switchToPrimary() throws IOException {
        //App.mesaAct.clearProducts();
        for (Producto prod: lista) {
            App.mesaAct.aniadirProducto(prod);
        }
        if (lista.isEmpty()){
            App.mesaAct.setOcupada(false);
        }
        lista.clear();


        App.setRoot("mesas");
    }



}