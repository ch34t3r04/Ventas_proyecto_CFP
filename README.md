# Proyecto de Gestión de Ventas

## Descripción
Este proyecto forma parte del módulo de Programación (Politécnico Grancolombiano). Consiste en un sistema que procesa archivos planos con información de vendedores, productos y ventas, generando reportes de desempeño.

## Estado del proyecto

| Entrega | Semana | Estado |
|---|---|---|
| Entrega 1 | Semana 3 | ✅ Completa — Generación de archivos de prueba |
| Entrega 2 | Semana 5 | ✅ Completa — Generación de reportes |
| Entrega 3 | Semanas 7 y 8 | ⏳ Pendiente — Entrega final y sustentación |

## Estructura del proyecto
```
├── src/
│   ├── GenerateInfoFiles.java
│   └── main.java
├── elementos_faltantes.txt
└── README.md
```

## Requisitos
- Java 8 (JDK)
- Eclipse para Java Developers o Visual Studio Code con la extensión "Extension Pack for Java"

---

## Clase GenerateInfoFiles (Entrega 1)

Genera los archivos de prueba pseudoaleatorios que sirven como entrada del sistema, sin solicitar información al usuario.

| Método | Función |
|---|---|
| `createProductsFile(int productsCount)` | Genera `productos.txt`: `IDProducto;Nombre;Precio` |
| `createSalesManInfoFile(int salesmanCount)` | Genera `vendedores.txt`: `TipoDocumento;NumeroDocumento;Nombres;Apellidos`, y crea el archivo de ventas de cada uno |
| `createSalesMenFile(int randomSalesCount, String name, long id)` | Genera el archivo individual de ventas de un vendedor: `TipoDocumento;NumeroDocumento` en la primera línea, seguido de `IDProducto;Cantidad;` por cada venta |

## Clase main (Entrega 2)

Lee los archivos generados por `GenerateInfoFiles` y produce los reportes finales, sin solicitar información al usuario.

| Método | Función |
|---|---|
| `leerProductos(String rutaArchivo)` | Carga `productos.txt` en un mapa indexado por ID de producto |
| `leerVendedores(String rutaArchivo)` | Carga `vendedores.txt` en una lista de vendedores |
| `procesarArchivosDeVentas(String carpeta, ...)` | Recorre todos los archivos `ventas_*.txt`, acumulando dinero recaudado por vendedor y cantidad vendida por producto |
| `generarReporteVendedores(...)` | Genera `reporteVendedores.csv`, ordenado de mayor a menor dinero recaudado: `NombreCompleto;DineroRecaudado` |
| `generarReporteProductos(...)` | Genera `reporteProductos.csv`, ordenado de mayor a menor cantidad vendida: `NombreProducto;Precio` |

---

## Cómo ejecutarlo

**Desde terminal**, dentro de la carpeta `src`:
```bash
javac GenerateInfoFiles.java main.java
java GenerateInfoFiles
java main
```

**Desde Eclipse o VS Code:**
Ejecutar primero `GenerateInfoFiles` (genera los archivos de entrada) y luego `main` (genera los reportes).

## Salida esperada

Tras ejecutar `GenerateInfoFiles`:
- `productos.txt`
- `vendedores.txt`
- Un archivo `ventas_<id>_<nombre>.txt` por cada vendedor

Tras ejecutar `main`:
- `reporteVendedores.csv`
- `reporteProductos.csv`

Y en consola: `Archivos generados exitosamente.` / `Reportes generados exitosamente.`

## Elementos pendientes

Ver [`elementos_faltantes.txt`](./elementos_faltantes.txt) para el detalle de los elementos extra aún no implementados (archivos serializados, validación de datos incoherentes, múltiples archivos de ventas por vendedor).

## Autor
Juan Sebastián Uribe López
