package com.example.obdtest;

import android.os.AsyncTask;

import com.example.obdtest.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientClass extends AsyncTask<Void, Void, String> {


    public interface ObdCommandResponse{
        public String rawResponse = null;
        public String formattedResponse = null;

        void getObdFormattedResponse(String response);
    }

    private ObdCommandResponse obdResponse = null;
    private ObdCommand command;

    ClientClass(ObdCommand command,ObdCommandResponse obdCommandResponse)
    {
        this.obdResponse = obdCommandResponse;
        this.command = command;
    }

    @Override
    protected String doInBackground(Void...voids) {
        try {
            Socket socket = new Socket("192.168.0.10", 35000);

            OutputStream mBufferOut = socket.getOutputStream();
            InputStream mBufferIn = socket.getInputStream();

            command.run(mBufferIn, mBufferOut);

            return command.getFormattedResult();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        obdResponse.getObdFormattedResponse(response);
    }
}


