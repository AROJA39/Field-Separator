/* ARCHIVO: Utilities.java
 * Clase para agrupar las funciones y procedimientos utiles a los demos
 * componentes desarrollados
 *
 * BANCO DE BOGOTA
 * VICEPRESIDENCIA DE DESARROLLO
 * GERENCIA DE DESARROLLO CANALES E INTEGRACION
 * 
 * ACTUALIZADO POR:         Juan Miguel Chaves
 * ULTIMA MODIFICACION:     Febrero 1 de 2023
 */
package com.bancodebogota.fieldseparator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 * Clase para agrupar las funciones y procedimientos utiles a los demas componentes desarrollados
 *
 * @author Juan Miguel Chaves
 */
public class Utilities {
  /**
   * Metodo para leer el contenido de un stream completo como cadena en una variable que se retorna
   *
   * @param is Flujo de bytes de entrada a ser leido
   * @return Cadena con el contenido completo leido
   * @throws IOException Si falla alguna operacion de entrada/salida
   */
  public static final String readFullyAsString(InputStream is) throws IOException {
    BufferedReader brBufferedReader = new BufferedReader(new InputStreamReader(is));
    StringBuilder stringBuilder = new StringBuilder();
    String aux;
    while ((aux = brBufferedReader.readLine()) != null) {
      stringBuilder.append(aux);
    }
    return stringBuilder.toString();
  }

  /**
   * Método especifico para crear un objeto JSON que permita que su conversión a cadena este 
   * ordenada por clave. La especificación de JSON no obliga a que los campos sean ordenados, sin
   * embargo en la labor de desarrollo y seguimiento si que resulta util que los mismos sean
   * presentados
   * @param datos
   * @return 
   */
  public static final JSONObject printJSONObject(Map<String, String> datos) {
    JSONObject jsonObject = new JSONObject();
    try {
      Field changeMap = jsonObject.getClass().getDeclaredField("map");
      changeMap.setAccessible(true);
      changeMap.set(jsonObject, new LinkedHashMap<>());
      changeMap.setAccessible(false);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      System.err.println("IMPOSIBLE ACCEDER A OBJETO POR RUNTIME: [" + e + "]");
      System.exit(-1);
    }
    for (Map.Entry<String, String> entry : datos.entrySet()) {
      jsonObject.put(entry.getKey(), entry.getValue());
    }
    return jsonObject;
  }
  
  public static final String getProperty(Map<String,Object> config, String property, String defaultValue ) {
    return config.get(property) == null ? defaultValue : (String) config.get(property);
  }
}
