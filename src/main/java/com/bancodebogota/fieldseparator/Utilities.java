/* ARCHIVO: Utilities.java
 * Clase para agrupar las funciones y procedimientos utiles a los demás
 * componentes desarrollados
 *
 * ULTIMA MODIFICACION:     Febrero 1 de 2023
 */
package com.bancodebogota.fieldseparator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Clase para agrupar las funciones y procedimientos utiles a los demás
 * componentes desarrollados
 * @author Juan Miguel Chaves
 */
public class Utilities {
    /**
     * Metodo para leer el contenido de un stream completo como cadena en una
     * variable que se retorna
     * @param is Flujo de bytes de entrada a ser leído
     * @return Cadena con el contenido completo leído
     * @throws IOException Si falla alguna operacion de entrada/salida
     */
    public static final String readFullyAsString(InputStream is ) throws IOException {
        BufferedReader brBufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String aux;
        while ((aux = brBufferedReader.readLine()) != null) {
            stringBuilder.append(aux);
        }
        return stringBuilder.toString();
    }
}
