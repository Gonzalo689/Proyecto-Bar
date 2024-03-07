package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

public class MesasController {

    private List<Mesa> mesas;
    @FXML
    public void initialize() {
        mesas = App.mesas;

        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
        pause.setOnFinished(event -> changeColor());
        pause.play();
    }

    @FXML
    private void switchToSecondary(ActionEvent event) throws IOException {
        int id = Integer.parseInt(((Button)event.getSource()).getId());
        for (Mesa mesa: mesas) {
            if (mesa.getId() == id) {
                mesa.setOcupada(true);
                App.mesaAct = mesa;
                App.br.iniciarMesa_Comanda(mesa);
                for (int i = 0; i < mesa.sizeProductos(); i++) {
                    App.productos.add(mesa.getProducto(i));
                }
            }
        }
        App.setRoot("productos");
    }
    public void changeColor(){
        for (Mesa m : mesas) {
            String buttonId = Integer.toString(m.getId());
            try {
                Button button = (Button) App.scene.lookup("#" + buttonId);
                if (m.isOcupada()) {
                    button.setStyle("-fx-background-color: #EB8989;");
                } else {
                    button.setStyle("-fx-background-color: #B7F360;");
                }
            }catch (Exception e){
                System.out.println("MAL");
                e.printStackTrace();
            }

        }
    }
    public void createHistorico(){
        HashMap<String, HashMap<Integer,Double>> paramHist = App.br.obtenerHashMapMesasComandas();

        for (Map.Entry<String, HashMap<Integer, Double>> entry : paramHist.entrySet()) {
            String nombre_Mesa = entry.getKey();
            HashMap<Integer, Double> mesaComandas = entry.getValue();

            System.out.println("Mesa: " + nombre_Mesa);

            for (Map.Entry<Integer, Double> comandaEntry : mesaComandas.entrySet()) {
                int mc_id = comandaEntry.getKey();
                double total_mc = comandaEntry.getValue();
                System.out.println("Mesa Comanda ID: " + mc_id + ", Precio Total: " + total_mc);
                crearJasper(mc_id,nombre_Mesa,total_mc);

            }
        }
    }
    public void crearJasper(int mc_id, String nombre_Mesa, double total_mc){
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("mc_id",mc_id);
        parametros.put("nombre_Mesa", nombre_Mesa + " con la comanda " + mc_id);
        parametros.put("total_mc", total_mc + " €");

        InputStream reportFile = getClass().getResourceAsStream("Historico.jrxml");
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(reportFile);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, App.br.crearConexionJasper());
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }


}
