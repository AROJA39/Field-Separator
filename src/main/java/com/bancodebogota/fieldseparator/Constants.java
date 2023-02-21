/* ARCHIVO: Constants.java
 * Clase con las constantes usadas en los servicios de campos de transacciones
 *
 * BANCO DE BOGOTA
 * VICEPRESIDENCIA DE DESARROLLO
 * GERENCIA DE DESARROLLO CANALES E INTEGRACION
 * 
 * ACTUALIZADO POR:         Juan Miguel Chaves
 * ULTIMA MODIFICACION:     Febrero 2 de 2023
 */
package com.bancodebogota.fieldseparator;

/**
 * Clase con todas las constantes del programa
 * @author Juan Miguel Chaves
 */
public class Constants {
    /**
     * URL base de publicacion de servicios, donde se puede acceder a las
     * funcionalidades
     */
    static String STR_URL_CONFIG_SERVICE_BASE = "http://localhost:8085";
    public static final String STR_SUBFIELD_PREFIX = "SUBFIELD_";
    public static final String STR_FIELD_PREFIX = "FIELD_";
    public static final String STR_SUFIJO_DESCRIPTIONS = "_DESCRIPTIONS";
    public static final String STR_DESCRIPTIONS_DELIM = ",";
    public static String STR_NOMBRE_CRITERIO_SOLICITADO = "CRITERIA_ID";
    public static String STR_PATRON_NOMBRE_CAMPO = "%03d_%01d";
    public static String STR_NOMBRE_ARCHIVO = "JSON.json";
    /**
     * Constante con el nombre de la propiedad para el status
     */
    public static final String STR_LABEL_STATUS = "STATUS";
    /**
     * Constante con el nombre de la propiedad para la fecha del ultimo resync
     */
    public static final String STR_LABEL_DATETIME_RESYNC = "DATETIME_RESYNC";
    /**
     * Constante con el nombre de la propiedad para el endpoint
     */
    public static final String STR_LABEL_ENDPOINT = "ENDPOINT";
    /**
     * Constante con el valor de la propiedad para los creditos
     */
    public static final String STR_LABEL_CREDITS = "Este componente se desarroll\u00f3 con el objetivo de separar campos " + "con el proposito de facilitar el desarrollo del proyecto de intregracion " + "de componentes para procesamiento transaccional de negocio, con un equipo " + "a cargo del ingeniero Wilson Martinez: " + "Adriana Rojas Beltran, " + "Lady Mu\u00f1oz Marquez, " + "Karen , " + "Andres Villalba Gaviria, " + "Daniel Suarez Castillo, " + "Andres , " + "Juan Chaves Palacios";
    /**
     * Constante con el nombre de la propiedad para contador de solicitudes
     */
    public static final String STR_LABEL_NUMBER_REQUESTS = "NUMBER_REQUESTS";
    /**
     * Constante con el nombre de la propiedad para almacenar la versi�n de componente
     */
    public static final String STR_LABEL_VERSION = "VERSION";
    /**
     * Constante con el nombre de la propiedad para almacenar la hora del llamado
     */
    public static final String STR_LABEL_TIME_CALL = "TIME_OF_CALL";
    /**
     * Constante con el nombre de la propiedad para el tiempo de la ultima solicitud de estado
     */
    public static final String STR_LABEL_DATETIME_DATA_REQUEST = "REQUEST_STATE_TIME";
    /**
     * Constante con el nombre de la propiedad para el message
     */
    public static final String STR_LABEL_MESSAGE = "MESSAGE";
    /**
     * Constante con el nombre de la propiedad para contador de erradas
     */
    public static final String STR_LABEL_NUMBER_REQUESTS_ERROR = "NUMBER_REQUESTS_ERROR";
    /**
     * Constante con el nombre de la propiedad para los creditos
     */
    public static final String STR_LABEL_REFERENCE_CREDITS = "REFERENCE_CREDITS";
    /**
     * Constante con el nombre de la propiedad para almacenar el componente
     */
    public static final String STR_LABEL_COMPONENT = "COMPONENT";
    /**
     * Constante con el nombre de la propiedad para contador de exitosas
     */
    public static final String STR_LABEL_NUMBER_REQUESTS_OK = "NUMBER_REQUESTS_OK";
    /**
     * Constante con el nombre de la propiedad para la fecha de inicio del servicio
     */
    public static final String STR_LABEL_DATETIME_STARTED = "DATETIME_STARTED";
    /**
     * Constante con el nombre de la propiedad para almacenar la duraci�n del llamado
     */
    public static final String STR_LABEL_ELAPSED_TIME = "ELAPSED_TIME_NANOS";
    /**
     * Constante con el nombre de la propiedad las �ltimas solicitudes
     */
    public static final String STR_LABEL_LAST_REQUESTS = "LAST_REQUESTS";
    
    /**
     * Constante para el valor de subcampo ficticio cuando no ocurre match en la
     * expresion para separar campos
     */
    public static final String STR_VALUE_FOR_NO_MATCH = "NO_MATCH_ON_REGULAR_EXPRESION RE:[%s] AND VALUE:[%s]";
    
    /**
     * Nombre de subcampo usado para enmarcar un inpedimento para separar un campo
     */
    public static final String STR_WARNING_FIELD_EXPRESION = "WARNING_FIELD_EXPRESION";

    /**
     * Nombre de subcampo usado para enmarcar un inpedimento para separar un campo
     */
    public static final String STR_WARNING_REGULAR_EXPRESION_NOT_FOUND = "No existe expresion regular definida para este campo";
}
