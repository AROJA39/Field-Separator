/* ARCHIVO: Constants.java
 * Clase con las onstantes usadas en los servicios de campos de transacciones
 *
 * ULTIMA MODIFICACION:     Febrero 2 de 2023
 */
package com.bancodebogota.fieldseparator;

/**
 * Clase con todas las constantes del programa
 * @author Juan Miguel Chaves
 */
public class Constants {

    static String STR_URL_CONFIG_SERVICE_BASE = "http://localhost:8085";
    public static final String STR_PATRON_TOKEN_QP = FieldSeparatorTokens.props.getProperty("STR_PATRON_TOKEN_QP", "\\A(\\w{2})(\\w{4})(\\w{6})(\\w{35})(\\w{1})(\\w{8})\\Z");
    public static final String STR_FIELD_NAME_SEPARATOR = FieldSeparatorTokens.props.getProperty("STR_FIELD_NAME_SEPARATOR", ".");
    public static final String STR_PATRON_OTRO_TOKEN = FieldSeparatorTokens.props.getProperty("STR_PATRON_OTRO_TOKEN", "\\A[\\!][ ](\\w{2})(\\d{5})[ ](.*?)\\Z");
    public static final String STR_FIELD_TOKEN_ROOT = FieldSeparatorTokens.props.getProperty("STR_FIELD_TOKEN_ROOT", "$1.");
    public static final String STR_FIELD_TOKEN_PATTERN = FieldSeparatorTokens.props.getProperty("STR_FIELD_TOKEN_PATTERN", "(FIELD_(?:(?:126)|(?:057)))");
    public static final String STR_LONGITUD_TOTAL = FieldSeparatorTokens.props.getProperty("STR_LONGITUD_TOTAL", "LONGITUD_TOTAL");
    public static final String STR_STATUS_PROCESS = FieldSeparatorTokens.props.getProperty("STR_STATUS_PROCESS", "STATUS_PROCESS");
    // 4915110100000000
    public static final String STR_PATRON_INI_TOKEN = FieldSeparatorTokens.props.getProperty("STR_PATRON_INI_TOKEN", "\\A[\\&][ ](\\d{5})(\\d{5})(.+)\\Z");
    public static final String STR_PATRON_TOKEN_QT = FieldSeparatorTokens.props.getProperty("STR_PATRON_TOKEN_QT", "\\A(\\w{6})(\\w{12})(\\d{12})(\\d[\\d ]{1})\\Z");
    public static final String STR_STATUS_DESCRIPTION = FieldSeparatorTokens.props.getProperty("STR_STATUS_DESCRIPTION", "STATUS_DESCRIPTION");
    public static final String STR_SUBFIELD = FieldSeparatorTokens.props.getProperty("STR_SUBFIELD", "SUBFIELD_");
    public static final String STR_TOKEN_QT = FieldSeparatorTokens.props.getProperty("STR_TOKEN_QT", "QT");
    public static final String STR_PATRON_TOKEN_BASE = FieldSeparatorTokens.props.getProperty("STR_PATRON_TOKEN_BASE", "\\A(.*?)\\Z");
    public static final String STR_TOKENS_ENCONTRADOS = FieldSeparatorTokens.props.getProperty("STR_TOKENS_ENCONTRADOS", "TOKENS_ENCONTRADOS");
    public static final String STR_TOKEN_QP = FieldSeparatorTokens.props.getProperty("STR_TOKEN_QP", "QP");
    public static final String STR_TIME_PROCESS = FieldSeparatorTokens.props.getProperty("STR_TIME_PROCESS", "TIME_PROCESS");
    public static final String STR_STATUS_OWNER_PROCESS = FieldSeparatorTokens.props.getProperty("STR_STATUS_OWNER_PROCESS", "STATUS_OWNER_PROCESS");
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
     * Constante con el nombre de la propiedad para almacenar la versión de componente
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
     * Constante con el nombre de la propiedad para almacenar la duración del llamado
     */
    public static final String STR_LABEL_ELAPSED_TIME = "ELAPSED_TIME_NANOS";
    /**
     * Constante con el nombre de la propiedad las últimas solicitudes
     */
    public static final String STR_LABEL_LAST_REQUESTS = "LAST_REQUESTS";
    
}
