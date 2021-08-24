package com.infosyialab.kavach;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordAudio {

    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Context context;
    File recordingFiles;

    public RecordAudio(Context context) {
        this.context = context;
    }

    public void startRecording(){

        String timeStamp = new SimpleDateFormat("yyyy-dd-M--HH-mm-ss").format(new Date());
        AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Kavach Recording/AUDIO_"+timeStamp+".3gp";
        //check for saved folders
        recordingFiles = new File(AudioSavePathInDevice);
        if (!recordingFiles.getParentFile().exists()) {
            recordingFiles.getParentFile().mkdir();
        }

        MediaRecorderReady();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            Toast.makeText(context, ""+e, Toast.LENGTH_LONG).show();
        }
        Toast.makeText(context, "Recording started", Toast.LENGTH_LONG).show();
    }

    private void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }


    public void stopRecording(){
        mediaRecorder.stop();
        mediaRecorder.release();
        MediaRecorderReady();
        Toast.makeText(context, "Recording Completed ", Toast.LENGTH_LONG).show();
    }
}
