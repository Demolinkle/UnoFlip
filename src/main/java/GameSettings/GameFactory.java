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
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameFactory implements EntityFactory {

    public enum EntityType {
        CARTA_LUZ, CARTA_OSCURIDAD, MAZO, MAZO_RECARGA
    }

    private static final Random random = new Random();
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

    @Spawns("mazo")
    public Entity crearMazo(SpawnData data) {
        return entityBuilder(data)
            .type(EntityType.MAZO)
            .with(new MazoComponent())
            .with(new NetworkComponent())
            .build();
    }

    @Spawns("mazo_recarga")
    public Entity crearMazoRecarga(SpawnData data) {
        return entityBuilder(data)
            .type(EntityType.MAZO_RECARGA)
            .view(texture("luz/amarillo/1.png", 100, 100))
            .at(300, 300)
            //.with(new MazoComponent())
            .with(new NetworkComponent())
            .build();
    }
}
