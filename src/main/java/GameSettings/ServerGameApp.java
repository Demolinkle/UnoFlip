package GameSettings;

import static com.almasb.fxgl.dsl.FXGL.*;
import component.UnoLogic;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;

public class ServerGameApp extends GameApplication {
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private Entity mazo;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new GameFactory());
        mazo = spawn("mazo");
        UnoLogic.mostrarCartas(mazo);
    }

    @Override
    protected void initInput() {
    }
}
