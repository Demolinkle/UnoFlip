package GameSettings;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.serialization.Bundle;
import component.UnoLogic;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
//import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.net.Connection;

public class ServerGameApp extends GameApplication {
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private Entity mazo;
    private Entity mazoRecarga;
    private final int limiteCartas = 7;
    private final int limiteMazoRecarga = 50;
    //multiplayer
    private Connection<Bundle> conexion;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Uno Flip");
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameFactory());

        var server = getNetService().newTCPServer(55555);
        server.setOnConnected(conn -> {
            conexion = conn;
            getExecutor().startAsyncFX(this::onServer);
        });

        System.out.println("Servidor creado");
        server.startAsync();
    }

    private void onServer() {

        mazo = spawn("mazo");
        getService(MultiplayerService.class).spawn(conexion, mazo, "bosque");

        UnoLogic.mostrarCartas(mazo);

        // Manejar mensajes recibidos
        conexion.addMessageHandlerFX((connection, bundle) -> {
            if (bundle.getName().equals("MouseClick")) {
                System.out.println("Mouse click received from client!");
                // Aquí puedes manejar el evento de clic del cliente
            }
        });
    }

    @Override
    protected void initInput() {

        onBtnDown(MouseButton.PRIMARY, () -> {
            System.out.println("Boton");
        });

    }

}
