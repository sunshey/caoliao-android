package com.yc.liaolive.model;

import com.yc.liaolive.interfaces.Executor;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TinyHung@Outlook.com
 * 2017/8/19
 */

public interface ExecutorService extends Executor {

    void shutdown();//顺次地关闭ExecutorService,停止接收新的任务，等待所有已经提交的任务执行完毕之后，关闭ExecutorService


    List<Runnable> shutdownNow();//阻止等待任务启动并试图停止当前正在执行的任务，停止接收新的任务，返回处于等待的任务列表


    boolean isShutdown();//判断线程池是否已经关闭

    boolean isTerminated();//如果关闭后所有任务都已完成，则返回 true。注意，除非首先调用 shutdown 或 shutdownNow，否则 isTerminated 永不为 true。


    boolean awaitTermination(long timeout, TimeUnit unit);//等待（阻塞）直到关闭或最长等待时间或发生中断,timeout - 最长等待时间 ,unit - timeout 参数的时间单位  如果此执行程序终止，则返回 true；如果终止前超时期满，则返回 false


    <T> Future<T> submit(Callable<T> task);//提交一个返回值的任务用于执行，返回一个表示任务的未决结果的 Future。该 Future 的 get 方法在成功完成时将会返回该任务的结果。


    <T> Future<T> submit(Runnable task, T result);//提交一个 Runnable 任务用于执行，并返回一个表示该任务的 Future。该 Future 的 get 方法在成功完成时将会返回给定的结果。


    Future<?> submit(Runnable task);//提交一个 Runnable 任务用于执行，并返回一个表示该任务的 Future。该 Future 的 get 方法在成功 完成时将会返回 null


    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)//执行给定的任务，当所有任务完成时，返回保持任务状态和结果的 Future 列表。返回列表的所有元素的 Future.isDone() 为 true。
            throws InterruptedException;


    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                  long timeout, TimeUnit unit)//执行给定的任务，当所有任务完成时，返回保持任务状态和结果的 Future 列表。返回列表的所有元素的 Future.isDone() 为 true。
            throws InterruptedException;


    <T> T invokeAny(Collection<? extends Callable<T>> tasks)//执行给定的任务，如果在给定的超时期满前某个任务已成功完成（也就是未抛出异常），则返回其结果。一旦正常或异常返回后，则取消尚未完成的任务。
            throws InterruptedException, ExecutionException;


    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                    long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException;
}
