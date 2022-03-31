package mx.itam.packages.potato;

public class Juego {
    public static void main(String[] args) {
        Jugador jugador1 = new Jugador(0,1);
        Jugador jugador2 = new Jugador(1,0);
        jugador1.start();
        jugador2.start();
    }

}
