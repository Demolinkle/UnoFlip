package GameSettings;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.multiplayer.NetworkComponent;
import com.almasb.fxgl.net.Connection;

import component.Carta;
import component.MazoComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameFactory implements EntityFactory {

    private Connection<Bundle> conexion;

    public GameFactory(Connection<Bundle> conexion) {
        this.conexion = conexion;
    }

    public enum EntityType {
        CARTA, CARTA_OSCURIDAD, MAZO, CARTA_INICIAL, CARTA_MAZO
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
            .type(EntityType.CARTA)
            //.viewWithBBox(80, 130))
            .with(new MazoComponent())
            .with(new NetworkComponent())
            .build();
    }

    @Spawns("mazo")
    public Entity crearMazo(SpawnData data) {
        Entity mazoEntity = entityBuilder(data)
                .type(EntityType.MAZO)
                .viewWithBBox(texture("carta_portada.png", 100, 100))
                .with(new MazoComponent())
                .with(new NetworkComponent())
                .onClick(e -> {
                    Bundle bundle = new Bundle("Robar una carta");
                    this.conexion.send(bundle);
                })
                .build();
        
        return mazoEntity;
    }

    @Spawns("carta_inicial")
    public Entity carta_inicial(SpawnData data) {
        Entity mazo = ServerGameApp.getMazo();
        MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        Carta primeraCarta = mazoComponent.getCartas().get(0);
        Entity cartaInicial = entityBuilder()
            .type(GameFactory.EntityType.CARTA_INICIAL)
            .with(new NetworkComponent())
            .view(texture(String.format("luz/%s/%s.png", primeraCarta.getColor(), primeraCarta.getId()), 60, 100))
            .at(700, 300)
            .build();
        // se envia una copia de la carta a los jugadores     
        Bundle bundle = new Bundle("Carta inicial del juego");
        bundle.put("carta", primeraCarta);
        this.conexion.send(bundle);     
          
        return cartaInicial;
    }
}