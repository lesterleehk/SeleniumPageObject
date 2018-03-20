package com.page.utils;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * Since the proxy won't try getting the actual web element until you
 * call a method on it, and since it is highly unlikely (if not impossible)
 * to get a web element that doesn't have a tag name, this simply will take
 * in a proxy web element and call the getTagName method on it. If it throws
 * a NoSuchElementException then return null (signaling that it hasn't been
 * found yet). Otherwise, return the proxy web element.
 */
public class ProxyWebElementLocated implements ExpectedCondition<WebElement> {

    private WebElement proxy;

    public ProxyWebElementLocated(WebElement proxy) {
        this.proxy = proxy;
    }

    @Override
    public WebElement apply(WebDriver d) {
        try {
            proxy.getTagName();
        } catch (NoSuchElementException e) {
            return null;
        }
        return proxy;
    }

}