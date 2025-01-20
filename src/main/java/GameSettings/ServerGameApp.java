package GameSettings;

import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.app.GameApplication;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.entity.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import component.GameFactory;
import component.UnoLogic;
import component.Carta;

public class ServerGameApp extends GameApplication implements Serializable{
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private List<Carta> mazo;

    //multiplayer
    private Connection<Bundle> conexion;
    private Carta carta_del_servidor;
    @SuppressWarnings("rawtypes")
    private List<Connection> conexiones = new ArrayList<>();
    private int turnoActual = 0;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Mesa");
        gameSettings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initGame() {
        mazo = UnoLogic.generarMazo();
        carta_del_servidor = UnoLogic.robarCarta(mazo);
        var server = getNetService().newTCPServer(55555);
        server.setOnConnected(conn -> {
            conexion = conn;
            getExecutor().startAsyncFX(this::onServer);
            //Hay que probar gamefactory
            //getGameWorld().addEntityFactory(new GameFactory(conexion));
        });
        
        System.out.println("Servidor creado");
        server.startAsync();
    }
   
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void onServer() {
        UnoLogic.mostrar_Carta_del_servidor(carta_del_servidor, conexion);
        UnoLogic.mostrarMazo(mazo);
        
        conexion.addMessageHandlerFX((connection, bundle) -> {
            switch (bundle.getName()) {
                case "Repartir":
                    System.out.println("Ejecutando repartir cartas");
                    UnoLogic.repartirCartas(mazo, connection);
                    getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_MAZO).forEach(Entity::removeFromWorld);
                    break;

                case "Robar una carta":
                    System.out.println("Ejecutando robar carta");
                    Carta carta = UnoLogic.robarCarta(mazo);
                    getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_MAZO).forEach(Entity::removeFromWorld);
                    UnoLogic.mostrarMazo(mazo);
                    UnoLogic.enviarMensaje("Carta robada", carta, connection);
                    break;

                    case "Carta a jugar":
                    // Verificar si es el turno del cliente
                    if (conexiones.indexOf(connection) == turnoActual) {
                        Carta carta_del_jugador = (Carta) bundle.get("carta");
                        carta_del_servidor = UnoLogic.jugarCarta(carta_del_servidor, carta_del_jugador, connection);
                        UnoLogic.mostrar_Carta_del_servidor(carta_del_servidor, connection);
                        // Enviar mensaje de actualizacion a todas las conexiones
                        for (Connection conn : conexiones) {
                            UnoLogic.enviarMensaje("Nueva carta del servidor", carta_del_servidor, conn);
                        }
                        // Cambiar el turno al siguiente cliente
                        turnoActual = (turnoActual + 1) % conexiones.size();
                    } else {
                        // Enviar mensaje de que no es el turno del cliente
                        UnoLogic.enviarMensaje1("No es tu turno", conexion);
                        System.out.println("No es tu turno");
                    }
                    break;
            }
        });
        conexiones.add(conexion);
    }
}