package board;

public enum StatePreperationGame {
    IN_PROCCESS("IN-PROCCESS"),
    PREPARED("PREPARED"),
    FINISHED("FINISHED");

    private String state;
    StatePreperationGame(String state){
        this.state = state;
    }
}
