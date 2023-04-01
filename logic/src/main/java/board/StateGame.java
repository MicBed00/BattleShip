package board;

public enum StateGame {
    NEW("NEW"),
    WAITING("WAITING"),

    APPROVED("APPROVED"),

    REQUESTING("REQUESTING"),
    IN_PROCCESS("IN-PROCCESS"),
    PREPARED("PREPARED"),

    REJECTED("REJECTED"),
    FINISHED("FINISHED");

    private String state;
    StateGame(String state){
        this.state = state;
    }
}
