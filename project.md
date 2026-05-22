# Historia de Usuario — Sistema de Inventario de Productos Tecnológicos

-----

## Información General del Proyecto

|Campo           |Detalle                                  |
|----------------|-----------------------------------------|
|**Proyecto**    |Sistema de Inventario de Productos Tech  |
|**Versión**     |1.0.0                                    |
|**Stack**       |Spring Boot · PostgreSQL · Docker        |
|**Arquitectura**|Capas (Controller → Service → Repository)|

-----

## Épica Principal

**Como** administrador del sistema,  
**quiero** gestionar un inventario de productos tecnológicos y consultar las ventas recientes,  
**para** tener visibilidad y control total sobre el stock y el movimiento de productos.

-----

## Historias de Usuario

-----

### HU-01 — Crear Producto

**Como** administrador,  
**quiero** registrar un nuevo producto tecnológico en el sistema,  
**para** mantener actualizado el catálogo de inventario.

#### Criterios de Aceptación

- El sistema permite ingresar los siguientes campos obligatorios: nombre, descripción, categoría, precio, cantidad en stock y SKU.
- El SKU debe ser único; el sistema retorna error `409 Conflict` si ya existe.
- El sistema guarda el producto en la base de datos PostgreSQL.
- La respuesta HTTP es `201 Created` con el objeto del producto creado.

#### Endpoint

```
POST /api/v1/products
```

#### Request Body (JSON)

```json
{
  "name": "Laptop Dell XPS 15",
  "description": "Laptop profesional de alto rendimiento",
  "category": "Laptops",
  "price": 1899.99,
  "stock": 25,
  "sku": "DELL-XPS15-001"
}
```

#### Response `201 Created`

```json
{
  "id": 1,
  "name": "Laptop Dell XPS 15",
  "sku": "DELL-XPS15-001",
  "stock": 25,
  "createdAt": "2026-04-09T10:00:00Z"
}
```

-----

### HU-02 — Listar Todos los Productos

**Como** administrador,  
**quiero** visualizar el listado completo de productos disponibles,  
**para** tener una vista general del inventario actual.

#### Criterios de Aceptación

- El sistema retorna todos los productos almacenados, ordenados por nombre ascendente.
- Soporta paginación: parámetros `page` y `size` con valores por defecto `0` y `10`.
- Si no hay productos, retorna lista vacía con status `200 OK`.

#### Endpoint

```
GET /api/v1/products?page=0&size=10
```

#### Response `200 OK`

```json
{
  "content": [
    {
      "id": 1,
      "name": "Laptop Dell XPS 15",
      "sku": "DELL-XPS15-001",
      "price": 1899.99,
      "stock": 25
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0
}
```

-----

### HU-03 — Consultar Producto por ID

**Como** administrador,  
**quiero** consultar los detalles de un producto específico por su ID,  
**para** revisar su información completa antes de hacer cambios.

#### Criterios de Aceptación

- El sistema retorna el producto con todos sus campos si el ID existe.
- Si el ID no existe, retorna `404 Not Found` con mensaje descriptivo.

#### Endpoint

```
GET /api/v1/products/{id}
```

#### Response `200 OK`

```json
{
  "id": 1,
  "name": "Laptop Dell XPS 15",
  "description": "Laptop profesional de alto rendimiento",
  "category": "Laptops",
  "price": 1899.99,
  "stock": 25,
  "sku": "DELL-XPS15-001",
  "createdAt": "2026-04-09T10:00:00Z",
  "updatedAt": "2026-04-09T10:00:00Z"
}
```

-----

### HU-04 — Actualizar Producto

**Como** administrador,  
**quiero** modificar la información de un producto existente,  
**para** mantener los datos actualizados ante cambios de precio, stock o descripción.

#### Criterios de Aceptación

- El sistema permite actualizar todos los campos editables de un producto.
- El campo `sku` no puede modificarse una vez creado.
- Si el producto no existe, retorna `404 Not Found`.
- Retorna el producto actualizado con status `200 OK`.

#### Endpoint

```
PUT /api/v1/products/{id}
```

#### Request Body (JSON)

```json
{
  "name": "Laptop Dell XPS 15 (2026)",
  "price": 1749.99,
  "stock": 30
}
```

-----

### HU-05 — Eliminar Producto

**Como** administrador,  
**quiero** eliminar un producto del inventario,  
**para** depurar el catálogo de productos descontinuados o incorrectos.

#### Criterios de Aceptación

- El sistema elimina el producto de forma lógica (soft delete con campo `active = false`).
- Si el producto no existe, retorna `404 Not Found`.
- Retorna `204 No Content` al eliminar con éxito.

#### Endpoint

```
DELETE /api/v1/products/{id}
```

-----

### HU-06 — Consultar Ventas Recientes

**Como** administrador,  
**quiero** ver un reporte de los productos vendidos recientemente,  
**para** monitorear el rendimiento del inventario y tomar decisiones de restock.

#### Criterios de Aceptación

- El sistema muestra las últimas ventas ordenadas de más reciente a más antigua.
- Soporta filtro por rango de fechas: parámetros `from` y `to` en formato `yyyy-MM-dd`.
- Cada registro muestra: nombre del producto, SKU, cantidad vendida, precio unitario, fecha de venta y total.
- Si no hay ventas en el rango, retorna lista vacía con `200 OK`.

#### Endpoint

```
GET /api/v1/sales/recent?from=2026-04-01&to=2026-04-09
```

#### Response `200 OK`

```json
[
  {
    "saleId": 101,
    "productName": "Laptop Dell XPS 15",
    "sku": "DELL-XPS15-001",
    "quantitySold": 3,
    "unitPrice": 1899.99,
    "total": 5699.97,
    "soldAt": "2026-04-08T14:30:00Z"
  }
]
```

-----

## Arquitectura por Capas

```
Controller layer -> Services layer -> Repository layer.
```


## Estrategia de Logs

|Nivel  |Cuándo usarlo                                      |
|-------|---------------------------------------------------|
|`DEBUG`|Listados, consultas de solo lectura sin impacto    |
|`INFO` |Creaciones, actualizaciones, consultas de ventas   |
|`WARN` |Eliminaciones de productos                         |
|`ERROR`|Fallos de base de datos, excepciones no controladas|
