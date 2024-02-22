package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MesasController {

    private List<Mesa> mesas;
    @FXML
    public void initialize() {
        mesas = App.mesas;
    }

    @FXML
    private void switchToSecondary(ActionEvent event) throws IOException {
        String textoBoton = ((Button) event.getSource()).getText();
        int id = Integer.parseInt(textoBoton.trim().split(" ")[1]);
        for (Mesa mesa: mesas) {
            if (mesa.getId() == id) {
                App.mesaAct = mesa;
                App.br.iniciarMesa_Comanda(mesa);
                for (int i = 0; i < mesa.sizeProductos(); i++) {
                    App.productos.add(mesa.getProducto(i));
                }
            }
        }
        App.setRoot("productos");
    }

}
