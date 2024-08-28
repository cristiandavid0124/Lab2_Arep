package com.miempresa;

import java.io.OutputStream;

public class Response {
    private final OutputStream outputStream;
    private String contentType;
    private String codeResponse;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.contentType = "text/plain";
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setCodeResponse(String codeResponse) {
        this.codeResponse = codeResponse;
    }

    public String getCodeResponse() {
        return codeResponse;
    }
}
