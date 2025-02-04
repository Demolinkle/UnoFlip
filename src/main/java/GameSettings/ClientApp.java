package GameSettings;

import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.app.GameApplication;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import java.util.ArrayList;
import java.util.List;
import component.GameFactory;
import component.UnoLogic;
import component.Carta;

public class ClientApp extends GameApplication {

    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private Connection<Bundle> conexion;
    private List<Carta> manoJugador = new ArrayList<>();

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Jugador");
        gameSettings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initGame() {
        var client = getNetService().newTCPClient("localhost", 55555);
        client.setOnConnected(conn -> {
            conexion = conn;
            getGameWorld().addEntityFactory(new GameFactory(conexion));
            getExecutor().startAsyncFX(() -> onClient());
            System.out.println("Cliente conectado");
        });
        client.connectAsync();
    }

    private void onClient() {
        getGameWorld().spawn("fondo");
        getGameWorld().spawn("mazo_recarga");
        getGameWorld().spawn("boton_ayuda");
        conexion.addMessageHandlerFX((conexion, bundle) -> {
            switch (bundle.getName()) {
                case "Mano inicial":
                    manoJugador = bundle.get("carta");
                    UnoLogic.mostrarMano(manoJugador, conexion);
                    break;
    
                case "Carta robada":
                    Carta cartaRobada = (Carta) bundle.get("carta");
                    manoJugador.add(cartaRobada);
                    UnoLogic.mostrarMano(manoJugador, conexion);
                    break;

                case "Activa":
                    UnoLogic.enviarMensaje("Flip", conexion);
                    break;
                
                case "Nueva carta del servidor":
                    Carta carta_inicial = (Carta) bundle.get("carta");
                    UnoLogic.mostrarCarta(carta_inicial);
                    break;
    
                case "Nueva carta":
                    System.out.println("papa");
                    Carta aux = (Carta) bundle.get("carta");
                    UnoLogic.mostrarCarta(aux);
                    break;
    
                case "Eliminar carta":
                    Carta carta = (Carta) bundle.get("carta");
                    manoJugador = UnoLogic.removerCarta(manoJugador, carta);
                    UnoLogic.mostrarMano(manoJugador, conexion);
                    victoria(manoJugador);
                    break;

                case "Derrota":
                    getDialogService().showMessageBox("¡Has perdido!", () -> {});
                    break;

                case "Color nuevo":
                    String colorNuevo = bundle.get("carta");
                    getDialogService().showMessageBox("Color nuevo: " + colorNuevo, () -> {});
                    break;

                case "No es tu turno":
                    Alert alert = new Alert(AlertType.INFORMATION, "No es tu turno", ButtonType.CLOSE);
                    alert.setTitle("Mensaje de Ayuda");
                    alert.setHeaderText("Advertencia");
                    alert.showAndWait();
                    break;

                case "Voltear cartas":
                    UnoLogic.voltearCartas(manoJugador);
                    UnoLogic.mostrarMano(manoJugador, conexion);
                    break;
            }
        });
    }
    
    @Override
    protected void initInput() {
        onBtnDown(MouseButton.SECONDARY, () -> {
            System.out.println("Click derecho");
            Bundle bundle = new Bundle("Repartir");
            conexion.send(bundle);
        });
    }

    public void victoria(List<Carta> manoJugador) {
        if (manoJugador.isEmpty()) {
            Bundle bundle = new Bundle("Victoria");
            conexion.send(bundle);
            getDialogService().showMessageBox("¡Has ganado!", () -> {});
        }
    }
}