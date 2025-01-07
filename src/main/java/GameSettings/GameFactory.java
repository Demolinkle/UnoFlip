package GameSettings;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.multiplayer.NetworkComponent;

import com.almasb.fxgl.net.Connection;
import component.MazoComponent;
import component.UnoLogic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameFactory implements EntityFactory {

    private Connection<Bundle> conexion;

    public GameFactory(Connection<Bundle> conexion) {
        this.conexion = conexion;
    }
    
    //public GameFactory(){
        //super();
    //}

    public enum EntityType {
        CARTA_LUZ, CARTA_OSCURIDAD, MAZO, MAZO_JUGADOR, CARTA_MAZO
    }

    private static final List<String> imagenesCartaLuz = new ArrayList<>();

    static {
        File folderLuz = new File("src/main/resources/assets/textures/luz");
        File[] files = folderLuz.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".PNG"));

        if (files != null) {
            for (File file : files) {
                imagenesCartaLuz.add(file.getName());
            }
        }
    }

    @Spawns("carta_luz")
    public Entity nuevaCarta1(SpawnData data) {
        return entityBuilder(data)
            .type(EntityType.CARTA_LUZ)
            //.viewWithBBox(80, 130))
            .with(new NetworkComponent())
            .build();
    }

    @Spawns("carta_vacia")
    public Entity cartita(SpawnData data) {
        return entityBuilder(data)
            .type(EntityType.CARTA_LUZ)

            .with(new NetworkComponent())
            .build();
    }

    @Spawns("mazo")
    public Entity crearMazo(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.MAZO)
                .viewWithBBox(texture("carta_portada.png", 100, 100))
                .with(new MazoComponent())
                .with(new NetworkComponent())
                .onClick(e -> {
                  //UnoLogic.mostrarMazo(e);
                })
                .build();
    }

    @Spawns("mazo_jugador")
    public Entity crearMazoJugador(SpawnData data) {
        return entityBuilder(data)
            .type(EntityType.MAZO_JUGADOR) 
            .with(new MazoComponent())
            .with(new NetworkComponent())
            .build();
    }
}
