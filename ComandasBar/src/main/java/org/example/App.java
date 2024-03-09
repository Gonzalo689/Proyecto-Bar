package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Scene scene;
    public static ArrayList<Producto> productos;
    public static ArrayList<Mesa> mesas;
    public static Mesa mesaAct;
    public static BarRepository br ;

    /**
     * Metodo que inicia el JavaFX
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        productos = new ArrayList<>();

        inicioBBDD();

        scene = new Scene(loadFXML("mesas"));

        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        System.out.println(scene);
    }

    /**
     * Método que inicia la base de daton con las tablas productos y mesas
     */
    public void inicioBBDD(){
        br = new BarRepository();
        br.crearConexion();
        br.crearTablas();
        // Crear las mesas y productos que usaré
        iniciarMesas();
        inicarProductos();

        mesas = br.recibirListaMesa();
    }

    /**
     * Método de la clase padre el cual cuando se cierre tambien cerrara la conexión a la base de datos y
     * borrará las mesasComandas que no tengan datos
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        br.borrarMesaComandaSinDatos();
        br.cerrarConexion();
        System.out.println("cierre");
        super.stop();
    }

    /**
     * Método estatico para cambiar de visto
     * @param fxml vista a la que quieres ir
     * @throws IOException excepción por si no encuentra la vista
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    public static void main(String[] args) {
        launch();
    }

    /**
     * Método que inicia las mesas solo si no existen
     */
    private void iniciarMesas(){
        br.insertarMesas(1,"Mesa 1");
        br.insertarMesas(2,"Mesa 2");
        br.insertarMesas(3,"Mesa 3");
        br.insertarMesas(4,"Mesa 4");
        br.insertarMesas(5,"Mesa 5");
        br.insertarMesas(6,"Mesa 6");
        br.insertarMesas(7,"Mesa 7");
        br.insertarMesas(8,"Mesa 8");
        br.insertarMesas(9,"Mesa 9");
        br.insertarMesas(10,"Mesa 10");
        br.insertarMesas(11,"Mesa 11");
        br.insertarMesas(12,"Mesa 12");
    }
    /**
     * Método que inicia los productos solo si no existen
     */
    private void inicarProductos(){
        br.insertarProducto(1,"Agua", 1.0);
        br.insertarProducto(2,"Fanta", 1.8);
        br.insertarProducto(3,"Coca-Cola", 1.8);
        br.insertarProducto(4,"Bocadilo de Lomo", 8.5);
        br.insertarProducto(5,"Bocadilo de jamon", 6.2);
        br.insertarProducto(6,"Bocadillo de tortilla", 9.8);
        br.insertarProducto(7, "Café", 1.2);
        br.insertarProducto(8,"Tarta de Queso", 3.5);
        br.insertarProducto(9,"Tarta de Chocolate", 3.5);

    }

}