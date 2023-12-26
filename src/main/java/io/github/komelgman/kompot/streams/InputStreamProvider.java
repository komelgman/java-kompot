package io.github.komelgman.kompot.streams;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamProvider {
    InputStream getInputStream() throws IOException;
}
