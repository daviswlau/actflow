package org.actflow.platform.http.exception;

public class HttpClientException extends RuntimeException{


    private int status = -1;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HttpClientException() {
        super();
    }

    public HttpClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public HttpClientException(final String message) {
        super(message);
    }

    public HttpClientException(final Throwable cause) {
        super(cause);
    }

    public HttpClientException(final int status) {
        super();
        this.status = status;
    }


    public HttpClientException(final int status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpClientException(final int status, final String message) {
        super(message);
        this.status = status;
    }

    public HttpClientException(final int status, final Throwable cause) {
        super(cause);
        this.status = status;
    }


    public int getStatus() {
        return this.status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

}
