package com.rdm.base;

/**
 * 忙碌反馈接口。UI可以通过访问这个对象来展示busy状态。
 * @author lokierao
 *
 */
public interface BusyFeedback extends Feeback{

    /**
     * 是否完成。
     * @return
     */
    boolean isBusy();

    /**
     * 返回完成进度值。
     * @return  0到1之间的浮点值。
     */
    float getProgress();

    /**
     * 尝试去取消。
     */
    void cancel();

    /**
     * 是否能够去取消。
     * @return
     */
    boolean isCancellable();


    public static class RunnableWrapper implements BusyFeedback, java.lang.Runnable {

        private  Runnable runnable;
        private boolean isBusy = false;

        public RunnableWrapper(Runnable runnable, boolean initBusyStatus){
            this.runnable = runnable;
            isBusy = initBusyStatus;

        }

        @Override
        public boolean isBusy() {
            return isBusy;
        }

        @Override
        public float getProgress() {
            return 0;
        }

        @Override
        public void cancel() {

        }

        @Override
        public boolean isCancellable() {
            return false;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public void abandon() {
            isBusy =false;
            runnable = null;
        }

        @Override
        public boolean isAbandon() {
            return runnable == null;
        }

        @Override
        public void run() {
            isBusy = true;
            try {
                runnable.run();
            }finally {
                abandon();
            }
        }
    }

}
