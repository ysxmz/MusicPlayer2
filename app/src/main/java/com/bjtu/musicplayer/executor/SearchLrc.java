package com.bjtu.musicplayer.executor;

import android.text.TextUtils;

import com.bjtu.musicplayer.http.HttpCallback;
import com.bjtu.musicplayer.http.HttpClient;
import com.bjtu.musicplayer.model.Lrc;
import com.bjtu.musicplayer.model.SearchMusic;
import com.bjtu.musicplayer.utils.FileUtils;

/**
 * 如果本地歌曲没有歌词则从网络搜索歌词
 * Created by wcy on 2016/4/26.
 */
public abstract class SearchLrc implements IExecutor<String> {
    private String artist;
    private String title;

    public SearchLrc(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    @Override
    public void execute() {
        onPrepare();
        searchLrc();
    }

    private void searchLrc() {
        HttpClient.searchMusic(title + "-" + artist, new HttpCallback<SearchMusic>() {
            @Override
            public void onSuccess(SearchMusic response) {
                if (response == null || response.getSong() == null || response.getSong().isEmpty()) {
                    onFail(null);
                    return;
                }

                downloadLrc(response.getSong().get(0).getSongid());
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }
        });
    }

    private void downloadLrc(String songId) {
        HttpClient.getLrc(songId, new HttpCallback<Lrc>() {
            @Override
            public void onSuccess(Lrc response) {
                if (response == null || TextUtils.isEmpty(response.getLrcContent())) {
                    onFail(null);
                    return;
                }

                String filePath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(artist, title);
                FileUtils.saveLrcFile(filePath, response.getLrcContent());
                onExecuteSuccess(filePath);
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }
        });
    }
}
