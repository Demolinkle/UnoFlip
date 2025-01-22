package component;

import com.almasb.fxgl.multiplayer.NetworkComponent;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.EntityFactory;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Spawns;

public class GameFactory implements EntityFactory {

    public enum EntityType {
        CARTA, CARTA_MAZO, CARTA_INICIAL, MAZO, MANO, BOTON_RECARGA, FONDO
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
    public Entity nuevaCarta1(SpawnData data) {
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


}