package com.rdm.base;

import android.os.AsyncTask;

/**
 * Created by Rao on 2015/1/28.
 */
public class AsyncTaskFeedBack implements BusyFeedback {

    private AsyncTask task = null;

    public AsyncTaskFeedBack(AsyncTask task){
        this.task = task;
    }


    @Override
    public boolean isBusy() {
        return task.getStatus() != AsyncTask.Status.FINISHED;
    }

    @Override
    public float getProgress() {
        return 0;
    }

  /*  @Override
    public boolean isProgressDeterminate() {
        return false;
    }*/

    @Override
    public void cancel() {
        task.cancel(true);
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public String getDescription() {
        return "asyntask:"+ task.getClass().getName();
    }

    @Override
    public void abandon() {

    }

    @Override
    public boolean isAbandon() {
        return false;
    }
}
