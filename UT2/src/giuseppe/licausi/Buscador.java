package giuseppe.licausi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Proceso hijo: cuenta una palabra completa en un archivo UTF-8. */
public final class Buscador {
    private static final String CARACTER_DE_PALABRA = "[\\p{L}\\p{N}\\p{M}_]";

    private Buscador() {
    }

    public static void main(String[] args) {
        int codigo = ejecutar(args, System.out, System.err);
        if (codigo != 0) {
            System.exit(codigo);
        }
    }

    static int ejecutar(String[] args, PrintStream salida, PrintStream error) {
        if (args == null || args.length != 2) {
            error.println("Uso: java giuseppe.licausi.Buscador <palabra> <archivo.txt>");
            return 2;
        }

        try {
            salida.println(buscar(Path.of(args[1]), args[0]));
            return 0;
        } catch (IOException | IllegalArgumentException ex) {
            error.println("ERROR: " + ex.getMessage());
            return 1;
        }
    }

    /** Lee una línea cada vez para evitar cargar el archivo completo en memoria. */
    public static String buscar(Path archivo, String palabra) throws IOException {
        validarPalabra(palabra);
        if (archivo == null || !Files.isRegularFile(archivo)) {
            throw new IOException("No es un archivo regular: " + archivo);
        }

        String palabraNormalizada = Normalizer.normalize(palabra, Normalizer.Form.NFC);
        Pattern patron = Pattern.compile("(?<!" + CARACTER_DE_PALABRA + ")"
                        + Pattern.quote(palabraNormalizada)
                        + "(?!" + CARACTER_DE_PALABRA + ")",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS);

        long inicio = System.nanoTime();
        long apariciones = 0;
        long lineas = 0;
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                lineas++;
                Matcher coincidencias = patron.matcher(Normalizer.normalize(linea, Normalizer.Form.NFC));
                while (coincidencias.find()) {
                    apariciones++;
                }
            }
        }

        long tiempoMs = (System.nanoTime() - inicio) / 1_000_000;
        return String.format(Locale.ROOT, "%s;%d;%d;%d;%d",
                campoCsv(archivo.getFileName().toString()), apariciones, lineas,
                ProcessHandle.current().pid(), tiempoMs);
    }

    private static void validarPalabra(String palabra) {
        if (palabra == null || palabra.isBlank() || !palabra.equals(palabra.trim())
                || palabra.codePoints().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("La palabra debe ser un término no vacío y sin espacios.");
        }
    }

    private static String campoCsv(String valor) {
        return valor.replace(';', '_').replace('\n', '_').replace('\r', '_');
    }
}
