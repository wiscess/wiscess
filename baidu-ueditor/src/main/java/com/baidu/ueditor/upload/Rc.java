package com.baidu.ueditor.upload;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.RequestContext;

import jakarta.servlet.http.HttpServletRequest;

public class Rc implements RequestContext{        
    HttpServletRequest request = null;
    public Rc(HttpServletRequest request) {
        this.request=request;
    }

    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public int getContentLength() {
        return request.getContentLength();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

}
