package component;

import com.almasb.fxgl.multiplayer.NetworkComponent;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.EntityFactory;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.net.Connection;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Spawns;

public class GameFactory implements EntityFactory {

    public enum EntityType {
        CARTA, CARTA_MAZO, CARTA_INICIAL, MAZO, MANO, BOTON_RECARGA, FONDO, BOTON_AYUDA
    }
    
    private Connection<Bundle> conexion;
    
    public GameFactory(Connection<Bundle> conexion) {
        this.conexion = conexion;
    }

    @Spawns("fondo")
    public Entity nuevoFondo(SpawnData data) {
        return entityBuilder(data)
            .type(EntityType.FONDO)
            .viewWithBBox(texture("fondo.jpg", 1400, 700))
            .with(new NetworkComponent())
            .buildAndAttach();
    }

    @Spawns("mazo_recarga")
    public Entity nuevoMazoRecarga(SpawnData data) {
        return entityBuilder(data)
            .type(EntityType.BOTON_RECARGA)
            .viewWithBBox(texture("carta_portada.png", 100, 100))
            .with(new NetworkComponent())
            .at(30, 30)
            .onClick(e -> {
                    System.out.println("Click izquierdo");
                    Bundle bundle = new Bundle("Robar una carta");
                    conexion.send(bundle);     
                    })
            .build();
    }

    @Spawns("boton_ayuda")
    public Entity nuevoBotonAyuda(SpawnData data) {
        return entityBuilder(data)
            .type(EntityType.BOTON_RECARGA)
            .viewWithBBox(texture("boton_ayuda.png", 100, 100))
            .with(new NetworkComponent())
            .at(1200, 30)
            .onClick(e -> {
                    System.out.println("Ayuda");
                    Alert alert = new Alert(AlertType.INFORMATION, 
                    
                    "Reglas Basicas de Uno Flip: \n" + 
                    "Haz click derecho una sola vez para robar 7 cartas, las cuales seran tu mano inicial\n" +
                    "Usa click izquierdo para las otras acciones, como jugar cartas\n" + 
                    "Arriba a la izquierda veras una carta solitaria, al darle click te permitira recargar una carta\n" +
                    "Solo puedes jugar cartas que sean identicas por tipo o color\n" + 
                    "Siendo la unica excepcion las cartas comodin, que tienen fondo negro\n" +
                    "Cuando alguien use la carta flip (carta con el dibujo de un cuadrado), todas las cartas de los jugadores seran volteadas \n" +
                    "Las cartas al ser volteadas mostraran su lado oscuro, que tienen poderes unicos\n" +
                    "Gana el jugador que se quede sin cartas\n"

                    , ButtonType.CLOSE);
                    
                    alert.setTitle("Mensaje de Ayuda");
                    alert.setHeaderText("REGLAS");
                    alert.showAndWait();
                    })
            .build();
    }


}