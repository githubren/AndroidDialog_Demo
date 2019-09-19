package com.example.yfsl.androiddialog_demo;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {
    private volatile static RxBus instance;
    /**
     * 存储某个标签的Subject集合
     */
    private ConcurrentMap<Object, List<Subject>> mSubjectMapper = new ConcurrentHashMap<>();

    public RxBus() {
    }

    public static RxBus get(){
        if (instance == null){
            synchronized (RxBus.class){
                if (instance == null){
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    /**
     * 注册事件
     *
     * @param tag   标签
     * @param <T>   类型
     * @return 被观察者
     */
    public <T> Observable<T> register(@NonNull Object tag) {
        List<Subject> subjectList = mSubjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            mSubjectMapper.put(tag, subjectList);
        }

        Subject<T> subject;
        // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
        subjectList.add(subject = PublishSubject.create());
//        KLog.e("{register}subjectMapper: " + mSubjectMapper);
        return subject;
    }

    /**
     * 取消注册事件
     *
     * @param tag        标签
     * @param observable 被观察者
     */
    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        final List<Subject> subjectList = mSubjectMapper.get(tag);
        if (null != subjectList) {
            subjectList.remove(observable);
            if (subjectList.isEmpty()) {
                // 集合数据为0的时候移map除掉tag
                mSubjectMapper.remove(tag);
            }
        }
//        KLog.e("{unregister}subjectMapper: " + mSubjectMapper);
    }

    /**
     * 发送事件
     *
     * @param tag     标签
     * @param content 发送的内容
     */
    @SuppressWarnings("unchecked")
    public void post(@NonNull Object tag, @NonNull Object content) {
        final List<Subject> subjectList = mSubjectMapper.get(tag);
        if (null != subjectList && !subjectList.isEmpty()) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
            }
        }
    }
}
