package org.semanticdesktop.aperture.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Wraps a writer within an output stream. Performs appropriate conversion behind the scenes.
 * @author Antoni Mylka - antoni DOT mylka AT dfki DOT de
 */
public class WriterOutputStream extends OutputStream {
    
    private Writer writer;
    private CharsetDecoder decoder;
    private ByteBuffer byteBuffer;
    private CharBuffer charBuffer;
    
    public WriterOutputStream(Writer writer, Charset charset) {
        this.decoder = charset.newDecoder();
        this.byteBuffer = java.nio.ByteBuffer.allocate(16);
        this.charBuffer = CharBuffer.allocate(16384);
        this.writer = writer;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        ByteBuffer bb = ByteBuffer.wrap(b,off,len);
        // first we gotta complete the bytes from the incomplete character
        // we got in the previous calls to write()
        while (byteBuffer.position() != 0 && bb.remaining()>0)  {
            write(bb.get());
        }
        
        // then we can decode the rest of the bb, 
        int lastCharactersFlushed = 0;
        if (bb.remaining() > 0) {
            do {
                decoder.decode(bb, charBuffer, true);
                lastCharactersFlushed = flushCharBuffer();
            } while (lastCharactersFlushed > 0);
            // if there are any bytes in bb, it means that they are from an incomplete
            // character and must be preserved in the internal byte buffer
            byteBuffer.put(bb);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b,0,b.length);
    }

    @Override
    public void write(int b) throws IOException {
        byteBuffer.put((byte)b);
        int oldPosition = byteBuffer.position();
        byteBuffer.flip();
        decoder.decode(byteBuffer, charBuffer, true);
        if (flushCharBuffer() > 0) {
            byteBuffer.clear();
        } else {
            byteBuffer.clear();
            byteBuffer.position(oldPosition);
        }
    }
    
    /**
     * Flushes the character buffer to the underlying writer
     * @throws IOException
     */
    private int flushCharBuffer() throws IOException {
        int position = charBuffer.position();
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            writer.write(charBuffer.get(0));
        } else {
            writer.write(charBuffer.array(),0,position);
        }
        int result = position;
        charBuffer.clear();
        return result;
    }

    /**
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException {
        writer.close();
    }

    /**
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException {
        flushCharBuffer();
        writer.flush();
    }
}
