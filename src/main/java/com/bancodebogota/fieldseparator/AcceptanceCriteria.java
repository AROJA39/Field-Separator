/* ARCHIVO: AcceptanceCriteria.java
 * Clase para buscar las expresiones regulares a aplicar a campos como
 * criterios de aceptacion para un tipo de transaccion particular
 *
 * BANCO DE BOGOTA
 * VICEPRESIDENCIA DE DESARROLLO
 * GERENCIA DE DESARROLLO CANALES E INTEGRACION
 * 
 * ACTUALIZADO POR:         Juan Miguel Chaves
 * ULTIMA MODIFICACION:     Febrero 2 de 2023
 */
package com.bancodebogota.fieldseparator;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

/**
 * OBJETIVO
 * Clase para buscar las expresiones regulares a aplicar a campos como
 * criterios de aceptacion para un tipo de transaccion particular
 * 
 * ENTRADAS
 * Al inicializar se carga un Map con expresiones regulares asociadas a un
 * valor de clave
 * Al inicializar se carga un Map con criterios de aceptacion, que son nombres
 * de campo atados a expresiones, pero todos atados a un mismo tipo de
 * transaccion
 * 
 * PROCESAMIENTO
 * El metodo principal de prcesamiento es acceptanceCriteria que debe casar
 * una clave con el tipo de transaccion a las expresiones a aplicar a dicho
 * tipo por valor de nombre de campo, con dicho nombre va a la expresion y
 * asi la puede retornar
 * 
 * SALIDAS
 * acceptanceCriteria retorna las expresiones regulares que aplican por campo
 * a cada tipo particular de transaccion
 * 
* @author Juan Miguel Chaves
 */
public class AcceptanceCriteria {
    /**
     * Arreglo global de expresiones regulares en el que se buscan expresiones
     * que validan que campos de transacciones cumplen patrones configurados
     * de antemano, por valor de clave
     */
    volatile Map<String, Object> regularExpresions;
    
    /**
     * Arreglo global de cadenas de reglas o criterios de aceptación que
     * aplican por cada posible tipo de transaccion
     */
    volatile Map<String, Object> acceptanceCriteria;
    
    /**
     * Arreglo asociativo de arreglos asociativos en el que se almacenan en
     * memoria los tipos de transaccion resueltos con sus criterios de
     * aceptacion para resolverlos mas rapidamente a medida que el programa ya
     * se ha ejecutado.
     */
    Map<String, Map<String, String>> cache = new ConcurrentHashMap<>();
    
    /**
     * Metodo para forzar la carga anticipada del cache posible para el
     * procesamiento de criterios de aceptacion de separacion de campos
     */
    public final void forceLoadChache() {
        for(String keyForce: acceptanceCriteria.keySet()) {
            Map<String, String> dataInSimulated = new ConcurrentHashMap<>();
            dataInSimulated.put(Constants.STR_NOMBRE_CRITERIO_SOLICITADO, keyForce);
            Map<String, String> dataOutSimulated = acceptanceCriteria(dataInSimulated);
            System.out.println("Forzado de lectura en map length[" + dataOutSimulated.keySet().size() + "]");
        }
    }

    /**
     * Metodo de retoma a memoria de configuraciones que pudieron ser
     * actualizadas
     * @throws IOException Si existe algun problema para conectar el
     * repositorio de configuraciones
     */
    public final void resync()
            throws IOException {
        URL url = new URL(Constants.STR_URL_CONFIG_SERVICE_BASE + "/get?JSON=EXPRESIONESREGULARES");
        HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
        JSONObject jsonObject = new JSONObject(Utilities.readFullyAsString(urlcon.getInputStream()));
        regularExpresions = (Map<String,Object>) jsonObject.toMap();
        url = new URL(Constants.STR_URL_CONFIG_SERVICE_BASE + "/get?JSON=CRITERIODEACEPTACION");
        urlcon = (HttpURLConnection) url.openConnection();
        jsonObject = new JSONObject(Utilities.readFullyAsString(urlcon.getInputStream()));
        acceptanceCriteria = (Map<String,Object>) jsonObject.toMap();
        cache.clear();
    }
    
    /**
     * Metodo con el que se obtienen los criterios de aceptacion de una
     * transaccion basado en un campo en el que se incluye la clave que señala
     * el tipo particular de transaccion
     * @param sourceFields Arreglo con campos de entrada en el que se busca
     * el campo con el tipo de transaccion
     * @return un arreglo con los criterios de aceptacion particular para la
     * transaccion analizada, en particular las expresiones regulares de
     * aceptacion
     */
    public final Map<String,String> acceptanceCriteria( Map<String, String> sourceFields ) {
         String key = sourceFields.get(Constants.STR_NOMBRE_CRITERIO_SOLICITADO);
         if( !cache.containsKey(key) ) {
                String criterioEncontrado = acceptanceCriteria.get(key).toString();
                StringTokenizer strTokenizer = new StringTokenizer(criterioEncontrado, ",");
                Map<String, String> definicionActualCriterios = new TreeMap<>();
                while(strTokenizer.hasMoreTokens()) {
                    String strCriterioExpresion = strTokenizer.nextToken();
                    StringTokenizer stInterno = new StringTokenizer(strCriterioExpresion, "-");
                    int campo = Integer.parseInt(stInterno.nextToken());
                    int regla = Integer.parseInt(stInterno.nextToken());
                    String nombreCampoRegla = Constants.STR_FIELD_PREFIX + String.format(Constants.STR_PATRON_NOMBRE_CAMPO, campo, regla);
                    String valorCampoRegla = this.regularExpresions.get(strCriterioExpresion).toString();
                    definicionActualCriterios.put(nombreCampoRegla, valorCampoRegla);
                }
                this.cache.put(key, definicionActualCriterios);
        }
        return cache.get(key);
    }
    
    /**
     * Metodo para escribir el cache en disco en un archivo en disco
     * @throws IOException 
     */
    public final void writeChacheFile() 
            throws IOException {
        FileWriter fsw = new FileWriter(Constants.STR_NOMBRE_ARCHIVO);
        JSONObject objectToWrite = new JSONObject(this.cache);
        objectToWrite.write(fsw);
        fsw.flush();
        fsw.close();
    }
}
