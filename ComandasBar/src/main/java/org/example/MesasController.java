package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class MesasController {

    private List<Mesa> mesas;
    @FXML
    public void initialize() {
        mesas = App.mesas;
        System.out.println(App.scene);

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
                System.out.println(App.scene);
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


}
