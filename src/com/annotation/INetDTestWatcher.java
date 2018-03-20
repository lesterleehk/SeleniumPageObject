package com.annotation;

import java.lang.reflect.Method;

import com.abstractclasses.TestObject;

public interface INetDTestWatcher {
	 public boolean isSkipClass(TestObject obj);
	 public boolean isSkipMethod(Method method);
	 public void start(TestObject obj);
	 public void failed(Throwable e, TestObject obj);
	 public void succeeded(TestObject obj);
	 public void finished(TestObject obj);
}
