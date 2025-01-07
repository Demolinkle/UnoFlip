package GameSettings;

import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.multiplayer.NetworkComponent;

import component.MazoComponent;
import component.UnoLogic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameFactory implements EntityFactory {

    public enum EntityType {
        CARTA_LUZ, CARTA_OSCURIDAD, MAZO, CARTA_INICIAL
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
                    // Aquí debes crear una instancia de UnoLogic
                    Entity cartaInicial = getGameWorld().getEntitiesByType(EntityType.CARTA_INICIAL).get(0); // Obtener la carta inicial
                    UnoLogic unoLogic = new UnoLogic(cartaInicial); // Crear la instancia de UnoLogic

                    // Llamar a mostrarCartas pasándole ambos parámetros
                    UnoLogic.mostrarCartas(e, unoLogic); 
                })
                .build();
        
        return mazoEntity;
    }


    @Spawns("carta_inicial")
    public Entity crearMazoJugador(SpawnData data) {
        return entityBuilder(data)
            .type(EntityType.CARTA_INICIAL) 
            .viewWithBBox(texture("1.png", 60, 100))
            .with(new MazoComponent())
            .at(700,300)
            .with(new NetworkComponent())
            .build();
    }
}
