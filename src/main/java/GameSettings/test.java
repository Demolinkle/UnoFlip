package GameSettings;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.input.Input;
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
import javafx.util.Duration;

public class test extends GameApplication {
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private Entity mazo;
    private Entity mazoRecarga;
    private final int limiteCartas = 7;
    private final int limiteMazoRecarga = 50;
    //multiplayer
    private Connection<Bundle> conexion;
    private boolean isServer;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Uno Flip");
    }

    @Override
    protected void initGame() {
        runOnce(() -> {
            getDialogService().showConfirmationBox("¿Crear un servidor?", respuesta -> {

                getGameWorld().addEntityFactory(new GameFactory());

                isServer = respuesta;

                if (respuesta) {
                    var server = getNetService().newTCPServer(55555);
                    server.setOnConnected(conn -> {
                        conexion = conn;
                        getExecutor().startAsyncFX(() -> onServer());
                    });

                    System.out.println("Servidor creado");
                    server.startAsync();

                } else {
                    var client = getNetService().newTCPClient("localhost", 55555);
                    client.setOnConnected(conn -> {
                        conexion = conn;
                        getExecutor().startAsyncFX(() -> onClient());
                    });

                    System.out.println("Cliente conectado");
                    client.connectAsync();

                }
            });
        }, Duration.seconds(0.001));
    }

    private void onServer() {

        mazo = spawn("mazo");
        getService(MultiplayerService.class).spawn(conexion, mazo, "bosque");

        UnoLogic.mostrarCartas(mazo);

        // Manejar mensajes recibidos
        conexion.addMessageHandlerFX((connection, bundle) -> {
            if (bundle.getName().equals("MouseClick")) {
                System.out.println("Mouse click received from client!");
            }
        });
    }

    private void onClient() {
        getService(MultiplayerService.class).addEntityReplicationReceiver(conexion, getGameWorld());
        getService(MultiplayerService.class).addInputReplicationSender(conexion, getInput());
        getService(MultiplayerService.class).addPropertyReplicationReceiver(conexion, getWorldProperties());
    }

    @Override
    protected void initInput() {
        //clientInput = new Input();

        onBtnDown(MouseButton.PRIMARY, () -> {
            System.out.println("Boton");
            Bundle bundle = new Bundle("MouseClick");
            conexion.send(bundle);
        });
    }

}
