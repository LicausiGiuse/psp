package giuseppe.licausi;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/** Proceso padre: enumera archivos y lanza hasta cuatro procesos Buscador a la vez. */
public final class Gestor {
    public static final int MAX_PROCESOS_SIMULTANEOS = 4;
    private static final String CABECERA = "archivo;apariciones;lineas;PID;tiempo_ms%n";

    private Gestor() {
    }

    public static void main(String[] args) {
        int codigo = ejecutar(args, Path.of("informe.txt"), Path.of("errores.log"), System.out, System.err);
        if (codigo != 0) {
            System.exit(codigo);
        }
    }

    static int ejecutar(String[] args, Path informe, Path errores, PrintStream salida, PrintStream error) {
        long inicio = System.nanoTime();
        Path informeAbsoluto = informe.toAbsolutePath().normalize();
        Path erroresAbsoluto = errores.toAbsolutePath().normalize();
        try {
            Files.writeString(informeAbsoluto, CABECERA.formatted(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            Files.writeString(erroresAbsoluto, "", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException ex) {
            error.println("No se pudieron crear informe.txt y errores.log: " + ex.getMessage());
            return 2;
        }

        if (args == null || args.length != 2) {
            return fallar(informeAbsoluto, erroresAbsoluto, error, inicio,
                    "Uso: java giuseppe.licausi.Gestor <palabra> <carpeta>");
        }
        if (args[0] == null || args[0].isBlank() || args[0].codePoints().anyMatch(Character::isWhitespace)) {
            return fallar(informeAbsoluto, erroresAbsoluto, error, inicio,
                    "La palabra debe ser un término no vacío y sin espacios.");
        }

        final Path carpeta;
        try {
            carpeta = Path.of(args[1]);
        } catch (InvalidPathException ex) {
            return fallar(informeAbsoluto, erroresAbsoluto, error, inicio,
                    "La ruta de la carpeta no es válida: " + ex.getInput());
        }
        if (!Files.isDirectory(carpeta)) {
            return fallar(informeAbsoluto, erroresAbsoluto, error, inicio,
                    "La carpeta no existe o no es un directorio: " + carpeta);
        }

        final List<Path> archivos;
        try (Stream<Path> entradas = Files.list(carpeta)) {
            archivos = entradas
                    .filter(Files::isRegularFile)
                    .filter(Gestor::esArchivoTxt)
                    .filter(archivo -> !archivo.toAbsolutePath().normalize().equals(informeAbsoluto))
                    .sorted()
                    .toList();
        } catch (IOException ex) {
            return fallar(informeAbsoluto, erroresAbsoluto, error, inicio,
                    "No se pudo listar la carpeta " + carpeta + ": " + ex.getMessage());
        }

        int erroresDeProceso = ejecutarProcesos(archivos, args[0], informeAbsoluto, erroresAbsoluto);
        long apariciones = 0;
        long archivosProcesados = 0;
        try {
            apariciones = sumarApariciones(informeAbsoluto);
            archivosProcesados = contarResultados(informeAbsoluto);
        } catch (IOException ex) {
            registrarError(erroresAbsoluto, "No se pudieron recopilar los resultados del informe: "
                    + ex.getMessage());
            error.println("No se pudieron recopilar los resultados del informe: " + ex.getMessage());
            erroresDeProceso++;
        }
        long tiempoTotalMs = (System.nanoTime() - inicio) / 1_000_000;
        try {
            String resumen = String.format(Locale.ROOT,
                    "RESUMEN;archivos_procesados=%d;apariciones_totales=%d;errores=%d;tiempo_total_ms=%d%n",
                    archivosProcesados, apariciones, erroresDeProceso, tiempoTotalMs);
            Files.writeString(informeAbsoluto, resumen, StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        } catch (IOException ex) {
            registrarError(erroresAbsoluto, "No se pudo completar el informe: " + ex.getMessage());
            error.println("No se pudo completar el informe: " + ex.getMessage());
            erroresDeProceso++;
        }

        salida.println("Informe: " + informeAbsoluto);
        if (erroresDeProceso > 0) {
            error.println("Se produjeron " + erroresDeProceso + " error(es); consulta " + erroresAbsoluto);
            return 1;
        }
        return 0;
    }

    private static boolean esArchivoTxt(Path archivo) {
        return archivo.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".txt");
    }

    private static int ejecutarProcesos(List<Path> archivos, String palabra, Path informe, Path errores) {
        if (archivos.isEmpty()) {
            return 0;
        }

        ExecutorService ejecutores = Executors.newFixedThreadPool(
                Math.min(MAX_PROCESOS_SIMULTANEOS, archivos.size()));
        try {
            List<CompletableFuture<Integer>> trabajos = archivos.stream()
                    .map(archivo -> CompletableFuture.supplyAsync(
                            () -> ejecutarBuscador(archivo, palabra, informe, errores), ejecutores))
                    .toList();
            return trabajos.stream()
                    .mapToInt(trabajo -> {
                        try {
                            return trabajo.join();
                        } catch (CompletionException ex) {
                            registrarError(errores, "Fallo inesperado al esperar un proceso: " + ex.getMessage());
                            return 1;
                        }
                    })
                    .sum();
        } finally {
            ejecutores.shutdown();
        }
    }

    private static int ejecutarBuscador(Path archivo, String palabra, Path informe, Path errores) {
        ProcessBuilder constructor = new ProcessBuilder(
                ejecutableJava(), "-Dfile.encoding=UTF-8", "-cp", classpathHijo(),
                Buscador.class.getName(), palabra, archivo.toAbsolutePath().toString());
        constructor.redirectOutput(ProcessBuilder.Redirect.appendTo(informe.toFile()));
        constructor.redirectError(ProcessBuilder.Redirect.appendTo(errores.toFile()));
        try {
            int codigo = constructor.start().waitFor();
            if (codigo != 0) {
                registrarError(errores, "El buscador de " + archivo.getFileName()
                        + " terminó con código " + codigo + ".");
            }
            return codigo == 0 ? 0 : 1;
        } catch (IOException ex) {
            registrarError(errores, "No se pudo iniciar el buscador de " + archivo + ": " + ex.getMessage());
            return 1;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            registrarError(errores, "Se interrumpió el buscador de " + archivo + ".");
            return 1;
        }
    }

    private static String ejecutableJava() {
        String ejecutable = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")
                ? "java.exe" : "java";
        return Path.of(System.getProperty("java.home"), "bin", ejecutable).toString();
    }

    private static String classpathHijo() {
        Set<String> entradas = new LinkedHashSet<>(Arrays.asList(
                System.getProperty("java.class.path", "").split(Pattern.quote(File.pathSeparator))));
        try {
            entradas.add(Path.of(Gestor.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString());
            entradas.add(Path.of(Buscador.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("No se pudo determinar la ubicación de las clases", ex);
        }
        return String.join(File.pathSeparator, entradas);
    }

    private static long sumarApariciones(Path informe) throws IOException {
        try (Stream<String> lineas = Files.lines(informe, StandardCharsets.UTF_8)) {
            return lineas
                    .filter(linea -> !linea.startsWith("archivo;") && !linea.startsWith("RESUMEN;"))
                    .map(linea -> linea.split(";", -1))
                    .filter(campos -> campos.length >= 2)
                    .mapToLong(campos -> {
                        try {
                            return Long.parseLong(campos[1]);
                        } catch (NumberFormatException ex) {
                            return 0;
                        }
                    })
                    .sum();
        }
    }

    private static long contarResultados(Path informe) throws IOException {
        try (Stream<String> lineas = Files.lines(informe, StandardCharsets.UTF_8)) {
            return lineas.filter(linea -> !linea.startsWith("archivo;") && !linea.startsWith("RESUMEN;")).count();
        }
    }

    private static int fallar(Path informe, Path errores, PrintStream error, long inicio, String mensaje) {
        registrarError(errores, mensaje);
        error.println(mensaje);
        try {
            long tiempoMs = (System.nanoTime() - inicio) / 1_000_000;
            Files.writeString(informe,
                    "RESUMEN;archivos_procesados=0;apariciones_totales=0;errores=1;tiempo_total_ms="
                            + tiempoMs + System.lineSeparator(),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        } catch (IOException ex) {
            error.println("Tampoco se pudo escribir el resumen: " + ex.getMessage());
        }
        return 2;
    }

    private static synchronized void registrarError(Path errores, String mensaje) {
        try {
            Files.writeString(errores, mensaje + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        } catch (IOException ignored) {
            System.err.println("No se pudo registrar el error en " + errores + ": " + mensaje);
        }
    }
}
