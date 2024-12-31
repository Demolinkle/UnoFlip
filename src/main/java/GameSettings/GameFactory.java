package GameSettings;

import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameFactory implements EntityFactory {

    public enum EntityType {
        CARTA_LUZ, CARTA_OSCURIDAD, MAZO
    }

    private static final Random random = new Random();
    private static final List<String> imagenesCartaLuz = new ArrayList<>();

    static {
        File folder = new File("C:\\Users\\Usuario\\OneDrive\\Documentos\\Game\\src\\main\\resources\\assets\\textures\\luz");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".PNG"));

        if (files != null) {
            for (File file : files) {
                imagenesCartaLuz.add(file.getName());
            }
        }
    }

    @Spawns("carta_luz")
    public Entity nuevaCarta1(SpawnData data) {
        String imagenAleatoria = "luz/" + imagenesCartaLuz.get(random.nextInt(imagenesCartaLuz.size()));
        return entityBuilder(data)
            .type(EntityType.CARTA_LUZ)
            .viewWithBBox(texture(imagenAleatoria, 100, 150))
            .build();
    }

    @Spawns("mazo")
    public Entity crearMazo(SpawnData data) {
        Entity mazo = entityBuilder(data)
            .type(EntityType.MAZO)
            .with(new MazoComponent())
            .build();
        
        return mazo;
    }
}
