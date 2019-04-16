package com.example.obdtest;

import android.os.AsyncTask;

import com.example.obdtest.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientClass extends AsyncTask<Void, Void, Void> {


    public interface ObdCommandResponse {
        void getObdFormattedResponse(String response);

        void getObdRawResponse(String response);
    }

    private ObdCommandResponse obdResponse = null;
    private ObdCommand command;
    private Socket socket;

    ClientClass() {
    }

    ClientClass(ObdCommand command, ObdCommandResponse obdCommandResponse) {
        this.obdResponse = obdCommandResponse;
        this.command = command;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            socket = new Socket("192.168.0.10", 35000);

            OutputStream mBufferOut = socket.getOutputStream();
            InputStream mBufferIn = socket.getInputStream();

            command.run(mBufferIn, mBufferOut);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        obdResponse.getObdRawResponse(command.getResult());
        obdResponse.getObdFormattedResponse(command.getFormattedResult());
    }
}


