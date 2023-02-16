/* ARCHIVO: AcceptanceCriteria.java
 * Clase para buscar las expresiones de los criterios de aceptacion para un tipo
 * de transaccion particular
 *
 * ULTIMA MODIFICACION:     Febrero 2 de 2023
 */
package com.bancodebogota.fieldseparator;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

/**
 * Clase para buscar las expresiones de los criterios de aceptacion para un tipo
 * de transaccion particular
 * @author Juan Miguel Chaves
 */
public class AcceptanceCriteria {
    volatile Map<String, String> regularExpresions;
    volatile Map<String, String> acceptanceCriteria;
    Map<String, Map<String, String>> cache = new ConcurrentHashMap<>();

    /**
     * Método de retoma a memoria de configuraciones que pudieron ser
     * actualizadas
     * @throws IOException 
     */
    public final void resync()
            throws IOException {
        URL url = new URL(Constants.STR_URL_CONFIG_SERVICE_BASE + "/get?JSON=EXPRESIONESREGULARES");
        HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
        JSONObject jsonObject = new JSONObject(Utilities.readFullyAsString(urlcon.getInputStream()));
        regularExpresions = (Map) jsonObject.toMap();
        url = new URL(Constants.STR_URL_CONFIG_SERVICE_BASE + "/get?JSON=CRITERIODEACEPTACION");
        urlcon = (HttpURLConnection) url.openConnection();
        jsonObject = new JSONObject(Utilities.readFullyAsString(urlcon.getInputStream()));
        acceptanceCriteria = (Map) jsonObject.toMap();
        cache.clear();
    }
    
    /**
     * Metodo con el que se combinan los criterios de aceptacion de una
     * transaccion
     * @param sourceFields
     * @return 
     */
    public final Map<String,String> acceptanceCriteria( Map<String, String> sourceFields ) {
         String key = sourceFields.get(Constants.STR_NOMBRE_CRITERIO_SOLICITADO);
         if( !cache.containsKey(key) ) {
                String criterioEncontrado = acceptanceCriteria.get(key);
                StringTokenizer strTokenizer = new StringTokenizer(criterioEncontrado, ",");
                Map<String, String> definicionActualCriterios = new ConcurrentHashMap<>();
                while(strTokenizer.hasMoreTokens()) {
                    String strCriterioExpresion = strTokenizer.nextToken();
                    StringTokenizer stInterno = new StringTokenizer(strCriterioExpresion, "-");
                    int campo = Integer.parseInt(stInterno.nextToken());
                    int regla = Integer.parseInt(stInterno.nextToken());
                    String nombreCampoRegla = Constants.STR_FIELD_PREFIX + String.format(Constants.STR_PATRON_NOMBRE_CAMPO, campo, regla);
                    String valorCampoRegla = this.regularExpresions.get(strCriterioExpresion);
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
