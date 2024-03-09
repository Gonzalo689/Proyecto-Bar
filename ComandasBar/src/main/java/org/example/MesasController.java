package org.example;

import java.io.*;
import java.util.*;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Clase que controla la vista de las mesas
 */

public class MesasController {

    private List<Mesa> mesas;
    @FXML
    public void initialize() {
        mesas = App.mesas;
        // Clase para que tarde un poco en llamarse le metodo color para que la escena se pueda cargar correctamente
        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
        pause.setOnFinished(event -> changeColor());
        pause.play();
    }

    /**
     * Método que sirve para elegir una mesa el cual cogera el id del la mesa en la que entra para iniciar una mesa con
     * comandas
     * @param event accion para cuando se le clica a alguna mesa
     * @throws IOException
     */
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

    /**
     * Método que sirve para cambiar de color según si estan ocupadas las mesas o no verde si estan libres, rojas si
     * estan ocupadas
     */
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

    /**
     * Crea un JAsper map con cada mesa con comandas del último mes con todos sus productos
     */
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

    /**
     * Crea el jasper Report con los parametros que necesito
     * @param mc_id id de la mesa con comandas
     * @param nombre_Mesa nombre de la mesa que se va usar con la fecha inicial
     * @param total_mc precio total de la mesa con comandas
     */
    public void crearJasper(int mc_id, String nombre_Mesa, double total_mc){
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("mc_id",mc_id);
        parametros.put("nombre_Mesa", nombre_Mesa);
        parametros.put("total_mc", total_mc + " €");

        InputStream reportFile = getClass().getResourceAsStream("Historico.jrxml");
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(reportFile);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, App.br.crearConexionJasper());

            //            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//            String fechaActual = dateFormat.format(new Date());
//            String nombreArchivoPDF = nombre_Mesa + "_comanda_" + mc_id + "_" + fechaActual + ".pdf";
//            String rutaPDF = "src/main/resources/historial/" + nombreArchivoPDF;
//
//             //Export to PDF
//            JasperExportManager.exportReportToPdfFile(jasperPrint, rutaPDF);
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    /**
     * Botón de estadisticas para ir a la vista de estadisticas
     * @throws IOException
     */
    @FXML
    private void botonEstadisticas() throws IOException {
        App.setRoot("estadisticas");
    }


}
