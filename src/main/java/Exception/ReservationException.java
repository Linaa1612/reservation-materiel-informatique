package Exception;

public class ReservationException extends RuntimeException{
    public ReservationException(String message){
        super(message); //transmet ce message à la classe mère pour qu'il soit accessible avec getMessage()
    }
}
