package chess.web.dto;

public class MoveDto {
    private final String source;
    private final String target;

    public MoveDto(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

}