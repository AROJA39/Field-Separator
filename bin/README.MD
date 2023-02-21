
FIELD SEPARATOR
===============
Descripcion
-----------
Este proyecto, trata de un servicio API REST, que permite resolver 
funcionalidades especificas de reconocimiento y separacion de campos de
transacción, así como su validación contra expresiones regulares que se
parametrizan de acuerdo al formato que se desea

Se trata de un proyecto Maven, por lo que en la carpeta principal encuentra
el archivo pom.xml que le permite compilar y generar el producto de este
proyecto con cualquier herramienta compatible, NetBeans, Eclipse, InteliJ...

Carpetas y archivos
-------------------
Las carpetas y archivos principales del proyecto son:
pom.xml -> Archivo con las dependencias y plugins necesarios para compilar
README.MD -> Archivo de ayuda para el desarrollador del proyecto
README.TXT -> Este archivo de ayuda para usuarios del componente producto
src -> Carpeta con los fuentes Java desarrollados para el proyecto
src\main -> Subcarpeta con fuentes de producto
src\test -> Subcarpeta con fuentes de prueba
target -> Carpeta donde quedan los Objetos Java compilados (.class)
dist\FieldSeparator.jar -> Carpeta y archivo del jar ejecutable armado a usar

Arranque
--------
Para arrancar el proyecto sin más, basta con ubicarse en la carpeta dist\ y
lanzar el comando:
```
java -jar FieldSeparator.jar
```

Configuracion
-------------
Tener en cuenta que debe tener el runtime de Java 1.8 instalado para que se
ejecute este programa con normalidad. Por defecto abre el puerto 8901 y se
conecta a un servicio de configuracion en el endpoint: 
http://localhost:8085 

Si se desea utilizar el servicio de configuracion en otra maquina, puede ser
necesario especificar en linea de comandos explicitamente el puerto de apertura
del servicio y el endpoint base de los servicios de configuracion cambiando el
comando de arranque a:
```
java -jar FieldSeparator.jar 8901 http://localhost:8085
```

El primer numero 8901 serua el puerto por el que se exponen los servicios REST
del producto y el endpoint http://localhost:8085 seria la base del URL del
servicio de configuracion.

Validaciones
------------
Para probar los servicios que responden datos de configuracion a el servicio
producto de esta carpeta se pueden probar en un browser normal estos endpoints
y observar que se retornen JSON correctamente de configuracion:
```
http://maquina_configuracion:8085/get?JSON=EXPRESIONESREGULARES
http://maquina_configuracion:8085/get?JSON=CRITERIODEACEPTACION
http://maquina_configuracion:8085/get?JSON=SUBCAMPOS
http://maquina_configuracion:8085/get?JSON=VALIDACIONES
http://maquina_configuracion:8085/get?JSON=TOKENS
```
Estos mismos enlaces pueden probarse desde la herramienta RESTDebugger.exe del
banco o desde la herramiena Postman para los que la tienen instalada y ver el
resultado JSON en un mejor formato.

Prueba básica de funcionalidades
----------------------------------------
Los endpoints que debería probarse de la funcionalidad en el producto son:

Consulta de las expresiones asociadas a un tipo de criterios pespecifico
http://localhost:8901/acceptancecriteria?CRITERIA_ID=44

Salvar en disco en un archivo JSON.json el contenido del cache del producto
http://localhost:8901/write

Validacion de un campo particular respecto a su expresion regulares
http://localhost:8901/validations?FIELD_041=0001565s

Separar en tokens campos que contienen tokens
http://localhost:8901/tokens?FIELD_126=%26 0000200054! QT00032 4010140000000000000000000000000 QP00056 XXEEEECCCCCCIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOFFFFFFFFF&FIELD_057=%26 0000200054! QT00032 4010140000000000000000000000000 QP00056 XXEEEECCCCCCIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOFFFFFFFFF&FIELD_005=PRUEBA

Hacer resync para recargar configuraciones de producto del servicio
http://localhost:8901/resync

Separacion de campos
http://localhost:8902/subfields?FIELD_037=000000008218&FIELD_041=0001792700007   &FIELD_043=AVV66Cra 30 10-77     00011001           

Por conveniencia, se han dejado junto a este archivo en la carpeta del
proyecto, varios archivos en formato JSON de la herramienta RESTDebugger para
realizar mas facilmente las solicitudes presentadas arriba en su orden:
```
acceptancecriteria.json
write.json
validations.json
tokens.json
resync.json
subfields.json
state.json
```
Para probar estas solicitudes se usa la herramienta RESTDebugger la cual
permite cargar solicitudes almacenadas en disco y ejecutarlaas para
validar sus salidas.