# API REST: pagos

Base local: http://localhost:8084/api. Swagger UI: http://localhost:8084/swagger-ui/index.html.

| Método | Ruta | HTTP de éxito |
|---|---|---:|
| POST | /pagos | 201 |
| GET | /pagos | 200 |
| GET | /pagos/{id} | 200 |
| PUT | /pagos/{id} | 200 |
| DELETE | /pagos/{id} | 204 |
| POST | /pagos/{id}/transacciones | 201 |
| GET | /pagos/{id}/transacciones | 200 |
| GET | /transacciones/{id} | 200 |
| PUT | /transacciones/{id} | 200 |
| DELETE | /transacciones/{id} | 204 |

## Crear entidad principal

```json
{
  "pedido": "PED-DEMO",
  "metodo": "TARJETA",
  "monto": 19980
}
```

## Crear entidad relacionada

```json
{
  "tipo": "COBRO",
  "monto": 19980,
  "estado": "APROBADA",
  "referencia": "FG-DEMO-01"
}
```

Usar el ID retornado por la creación del padre. Los ID son generados por la BD. Editar los hijos mediante sus propias rutas. Ver las reglas y los campos calculados en REGLAS_EP02.md.

Errores: 400 para datos o JSON inválidos; 404 para recurso/relación local inexistente; 409 para conflictos de integridad o unicidad cuando corresponda. Un campo demasiado largo devuelve 400. Los mensajes y validationErrors se entregan mediante ApiExceptionHandler.
