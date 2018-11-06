package ru.fintech.tinkoff.tfshw6;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.jetbrains.annotations.NotNull;


public final class Observable {
    private Handler subscribeOnHandler;
    private Handler observeOnHandler;
    private final MyCallable func;

    public static Observable from(@NotNull MyCallable func) {
        return new Observable(func);
    }

    public final Observable subscribeOn(@NotNull Looper looper) {
            if (this.observeOnHandler == null) {
                this.observeOnHandler = new Handler(looper);
            }
            this.subscribeOnHandler = new Handler(looper);
        return this;

    }

    public final Observable observeOn(@NotNull Looper looper) {
        this.observeOnHandler = new Handler(looper);
        return this;
    }

    public final void subscribe(@NotNull final MyCallback onSuccess) {
        if (this.subscribeOnHandler == null &&  this.observeOnHandler == null) {
            try {
                final Object result;
                result = Observable.this.func.call();
                final Message msg = new Message();
                msg.obj = result;
                onSuccess.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (this.subscribeOnHandler == null) {
                this.subscribeOnHandler = new Handler();
                if(this.observeOnHandler == null) {
                    this.observeOnHandler = new Handler();
                }
            }
            this.subscribeOnHandler.post((new Runnable() {
                public final void run() {
                    try {
                        final Object result;
                        result = Observable.this.func.call();
                        final Message msg = new Message();
                        msg.obj = result;
                        Observable.this.observeOnHandler.post((new Runnable() {
                            public final void run() {
                                onSuccess.handleMessage(msg);
                            }
                        }));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
    }

    private Observable(MyCallable func) {
        this.func = func;
        this.subscribeOnHandler = null;
        this.observeOnHandler = null;
    }


}
