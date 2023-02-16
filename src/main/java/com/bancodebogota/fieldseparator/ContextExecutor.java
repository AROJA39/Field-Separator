/* ARCHIVO: ContextExecutor.java
 * Clase para ejecutar logica en otros componentes facilitando el manejo de
 * condiciones de error
 *
 * ULTIMA MODIFICACION:     Febrero 1 de 2023
 */
package com.bancodebogota.fieldseparator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Esta clase sirve para 
 * @author Juan Miguel Chaves
 */
public class ContextExecutor {

    /**
     * El estado actual en un conjunto de Contextos
     */
    static List<ContextExecutor> actualState = new ArrayList<ContextExecutor>();
    
    /**
     * Contador de solicitudes totales
     */
    static volatile AtomicInteger requestCount = new AtomicInteger();
    /**
     * Contador de solicitudes Erradas
     */
    static volatile AtomicInteger errorResponsesCount = new AtomicInteger();
    /**
     * Contador de solicitudes Exitosas
     */
    static volatile AtomicInteger okResponsesCount = new AtomicInteger();
    /**
     * Variable con la fecha de arranque del sistema
     */
    static final Date datetimeStart = new Date();
    /**
     * Variable con la fecha del ultimo resync
     */
    static volatile Date datetimeResync = new Date();
    
    /**
     * Cadena con el nombre del servicio
     */
    String service;
    /**
     * Cadena con el contenido del endpoint asociado
     */
    String endpoint;
    /**
     * Cadena con la version del servicio especifico que representa este objeto
     */
    String version;
    /**
     * Tiempo de inicio de atencion en contador de nanos del sistema
     */
    long nanotimeIni;
    /**
     * Tiempo de fin de atencion en contador de nanos del sistema
     */
    long nanotimeEnd;
    Date dTime;
    RunnableWithThrows code;
    Exception exceptionThrow = null;
    int status =  200;
    String message = "OK";
    
    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
    /**
     * Metodo de establecimiento de la excepcion que se pudo capturar
     * @param e Excepcion capturada
     */
    public void setExceptionThrow(Exception e ) {
        this.exceptionThrow = e;
    }
    
    /**
     * Metodo de acceso a la excepcion que se pudo capturar
     * @return La excepcion capturada o null si no hubo
     */
    public Exception getExceptionThrow() {
        return exceptionThrow;
    }
        
    /**
     * Construtor del conexto de ejcucion
     * @param service Nombre que se le da a la funcionalidad o servicio
     * @param endpoint URL con el que se disfruta del servicio
     * @param version Una cadena representando la version del servicio ej: "1.0.0"
     * @param code La referencia al Runnable a ejecutar
     */
    public ContextExecutor( String service, String endpoint, String version, RunnableWithThrows code ) {
        this.nanotimeIni = System.nanoTime();
        this.service = service;
        this.endpoint = endpoint;
        this.version = version;
        this.code = code;
        this.dTime = new Date();
        requestCount.incrementAndGet();
    }
    
    /**
     * Metodo ejecutor de la logica en el contexto
     */
    public void RunToEnd() {
        try {
            code.run();
            okResponsesCount.incrementAndGet();
        } catch( Exception e ) {
            this.exceptionThrow = e;
            errorResponsesCount.incrementAndGet();
        }
        this.nanotimeEnd = System.nanoTime();
        actualState.add(this);
        System.out.print( "AVISO1:" + System.nanoTime());
    }
    
    /**
     * Metodo con el que se obtiene un JSON de todos los contextos almacenados
     * @return Un objeto compuesto de todos los conextos
     */
    public static String toStringJSON() {
        JSONObject objMain = new JSONObject();
        JSONArray objArray = new JSONArray();
        for(ContextExecutor ce: actualState) {
            objArray.put( ce.toJSON());
        }
        objMain.put(Constants.STR_LABEL_DATETIME_DATA_REQUEST, (new Date()).toString() );
        objMain.put(Constants.STR_LABEL_NUMBER_REQUESTS, requestCount );
        objMain.put(Constants.STR_LABEL_NUMBER_REQUESTS_OK, okResponsesCount );
        objMain.put(Constants.STR_LABEL_NUMBER_REQUESTS_ERROR, errorResponsesCount );
        objMain.put(Constants.STR_LABEL_DATETIME_STARTED, datetimeStart );
        objMain.put(Constants.STR_LABEL_DATETIME_RESYNC, datetimeResync );
        objMain.put(Constants.STR_LABEL_REFERENCE_CREDITS, Constants.STR_LABEL_CREDITS);
        
        
        
        objMain.put(Constants.STR_LABEL_LAST_REQUESTS, objArray);
        actualState.clear();
        return objMain.toString();
    }
    
    /**
     * Metodo con el que se obtiene un JSON de todo el contexto
     * @return Objeto JSOn individual de un contexto
     */
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put(Constants.STR_LABEL_COMPONENT, service);
        obj.put(Constants.STR_LABEL_ENDPOINT, endpoint);
        obj.put(Constants.STR_LABEL_ELAPSED_TIME, (this.nanotimeEnd - this.nanotimeIni));
        obj.put(Constants.STR_LABEL_TIME_CALL, this.dTime.toString());
        obj.put(Constants.STR_LABEL_VERSION, this.version);
        obj.put(Constants.STR_LABEL_STATUS, this.status);
        obj.put(Constants.STR_LABEL_MESSAGE, this.message);
        return obj;
    }
}
