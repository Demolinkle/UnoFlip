package GameSettings;

import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import component.MazoComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameFactory implements EntityFactory {

    public enum EntityType {
        CARTA_LUZ, CARTA_OSCURIDAD, MAZO, MAZO_RECARGA
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
            .build();
    }

    @Spawns("mazo")
    public Entity crearMazo(SpawnData data) {
        return entityBuilder(data)
            .type(EntityType.MAZO)
            .with(new MazoComponent())
            .build();
    }

    @Spawns("mazo_recarga")
    public Entity crearMazoRecarga(SpawnData data) {
        return entityBuilder(data)
            .type(EntityType.MAZO_RECARGA)
            .with(new MazoComponent())
            .build();
    }
}
