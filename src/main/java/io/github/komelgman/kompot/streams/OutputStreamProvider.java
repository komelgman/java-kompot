package io.github.komelgman.kompot.streams;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputStreamProvider {
    OutputStream getOutputStream() throws IOException;
}
