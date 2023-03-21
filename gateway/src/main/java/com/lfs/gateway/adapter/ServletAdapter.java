package com.lfs.gateway.adapter;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class ServletAdapter extends HttpServletRequestWrapper {

    private final ServerHttpRequest serverHttpRequest;

    public ServletAdapter(ServerHttpRequest serverHttpRequest) {
        super(null);
        this.serverHttpRequest = serverHttpRequest;
    }

    @Override
    public String getHeader(String name) {
        return serverHttpRequest.getHeaders().getFirst(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> headerList = serverHttpRequest.getHeaders().get(name);
        if (headerList != null) {
            return Collections.enumeration(headerList);
        } else {
            return Collections.emptyEnumeration();
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> headerSet = new LinkedHashSet<>();
        for (String header : serverHttpRequest.getHeaders().keySet()) {
            headerSet.add(header);
        }
        return Collections.enumeration(headerSet);
    }

    @Override
    public String getMethod() {
        return serverHttpRequest.getMethodValue();
    }

    @Override
    public String getRequestURI() {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(serverHttpRequest.getURI().toString()).build();
        return uriComponents.getPath();
    }

    @Override
    public StringBuffer getRequestURL() {
        String scheme = serverHttpRequest.getURI().getScheme();
        String host = serverHttpRequest.getURI().getHost();
        int port = serverHttpRequest.getURI().getPort();
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(scheme).host(host).port(port).build();
        return new StringBuffer(uriComponents.toUriString() + getRequestURI());
    }

    @Override
    public String getQueryString() {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(serverHttpRequest.getURI().toString()).build();
        MultiValueMap<String, String> queryParams = uriComponents.getQueryParams();
        if (queryParams != null && !queryParams.isEmpty()) {
            StringBuilder queryStringBuilder = new StringBuilder();
            queryParams.forEach((name, values) -> {
                values.forEach(value -> {
                    if (queryStringBuilder.length() > 0) {
                        queryStringBuilder.append("&");
                    }
                    queryStringBuilder.append(name).append("=").append(value);
                });
            });
            return queryStringBuilder.toString();
        } else {
            return null;
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.transToInputStream(serverHttpRequest)));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStreamWrapper(serverHttpRequest.getBody());
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(getParameterMap().keySet());
    }

    @Override
    public String getParameter(String name) {
        return serverHttpRequest.getQueryParams().getFirst(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        List<String> values = serverHttpRequest.getQueryParams().get(name);
        if (values != null) {
            return values.toArray(new String[]{});
        } else {
            return null;
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<>();
        serverHttpRequest.getQueryParams().forEach((name, values) ->
                parameterMap.put(name, values.toArray(new String[]{}))
        );
        return parameterMap;
    }

    private static class ServletInputStreamWrapper extends ServletInputStream {

        private final Iterator<byte[]> byteIterator;

        private ServletInputStreamWrapper(Publisher<? extends DataBuffer> publisher) {
            Flux<DataBuffer> dataBufferFlux = Flux.from(publisher);
            byteIterator = dataBufferFlux.map(DataBuffer::asByteBuffer).map(byteBuffer -> {
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                return bytes;
            }).toIterable().iterator();
        }

        @Override
        public int read() throws IOException {
            if (byteIterator.hasNext()) {
                byte[] bytes = byteIterator.next();
                return bytes[0] & 0xFF;
            } else {
                return -1;
            }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (byteIterator.hasNext()) {
                byte[] bytes = byteIterator.next();
                System.arraycopy(bytes, 0, b, off, bytes.length);
                return bytes.length;
            } else {
                return -1;
            }
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }


    private InputStream transToInputStream(ServerHttpRequest shr) throws IOException {
        return  shr.getBody()
                .reduce(DataBuffer::write)
                .map(DataBuffer::asInputStream)
                .blockOptional().orElseThrow(IOException::new);
    }

}
