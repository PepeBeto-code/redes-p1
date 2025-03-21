# Tic-Tac-Toe Multijugador con Sockets

## Descripción

Este proyecto implementa un juego de Tic-Tac-Toe multijugador en terminal utilizando sockets TCP para la comunicación entre clientes y servidor. Incluye autenticación con persistencia de sesión mediante cookies y un protocolo de comunicación personalizado.

## Características

- Sistema de registro e inicio de sesión con manejo de cookies.
- Comunicación bidireccional cliente-servidor utilizando sockets.
- Diseño de un protocolo sencillo basado en JSON para intercambio de datos.
- Persistencia del estado del juego mediante un archivo JSON (game_state.json).
- Sincronización en tiempo real entre múltiples clientes.

## Tecnologías utilizadas

- **Lenguaje:** Java
- **Comunicación en red:** Sockets TCP
- **Formato de mensajes:** JSON
- **Persistencia:** Archivos JSON
- **Autenticación:** Cookies

## Instalación y Ejecución

### Requisitos previos

- Tener instalado Java (versión 8 o superior).
- Clonar el repositorio:

```bash
git clone https://github.com/tu-usuario/tic-tac-toe-sockets.git
cd tic-tac-toe-sockets
```

### Ejecución del servidor

```bash
java -jar servidor.jar
```

### Ejecución de los clientes

```bash
java -jar cliente1.jar
java -jar cliente2.jar
```

## Protocolo de Comunicación:

El protocolo se basa en el intercambio de mensajes JSON, donde cada mensaje incluye una acción a realizar (action) y datos relevantes (data). El servidor responde con un estado actualizado del juego o mensajes de error si corresponde.

### Ejemplo de solicitud del cliente

```json
{
  "action": "makeMove",
  "data": {
    "gameId": "1234",
    "player": "X",
    "position": [0, 1]
  }
}
```

### Respuesta del servidor

```json
{
  "status": "success",
  "data": {
    "board": [
      ["X", "O", "X"],
      ["O", "X", " "],
      [" ", " ", "O"]
    ],
    "nextPlayer": "O"
  }
}
```

## Cómo Jugar:

1. Inicia el servidor: java Server
2. Inicia los clientes: java Client1 y Java Clien2
3. Esperar a que un oponente se conecte. El registro e inicio de sesión se realizan automáticamente, generando un usuario y contraseña para cada cliente al ejecutar su clase correspondiente (Client1/Client2).
4. Juega introduciendo coordenadas (x,y) desde la terminal (ej. 1,2 para fila 1, columna 2), una a la vez.
5. Realizar movimientos en el tablero hasta que un jugador gane o se declare un empate.

## Autenticación y Sesión

- Los usuarios se registran e inician sesión automáticamente al conectarse al servidor (cambio considerado).
- El servidor genera un sessionId que el cliente envía en cada solicitud.
- Las sesiones se gestionan mediante cookies para evitar reautenticaciones innecesarias.

## Persistencia del Juego

El servidor almacena el estado del juego en game_state.json, lo que permite restaurar la partida en caso de reinicio del servidor.

## Capturas de Pantalla
