package component;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static com.almasb.fxgl.dsl.FXGL.*;
import java.util.List;

public class UnoLogic extends Component {

    private static final Logger log = LogManager.getLogger(UnoLogic.class);

    public static void agregarCartaAlMazo(Entity mazo, Entity carta) {
        // MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        // mazoComponent.agregarCarta(carta);
    }

    public static void mostrarCartas(Entity mazo) {
        MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        List<Carta> cartas = mazoComponent.getCartas();
        double startX = 50;
        double startY = 50; 
        log.info("cartas: {}", cartas.size());

        for (Carta carta : cartas) {
            Entity aux = entityBuilder()
                    .viewWithBBox(texture(String.format("luz/%s/%s.png", carta.getColor(), carta.getId()), 60, 100))
                    .build();

            aux.setPosition(startX, startY); 
            aux.setRotation(10 * cartas.indexOf(carta));
            aux.setOpacity(0.9 - (0.05 * cartas.indexOf(carta)));

            getGameWorld().addEntity(aux);
        }
    }

    public static void repartirPrimeras7Cartas(Entity mazo, Entity nuevoMazo) {
        MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        List<Carta> cartas = mazoComponent.getCartas();
        List<Carta> primeras7Cartas = cartas.subList(0, Math.min(7, cartas.size()));

        double startX = 200; 
        double startY = 200;
        double spacing = 90;

        for (int i = 0; i < primeras7Cartas.size(); i++) {
            Carta carta = primeras7Cartas.get(i);
            Entity aux = entityBuilder()
                    .viewWithBBox(texture(String.format("luz/%s/%s.png", carta.getColor(), carta.getId()), 60, 100))
                    .build();

            aux.setPosition(startX + i * spacing, startY);
            getGameWorld().addEntity(aux);
        }

        for (int i = 0; i < 7 && !cartas.isEmpty(); i++) {
            cartas.remove(0);
        }
        mazoComponent.setCartas(cartas);
    }
}
