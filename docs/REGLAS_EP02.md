# Integridad del dominio pagos

Métodos TARJETA, TRANSFERENCIA o EFECTIVO. Transacciones COBRO o REEMBOLSO con estados PENDIENTE, APROBADA o RECHAZADA. Monto positivo entero. Los cobros aprobados acumulados no superan el pago; los reembolsos aprobados acumulados no superan lo cobrado. El CRUD registra operaciones académicas, no procesa dinero externo.

## Alcance de la evaluación

Se mantienen controller/service/repository/model, CRUD REST, relaciones OneToMany/ManyToOne, MySQL, Maven y Git. Las reglas hacen coherentes los datos retornados y las pruebas de éxito/error (IE1, IE2, IE3, IE5, IE6). No se agregan componentes externos. README, Postman y consultas SQL respaldan IE4, IE7 e IE10.

La colección incluye 9 peticiones de CRUD/lectura, 12 casos de error y 6 peticiones de eliminación/cascada. `mvn clean install` ejecuta pruebas unitarias, MockMvc con JPA/H2 y escenarios Cucumber. MySQL se demuestra con el perfil mysql y la colección.
