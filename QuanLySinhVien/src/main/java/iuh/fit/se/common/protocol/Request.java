package iuh.fit.se.common.protocol;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private Command command;
    private Object data;
    private int protocolVersion = 1; // default version

    // === NÂNG CAO - Token xác thực ===
    private String authToken;  // Token xác thực (gửi kèm mỗi request SAU KHI login)

    public Request() { }

    public Request(Command command, Object data) {
        this.command = command;
        this.data = data;
    }

    public Request(Command command, Object data, int protocolVersion) {
        this.command = command;
        this.data = data;
        this.protocolVersion = protocolVersion;
    }

    public Request(Command command, Object data, String authToken) {
        this.command = command;
        this.data = data;
        this.authToken = authToken;
    }

    public Command getCommand() { return command; }
    public void setCommand(Command command) { this.command = command; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public int getProtocolVersion() { return protocolVersion; }
    public void setProtocolVersion(int protocolVersion) { this.protocolVersion = protocolVersion; }

    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }
}
