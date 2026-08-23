# Pago — Contrato de la API REST

## Base

- **Base path**: `/api/pagos`
- **Formato**: JSON — **Puerto**: 8084 (configurable con `PORT`)

## Recursos

| Método | Ruta | Códigos de estado | Descripción |
|--------|------|-------------------|-------------|
| GET | `/api/pagos` | 200 | Lista todos los recursos |
| GET | `/api/pagos/{id}` | 200 / 404 | Obtiene un recurso por id |
| POST | `/api/pagos` | 201 / 400 | Crea un recurso |
| PUT | `/api/pagos/{id}` | 200 / 404 / 400 | Actualiza un recurso |
| DELETE | `/api/pagos/{id}` | 204 / 404 | Elimina un recurso |

## Atributos de un recurso

| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| id | Long | - | Identificador autogenerado |
| pedido | String | Sí | Campo principal del recurso |
| metodo | String | No | Campo del dominio |
| monto | BigDecimal | No | Campo del dominio |

## Ejemplos con curl

```bash
# Listar
curl http://localhost:8084/api/pagos

# Crear
curl -X POST http://localhost:8084/api/pagos \
  -H "Content-Type: application/json" \
  -d '{"pedido":"Demo"}'

# Obtener por id
curl http://localhost:8084/api/pagos/1

# Actualizar
curl -X PUT http://localhost:8084/api/pagos/1 \
  -H "Content-Type: application/json" \
  -d '{"pedido":"Actualizado"}'

# Eliminar
curl -X DELETE http://localhost:8084/api/pagos/1
```
