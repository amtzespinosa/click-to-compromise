package com.amtzespinosa.c2server;

import java.sql.Timestamp;

public class Agent {
    protected String agentId;
    protected String ip;
    protected String os;
    protected Timestamp timestamp;

    public Agent(String agentId, String ip, String os, Timestamp timestamp) {
        this.agentId = agentId;
        this.ip = ip;
        this.os = os;
        this.timestamp = timestamp;
    }

    public String getAgentId() { return agentId; }
    public String getIp() { return ip; }
    public String getOs() { return os; }
    public Timestamp getTimestamp() { return timestamp; }
}
