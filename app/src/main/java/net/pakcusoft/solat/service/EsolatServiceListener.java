package net.pakcusoft.solat.service;

public interface EsolatServiceListener {
    public void complete(String response);
    public void failure(Throwable t);
}
