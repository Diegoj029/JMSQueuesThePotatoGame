package mx.itam.packages.potato;

import java.io.Serializable;
import java.util.Random;

public class Papa implements Serializable {
    private String id;
    private int tiempo;

    public Papa(String id){
        this.id = id;
        this.tiempo = this.tiempoParaCaerse();
    }

    private int tiempoParaCaerse(){
        Random rand = new Random();
        return rand.nextInt(100) + 50;
    }

    public void restarTiempo(int tiempo){
        this.tiempo = this.tiempo - tiempo;
    }

    public boolean cayoPapa(){
        return this.tiempo == 0;
    }

    @Override
    public String toString() {
        return "Papa{" +
                "id='" + id + '\'' +
                ", tiempo=" + tiempo +
                '}';
    }
}
