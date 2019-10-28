package com.Alan.eva.tools;

import android.media.MediaPlayer;
import android.text.TextUtils;

import java.io.IOException;

/**
 * Created by qc on 2016/4/14.
 * 媒体录音管理器
 */
public class MediaManager {
    private boolean isPlaying = false;

    /**
     * 录音需要的一些变量
     */
    private MediaPlayer mPlayer;


    /**
     * 带路径播放
     *
     * @param path 路径
     */
    public void startPlay(String path) {
        if (isPlaying) {
            stopPlay();
        }
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        mPlayer.setOnErrorListener((mp, what, extra) -> {
            stopPlay();
            return false;
        });
        mPlayer.setOnCompletionListener(mp -> stopPlay());
        try {
            mPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(path);
            mPlayer.prepare();
            mPlayer.start();
            isPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
            stopPlay();
        }
    }

    /**
     * 停止播放，并释放播放器
     */
    public void stopPlay() {
        if (isPlaying && mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            isPlaying = false;
        }
    }
}
