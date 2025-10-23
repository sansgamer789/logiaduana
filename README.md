
# LogiAduana - Spring Boot + SQLite (.db)

Proyecto base en Java (Spring Boot) con SQLite como base de datos (archivo `logiaduana.db`).

## Requisitos
- Java 17+
- Maven (para ejecutar `mvn spring-boot:run`)
- (Opcional) IDE: VS Code, IntelliJ

## Ejecutar
1. Copia el proyecto a tu máquina y abre terminal en la carpeta raíz.
2. Compilar / ejecutar con Maven:
```bash
mvn spring-boot:run
```
3. Al iniciar se creará `logiaduana.db` en la carpeta raíz y escuchará en `http://localhost:8080/`.
4. Credenciales demo:
- Email: admin@example.com
- Pass: Admin123!

Nota: por simplicidad de demo la contraseña está en formato `{noop}Admin123!` en el seed.
Para producción debes implementar encriptación con BCrypt y configurar usuarios adecuados.
