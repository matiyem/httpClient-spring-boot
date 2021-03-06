package com.example.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
//همچنین برای آپلود از این کلاس ارث بری میشود
public class ProgressEntityWrapper extends HttpEntityWrapper {
    private final ProgressListener listener;

    ProgressEntityWrapper(final HttpEntity entity, final ProgressListener listener) {
        super(entity);
        this.listener = listener;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        super.writeTo(new CountingOutputStream(outstream, listener, getContentLength()));
    }
//این اینترفیس را فعال میکنیم برای پیش رفتن مراحل آپلود فایل
    public interface ProgressListener {
        void progress(float percentage);
    }

    public static class CountingOutputStream extends FilterOutputStream {
        //این کلاس برای override کردن متد write است

        private final ProgressListener listener;
        private long transferred;
        private long totalBytes;

        CountingOutputStream(final OutputStream out, final ProgressListener listener, final long totalBytes) {
            super(out);
            this.listener = listener;
            transferred = 0;
            this.totalBytes = totalBytes;
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            out.write(b, off, len);
            transferred += len;
            listener.progress(getCurrentProgress());
        }

        @Override
        public void write(final int b) throws IOException {
            out.write(b);
            transferred++;
            listener.progress(getCurrentProgress());
        }

        private float getCurrentProgress() {
            return ((float) transferred / totalBytes) * 100;
        }
    }
}
