package cn.anytec.security.model.websocketmodel;

public class EnrollResp {
    private String play_stream;
    private String playerId;
    private String guid;

    public EnrollResp(String playerId,String play_stream,String guid){
        this.play_stream = play_stream;
        this.playerId = playerId;
        this.guid = guid;
    }
    public String getPlay_stream() {
        return play_stream;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlay_stream(String play_stream) {
        this.play_stream = play_stream;
    }

    public String getGuid() {
        return guid;
    }
}
