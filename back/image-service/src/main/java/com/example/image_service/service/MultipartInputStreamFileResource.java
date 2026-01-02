package com.example.image_service.service;

import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.io.InputStream;

public class MultipartInputStreamFileResource extends ByteArrayResource {

    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) throws IOException {
        super(inputStream.readAllBytes());
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }
}
