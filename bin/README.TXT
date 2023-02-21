FIELD SEPARATOR

Este proyecto, trata de un servicio API REST, que permite resolver 
funcionalidades especificas de reconocimiento y separacion de campos de
transacción, así como su validación contra expresiones regulares que se
parametrizan de acuerdo al formato que se desea

Se trata de un proyecto Maven, por lo que en la carpeta principal encuentra
el archivo pom.xml que le permite compilar y generar el producto de este
proyecto con cualquier herramienta compatible, NetBeans, Eclipse, InteliJ...

Las carpetas y archivos principales del proyecto son:
pom.xml -> Archivo con las dependencias y plugins necesarios para compilar
README.MD -> Archivo de ayuda para el desarrollador del proyecto
README.TXT -> Este archivo de ayuda para usuarios del componente producto
src -> Carpeta con los fuentes Java desarrollados para el proyecto
src\main -> Subcarpeta con fuentes de producto
src\test -> Subcarpeta con fuentes de prueba
target -> Carpeta donde quedan los Objetos Java compilados (.class)
dist\FieldSeparator.jar -> Carpeta y archivo del jar ejecutable armado a usar

Para arrancar el proyecto sin más, basta con ubicarse en la carpeta dist\ y
lanzar el comando:

java -jar FieldSeparator.jar

Tener en cuenta que debe tener el runtime de Java 1.8 instalado para que se
ejecute este programa con normalidad. Por defecto abre el puerto 8901 y se
conecta a un servicio de configuracion en el endpoint: 
http://maquina_configuracion:8085 la idea es que como es una maquina de uso
recurrente en servicios de atencion de transacciones para leer archivos o 
claves de configuracion, modifique su archivo de hosts con la IP de la
maquina donde sabe que atiende el servicio de configuracion. En el archivo
hosts (Usualmente en C:\Windows\System32\drivers\etc) una linea:

192.168.1.59	maquina_configuracion

Para probar los servicios que responden datos de configuracion a el servicio
producto de esta carpeta se pueden probar en un browser normal estos endpoints
y observar que se retornen JSON correctamente de configuracion:

http://maquina_configuracion:8085/get?JSON=EXPRESIONESREGULARES
http://maquina_configuracion:8085/get?JSON=CRITERIODEACEPTACION
http://maquina_configuracion:8085/get?JSON=SUBCAMPOS
http://maquina_configuracion:8085/get?JSON=VALIDACIONES
http://maquina_configuracion:8085/get?JSON=TOKENS

Estos mismos enlaces pueden probarse desde la herramienta RESTDebugger.exe del
banco o desde la herramiena Postman para los que la tienen instalada y ver el
resultado JSON en un mejor formato.

