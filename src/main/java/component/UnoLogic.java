package component;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static com.almasb.fxgl.dsl.FXGL.*;

import java.util.List;
import component.Carta;
import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

public class UnoLogic extends Component {

    public static void agregarCartaAlMazo(Entity mazo, Entity carta) {
        MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        //mazoComponent.agregarCarta(carta);
    }

    public static void mostrarCartas(Entity mazo) {
        MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        List<Carta> cartas = mazoComponent.getCartas();
        double startX = 50;
        double startY = 50;
        double spacing = 70;
        int i = 0;

        //log.info("cartas: {}", cartas.size());

        for (Carta carta : cartas) {
            Entity aux = entityBuilder() //luz/verde/5.png
                    .viewWithBBox(texture(String.format("luz/%s/%s.png", carta.getColor(), carta.getId()), 60, 100))
                    .build();

            aux.setPosition((startX + i * spacing), startY);
            i++;

            if (i % 16 == 0) {
                startY += 120;
                i=0;
            }

            getGameWorld().addEntity(aux);
        }
    }

}
