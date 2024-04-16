package com.example.oopandroidapi.data;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HistoryData {
    public List<String> history = new ArrayList<>();

    public HistoryData() {
        history.add(" ");
        history.add(" ");
        history.add(" ");
        history.add(" ");
        history.add(" ");
    }

    public void add(String item) {
        history.add(0, item);
        history.remove(history.size() - 1);
    }

    public String getHistory() {
        String result = "";
        for (String item : history) {
            result += item + "\n";
        }
        return result;
    }

    public void saveToFile(Context context) {
        try {
            String filecontent = getHistory();
            FileOutputStream output = context.openFileOutput("history.text", Context.MODE_PRIVATE);
            output.write(filecontent.getBytes());
            output.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void readFromFile(Context context) {
        try {
            File file = new File(context.getFilesDir(), "history.text");
            if (file.exists()) {
                InputStream input = context.openFileInput("history.text");
                byte[] filecontent = new byte[(int) file.length()];
                input.read(filecontent);
                input.close();

                String[] data = new String(filecontent).split("\n");
                for (int i = 0; i < data.length && i < 5; i++) {
                    this.history.set(i, data[i]);
                }
            } else {
                String filecontent = "";
                FileOutputStream output = context.openFileOutput("history.text", Context.MODE_PRIVATE);
                output.write(filecontent.getBytes());
                output.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
