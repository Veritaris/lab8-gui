package client.Utils;

public enum StagesEnum {
    SELECT_SERVER("selectServer"),
    LOGIN("login"),
    REGISTER("register"),
    MAIN("main");


    public String stage;

    StagesEnum(String stage) {
        this.stage = stage;
    }

    public String getStageName() {
        return stage;
    }
}
