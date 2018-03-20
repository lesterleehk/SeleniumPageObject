package com.annotation;

import java.lang.reflect.Method;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.abstractclasses.TestObject;

public abstract class NetDTestWatcher extends TestWatcher implements INetDTestWatcher {
	
	public abstract boolean isSkipClass(TestObject obj);
	public abstract boolean isSkipMethod(Method method);
	
	@Override
	public void start(TestObject obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void failed(Throwable e, TestObject obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void succeeded(TestObject obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finished(TestObject obj) {
		// TODO Auto-generated method stub

	}

}
