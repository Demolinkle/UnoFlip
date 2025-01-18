package GameSettings;

import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.app.GameApplication;
//import com.almasb.fxgl.entity.SpawnData;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.entity.Entity;

//import javafx.scene.input.MouseButton;
import java.io.Serializable;
import java.util.List;

import component.GameFactory;
import component.UnoLogic;
import component.Carta;

public class ServerGameApp extends GameApplication implements Serializable{
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    //private static List<Carta> mazo;
    //multiplayer
    private Connection<Bundle> conexion;
    private Carta carta_del_servidor;


    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Mesa");
        gameSettings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initGame() {
        
        var server = getNetService().newTCPServer(55555);
        server.setOnConnected(conn -> {
            conexion = conn;
            getExecutor().startAsyncFX(this::onServer);
            //Agregue aqui el gameFactory para el boton de recargar
            getGameWorld().addEntityFactory(new GameFactory(conexion));
        });
        
        System.out.println("Servidor creado");
        server.startAsync();
    }
    // NOTA: tal parece se genera un nuevo mazo cuando recibe una conexion nueva
    public void onServer() {
        

        List <Carta> mazo = UnoLogic.generarMazo();
        carta_del_servidor = UnoLogic.robarCarta(mazo);
        UnoLogic.mostrar_Carta_del_servidor(carta_del_servidor, conexion);
        UnoLogic.mostrarMazo(mazo);
        
        // Manejar mensajes recibidos
        conexion.addMessageHandlerFX((connection, bundle) -> {
            switch (bundle.getName()) {
                case "Repartir":
                    System.out.println("Ejecutando repartir cartas");
                    UnoLogic.repartirCartas(mazo, conexion);
                    getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_MAZO).forEach(Entity::removeFromWorld);
                    UnoLogic.mostrarMazo(mazo);
                    break;
                
                case "Robar una carta":
                    System.out.println("Ejecutando robar carta");
                    Carta carta = UnoLogic.robarCarta(mazo);
                    getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_MAZO).forEach(Entity::removeFromWorld);
                    UnoLogic.mostrarMazo(mazo);
                    UnoLogic.enviarMensaje("Carta robada", carta, conexion);
                    break;
                
                case "Carta a jugar":
                    Carta carta_del_jugador = (Carta) bundle.get("carta"); //la carta jugada por el jugador al jugarla
                    carta_del_servidor = UnoLogic.jugarCarta(carta_del_servidor, carta_del_jugador, conexion);
                    UnoLogic.mostrar_Carta_del_servidor(carta_del_servidor, conexion);
                    break;

                case "Juego":
                    UnoLogic.mostrar_Carta_del_servidor(carta_del_servidor, conexion);
                    break;
            }
        });
    }

    public  Connection<Bundle> getConexion(){
        return conexion;
    }
}