package de.inovex.chromecast.presentation;

import android.util.Log;

import com.google.cast.MessageStream;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

class PresentationStream extends MessageStream {
    private static final String NAMESPACE = "de.inovex.chromecast.presentation";
    private static final String COMMAND = "command";
    private static final String COMMAND_START = "start";
    private static final String COMMAND_NEXT = "next";
    private static final String COMMAND_PREVIOUS = "previous";

    protected PresentationStream() {
        super(NAMESPACE);
    }

    public void start() {
        sendCommand(COMMAND_START);
    }

    public void nextSlide() {
        sendCommand(COMMAND_NEXT);
    }

    public void previousSlide() {
        sendCommand(COMMAND_PREVIOUS);
    }

    private void sendCommand(String command) {
        try {
            JSONObject payload = new JSONObject();
            payload.put(COMMAND, command);
            sendMessage(payload);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(JSONObject jsonObject) {
        Log.d(NAMESPACE, jsonObject.toString());
    }
}