package org.example;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.util.HashMap;

/**
 * Clase que contiene Barchart Con las estadisticas de los productos y las mesas
 */

public class EstadisticasController {
    @FXML
    private BarChart<String,Number> productos;
    @FXML
    private BarChart<String,Number> mesas;

    @FXML
    public void initialize(){
        estatsProductos();
        estatsMesas();
    }

    /**
     * Método que crea las estadisticas de los productos cogiendo los datos de la base de datos
     */
    public void estatsProductos(){
        HashMap<String, Integer> cantidadProductos = App.br.obtenerCantidadProductos();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Productos");
        for (HashMap.Entry<String, Integer> entry : cantidadProductos.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        productos.getData().clear();
        productos.getData().add(series);
        productos.setTitle("Productos más consumidos");
    }
    /**
     * Método que crea las estadisticas de las Mesas cogiendo los datos de la base de datos
     */
    public void estatsMesas(){
        HashMap<String, Integer> cantidadProductos = App.br.obtenerMesasMasUsadas();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Mesas");

        for (HashMap.Entry<String, Integer> entry : cantidadProductos.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        mesas.getData().clear();
        mesas.getData().add(series);
        mesas.setTitle("Mesas más usadas");
    }

    /**
     * Método para el boton de volver para volver a la vista de las mesas
     * @throws IOException
     */
    @FXML
    public void volver() throws IOException {
        App.setRoot("mesas");
    }

}
