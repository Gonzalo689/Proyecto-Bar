package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;


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
        reiniciarLista();
        listener();

    }
    private void listener() {
        imageViewAgua.setOnMouseClicked(event -> listenerProducts(1));
        imageViewFanta.setOnMouseClicked(event -> listenerProducts(2));
        imageViewCola.setOnMouseClicked(event -> listenerProducts(3));
        bocataLomo.setOnMouseClicked(event -> listenerProducts(4));
        bocarajamon.setOnMouseClicked(event -> listenerProducts(5));
        bocataTorilla.setOnMouseClicked(event -> listenerProducts(6));
        tartaChocolate.setOnMouseClicked(mouseEvent -> listenerProducts(9));
        tartaQueso.setOnMouseClicked(mouseEvent -> listenerProducts(8));
        cafe.setOnMouseClicked(mouseEvent -> listenerProducts(7));
    }
    public void listenerProducts (int i){
        Producto producto = App.br.recibirProducto(i);
        addList(producto);
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
        }
        App.br.actualizarMesaComanda(mesa_id, precioTotal);
        lista.clear();
        App.mesaAct.clearProducts();
        listaProductos.clear();
        reiniciarLista();
        App.setRoot("mesas");
    }
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