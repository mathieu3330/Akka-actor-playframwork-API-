package demo.messages;

import java.util.List;

public class LogLineMessage {

    private final List<String> data;

    public LogLineMessage(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }
}
