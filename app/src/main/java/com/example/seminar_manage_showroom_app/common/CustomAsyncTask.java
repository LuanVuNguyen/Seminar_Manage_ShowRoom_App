package com.example.seminar_manage_showroom_app.common;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomAsyncTask<Params, Progress, Result> {
    private boolean canceled;
    private Handler handlerProgress = new Handler(Looper.getMainLooper());


    protected void onPreExecute(){
        // 本メソッドは定義のみ
        // 処理はオーバーライド時に実装すること
    }


    protected void onPostExecute(Result result){
        // 本メソッドは定義のみ
        // 処理はオーバーライド時に実装すること
    }


    protected Result doInBackground(Params... var1){
        return (Result) "";
    }


    protected void onCancelled(){
        // 本メソッドは定義のみ
        // 処理はオーバーライド時に実装すること
    }


    protected void onProgressUpdate(Progress... values){
        // 本メソッドは定義のみ
        // 処理はオーバーライド時に実装すること
    }


    protected final void publishProgress(final Progress... values) {
        handlerProgress.post(new Runnable() {
            @Override
            public void run() {
                onProgressUpdate(values);
            }
        });
    }


    public void execute(Params... params){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new AsyncRunnable(params));
    }

    public void cancel(Boolean flag){
        canceled = flag;
    }


    private class AsyncRunnable implements Runnable{
        private Result result;
        private Handler handler = new Handler(Looper.getMainLooper());
        private Params[] params;
        AsyncRunnable(Params ... p){
            params = p;
        }

        @Override
        public void run() {
            onPreExecute();
            result = doInBackground(params);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(!canceled){
                        onPostExecute(result);
                    }else{
                        onCancelled();
                    }
                }
            });
        }
    }
}
