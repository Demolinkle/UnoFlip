package GameSettings;

import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.app.GameApplication;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.entity.Entity;

import java.util.Collections;
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
        });
        System.out.println("Servidor creado");
        server.startAsync();
    }
   
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void onServer() {
        UnoLogic.mostrarCartaServidor(carta_del_servidor, conexion);
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
                    UnoLogic.enviarMensaje("Carta robada", carta, connection);
                    break;

                case "Carta a jugar":
                    if (conexiones.indexOf(connection) == turnoActual) {
                        Carta carta_del_jugador = (Carta) bundle.get("carta");
                        if (!UnoLogic.esValido(carta_del_servidor, carta_del_jugador)) {
                            UnoLogic.enviarMensaje("No puedes jugar esa carta", connection);
                        } else {
                            carta_del_servidor = UnoLogic.jugarCarta(carta_del_servidor, carta_del_jugador, connection);
                            UnoLogic.mostrarCartaServidor(carta_del_servidor, connection);
                            //put
                            //Logica para la carta +1
                            if (carta_del_jugador.getId() == 10 && UnoLogic.cartaTipo(carta_del_jugador.getColor()) == "luz") {
                                UnoLogic.enviarMensaje("Activa", connection);
                                for (Connection conn : conexiones) {
                                    carta_del_servidor = new Carta("naranja", 1, "data"); 
                                    UnoLogic.enviarMensaje("Nueva carta del servidor", carta_del_servidor, conn);
                                    UnoLogic.mostrarCartaServidor(carta_del_servidor, connection);
                                }
                            } 
                            else if (carta_del_jugador.getId() == 10 && UnoLogic.cartaTipo(carta_del_jugador.getColor()) == "oscuridad") {
                                UnoLogic.enviarMensaje("Activa", connection);
                                for (Connection conn : conexiones) {
                                    carta_del_servidor = new Carta("azul", 1, "data"); 
                                    UnoLogic.enviarMensaje("Nueva carta del servidor", carta_del_servidor, conn);
                                    UnoLogic.mostrarCartaServidor(carta_del_servidor, connection);
                                }
                            }
                            else if (carta_del_jugador.getId() == 11 && UnoLogic.cartaTipo(carta_del_jugador.getColor()) == "luz") {
                                enviarCarta();
                                int siguienteTurno = (turnoActual + 1) % conexiones.size();
                                Connection siguienteConexion = conexiones.get(siguienteTurno);
                                Carta cartaRobada = UnoLogic.robarCarta(mazo);
                                UnoLogic.enviarMensaje("Carta robada", cartaRobada, siguienteConexion);
                                turnoActual = (siguienteTurno + 1) % conexiones.size();
                                System.out.println("Carta +1");
                            } 
                            else if (carta_del_jugador.getId() == 11 && UnoLogic.cartaTipo(carta_del_jugador.getColor()) == "oscuridad") {
                                enviarCarta();
                                int siguienteTurno = (turnoActual + 1) % conexiones.size();
                                Connection siguienteConexion = conexiones.get(siguienteTurno);
                                for(int i = 0; i<5; i++){
                                    Carta cartaRobada = UnoLogic.robarCarta(mazo);
                                    UnoLogic.enviarMensaje("Carta robada", cartaRobada, siguienteConexion);
                                }
                                turnoActual = (siguienteTurno + 1) % conexiones.size();
                                System.out.println("Carta +1");
                            }
                            //Logica para la carta skip
                            else if (carta_del_jugador.getId() == 12 && UnoLogic.cartaTipo(carta_del_jugador.getColor()) == "luz") {
                                enviarCarta();
                                turnoActual = (turnoActual + 2) % conexiones.size();
                                System.out.println("Skipear turno");
                                
                            } 
                            else if(carta_del_jugador.getId() == 12 && UnoLogic.cartaTipo(carta_del_jugador.getColor()) == "oscuridad"){
                                enviarCarta();
                                turnoActual = (turnoActual + conexiones.size()) % conexiones.size();
                                System.out.println("Skipear turno");
                                
                            }
                            else if (carta_del_jugador.getId() == 13) {
                                enviarCarta();
                                Collections.reverse(conexiones);
                                System.out.println("Invertir dirección de turnos");
                                if (conexiones.size() == 2) {
                                    turnoActual = (turnoActual + 1) % conexiones.size();
                                } else {
                                    turnoActual = (turnoActual + conexiones.size() - 1) % conexiones.size();
                                }
                                
                            }
                            else if (carta_del_jugador.getId() == 15) {
                                enviarCarta();
                                int siguienteTurno = (turnoActual + 1) % conexiones.size();
                                Connection siguienteConexion = conexiones.get(siguienteTurno);
                                for(int i = 0; i<2; i++){
                                    Carta cartaRobada = UnoLogic.robarCarta(mazo);
                                    UnoLogic.enviarMensaje("Carta robada", cartaRobada, siguienteConexion);
                                }
                                turnoActual = (siguienteTurno + 1) % conexiones.size();
                                System.out.println("Carta +2");
                            }
                            else {
                                for (Connection conn : conexiones) {
                                    UnoLogic.enviarMensaje("Nueva carta del servidor", carta_del_servidor, conn);
                                }
                                turnoActual = (turnoActual + 1) % conexiones.size();
                            }
                        }
                    } else {
                        UnoLogic.enviarMensaje("No es tu turno", connection);
                        System.out.println("No es tu turno");
                    }
                    break;

                case "Victoria":
                    for (Connection<Bundle> conn : conexiones) {
                        if (conn != connection) {
                            Bundle mensaje = new Bundle("Derrota");
                            conn.send(mensaje);
                        }
                    }
                    break;

                case "Se ha cambiado el color":
                    String color = bundle.get("carta");
                    for (Connection<Bundle> conn : conexiones) {
                        if (conn != connection) {
                            UnoLogic.enviarMensaje("Color nuevo", color, connection);
                        }
                    }
                    break;

                case "Flip":
                    UnoLogic.voltearCartas(mazo);
                    UnoLogic.mostrarMazo(mazo);
                    for (Connection conn : conexiones) {
                        UnoLogic.enviarMensaje("Voltear cartas", conn);
                    }
                    break;
            }
        });
        conexiones.add(conexion);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void enviarCarta(){
        for (Connection conn : conexiones) {
            UnoLogic.enviarMensaje("Nueva carta del servidor", carta_del_servidor, conn);
        }
    }
}