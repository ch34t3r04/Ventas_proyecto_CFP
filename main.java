import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * main
 *
 * Segunda clase con metodo main del proyecto (Entrega 2 - Semana 5).
 *
 * Lee los archivos generados por GenerateInfoFiles (productos.txt,
 * vendedores.txt y los archivos ventas_<id>_<nombre>.txt) y produce dos
 * archivos de reporte, ambos tipo CSV:
 *
 *  - reporteVendedores.csv: vendedores ordenados de mayor a menor dinero
 *    recaudado, formato NombreCompleto;DineroRecaudado
 *  - reporteProductos.csv: productos ordenados de mayor a menor cantidad
 *    vendida, formato NombreProducto;Precio
 *
 * No solicita ninguna informacion al usuario.
 *
 * @author Sebas
 */
public class main {

    /**
     * Punto de entrada del programa.
     *
     * @param args no se utilizan
     */
    public static void main(String[] args) {
        try {
            Map<String, Producto> productos = leerProductos("productos.txt");
            List<Vendedor> vendedores = leerVendedores("vendedores.txt");

            procesarArchivosDeVentas(".", productos, vendedores);

            generarReporteVendedores(vendedores, "reporteVendedores.csv");
            generarReporteProductos(productos, "reporteProductos.csv");

            System.out.println("Reportes generados exitosamente.");
        } catch (IOException e) {
            System.out.println("Error generando los reportes: " + e.getMessage());
        }
    }

    /**
     * Representa un producto del catalogo, junto con la cantidad total
     * vendida acumulada a partir de los archivos de ventas.
     */
    private static class Producto {
        String id;
        String nombre;
        double precio;
        int cantidadVendida = 0;

        Producto(String id, String nombre, double precio) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
        }
    }

    /**
     * Representa un vendedor, junto con el dinero total recaudado
     * acumulado a partir de sus archivos de ventas.
     */
    private static class Vendedor {
        String tipoDocumento;
        long numeroDocumento;
        String nombres;
        String apellidos;
        double dineroRecaudado = 0;

        Vendedor(String tipoDocumento, long numeroDocumento, String nombres, String apellidos) {
            this.tipoDocumento = tipoDocumento;
            this.numeroDocumento = numeroDocumento;
            this.nombres = nombres;
            this.apellidos = apellidos;
        }

        String nombreCompleto() {
            return nombres + " " + apellidos;
        }
    }

    /**
     * Lee el archivo de productos y lo carga en un mapa indexado por ID
     * de producto, para poder consultarlo rapidamente al procesar ventas.
     *
     * @param rutaArchivo ruta del archivo productos.txt
     * @return mapa de productos indexado por su ID
     * @throws IOException si ocurre un error leyendo el archivo
     */
    private static Map<String, Producto> leerProductos(String rutaArchivo) throws IOException {
        Map<String, Producto> productos = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(";");
                String id = partes[0];
                String nombre = partes[1];
                double precio = Double.parseDouble(partes[2]);

                productos.put(id, new Producto(id, nombre, precio));
            }
        }
        return productos;
    }

    /**
     * Lee el archivo de vendedores y lo carga en una lista.
     *
     * @param rutaArchivo ruta del archivo vendedores.txt
     * @return lista de vendedores
     * @throws IOException si ocurre un error leyendo el archivo
     */
    private static List<Vendedor> leerVendedores(String rutaArchivo) throws IOException {
        List<Vendedor> vendedores = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(";");
                String tipoDocumento = partes[0];
                long numeroDocumento = Long.parseLong(partes[1]);
                String nombres = partes[2];
                String apellidos = partes[3];

                vendedores.add(new Vendedor(tipoDocumento, numeroDocumento, nombres, apellidos));
            }
        }
        return vendedores;
    }

    /**
     * Busca todos los archivos de ventas (ventas_*.txt) en la carpeta dada,
     * y por cada uno acumula el dinero recaudado del vendedor correspondiente
     * y la cantidad vendida de cada producto.
     *
     * La primera linea de cada archivo de ventas trae el numero de documento
     * del vendedor, que se usa para encontrar el vendedor correspondiente en
     * la lista ya cargada desde vendedores.txt.
     *
     * @param carpeta carpeta donde buscar los archivos de ventas
     * @param productos mapa de productos, se actualiza con la cantidad vendida
     * @param vendedores lista de vendedores, se actualiza con el dinero recaudado
     * @throws IOException si ocurre un error leyendo alguno de los archivos
     */
    private static void procesarArchivosDeVentas(String carpeta, Map<String, Producto> productos,
            List<Vendedor> vendedores) throws IOException {

        File directorio = new File(carpeta);
        File[] archivosVentas = directorio.listFiles(
                (dir, nombre) -> nombre.startsWith("ventas_") && nombre.endsWith(".txt"));

        if (archivosVentas == null) {
            return;
        }

        for (File archivo : archivosVentas) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String primeraLinea = reader.readLine();
                if (primeraLinea == null || primeraLinea.trim().isEmpty()) {
                    continue;
                }

                String[] datosVendedor = primeraLinea.split(";");
                long numeroDocumentoVendedor = Long.parseLong(datosVendedor[1]);
                Vendedor vendedor = buscarVendedorPorDocumento(vendedores, numeroDocumentoVendedor);

                String linea;
                while ((linea = reader.readLine()) != null) {
                    if (linea.trim().isEmpty()) {
                        continue;
                    }
                    String[] partesVenta = linea.split(";");
                    String idProducto = partesVenta[0];
                    int cantidad = Integer.parseInt(partesVenta[1]);

                    Producto producto = productos.get(idProducto);
                    if (producto == null) {
                        continue;
                    }

                    producto.cantidadVendida += cantidad;

                    if (vendedor != null) {
                        vendedor.dineroRecaudado += producto.precio * cantidad;
                    }
                }
            }
        }
    }

    /**
     * Busca un vendedor dentro de la lista por su numero de documento.
     *
     * @param vendedores lista de vendedores
     * @param numeroDocumento numero de documento a buscar
     * @return el vendedor encontrado, o null si no existe
     */
    private static Vendedor buscarVendedorPorDocumento(List<Vendedor> vendedores, long numeroDocumento) {
        for (Vendedor v : vendedores) {
            if (v.numeroDocumento == numeroDocumento) {
                return v;
            }
        }
        return null;
    }

    /**
     * Genera el archivo de reporte de vendedores, ordenado de mayor a menor
     * dinero recaudado. Formato por linea: NombreCompleto;DineroRecaudado
     *
     * @param vendedores lista de vendedores ya con su dinero recaudado calculado
     * @param rutaSalida ruta del archivo de salida
     * @throws IOException si ocurre un error escribiendo el archivo
     */
    private static void generarReporteVendedores(List<Vendedor> vendedores, String rutaSalida) throws IOException {
        List<Vendedor> ordenados = new ArrayList<>(vendedores);
        ordenados.sort(Comparator.comparingDouble((Vendedor v) -> v.dineroRecaudado).reversed());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaSalida))) {
            for (Vendedor v : ordenados) {
                writer.write(v.nombreCompleto() + ";" + v.dineroRecaudado);
                writer.newLine();
            }
        }
    }

    /**
     * Genera el archivo de reporte de productos, ordenado de mayor a menor
     * cantidad vendida. Formato por linea: NombreProducto;Precio
     *
     * @param productos mapa de productos ya con su cantidad vendida calculada
     * @param rutaSalida ruta del archivo de salida
     * @throws IOException si ocurre un error escribiendo el archivo
     */
    private static void generarReporteProductos(Map<String, Producto> productos, String rutaSalida) throws IOException {
        List<Producto> ordenados = new ArrayList<>(productos.values());
        ordenados.sort(Comparator.comparingInt((Producto p) -> p.cantidadVendida).reversed());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaSalida))) {
            for (Producto p : ordenados) {
                writer.write(p.nombre + ";" + p.precio);
                writer.newLine();
            }
        }
    }
}
