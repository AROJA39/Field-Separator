/* ARCHIVO: FieldSeparatorSubFields.java
 * Separacion de campos en subcampos de acuerdo a expresiones regulares de
 * grupos que se parametrizan
 *
 * BANCO DE BOGOTA
 * VICEPRESIDENCIA DE DESARROLLO
 * GERENCIA DE DESARROLLO CANALES E INTEGRACION
 * 
 * ACTUALIZADO POR:         Juan Miguel Chaves
 * ULTIMA MODIFICACION:     Febrero 2 de 2023
 */

package com.bancodebogota.fieldseparator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

/**
 * Separacion de campos en subcampos de acuerdo a expresiones regulares de
 * grupos que se parametrizan
 * @author Juan Miguel Chaves
 */
public class FieldSeparatorSubFields {
    /**
     * Arreglo con las definiciones de separacion de campos para cada campo
     * en un determinado tipo de transaccion
     */
    static volatile Map<String, Object> definitions;
    /**
     * Arreglo con las definiciones de validaciones de campos y subcampos
     * por un determinado tipo de transaccion
     */
    static volatile Map<String, Object> validations;
    
    static String strUrlConfigServiceBase;
    
    /**
     * Metodo de inicializacion del componente, en el que se cargan los
     * archivos de configuracion en memoria del servicio
     * @param urlServicioConfiguracion URL del servicio por el que se consultan
     * archivos especificos de configuracion de componentes
     * @throws IOException Si fue imposible comunicarse con el servicio con los
     * archivos de configuracion
     */
    public static final void initialize(String urlServicioConfiguracion)
            throws IOException {
        strUrlConfigServiceBase = urlServicioConfiguracion;
        internalInitialize();
    }
    
    private static final void internalInitialize() throws IOException  {
      URL url = new URL(strUrlConfigServiceBase + "/get?JSON=SUBCAMPOS");
        HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
        JSONObject jsonObject = new JSONObject(Utilities.readFullyAsString(urlcon.getInputStream()));
        definitions = (Map<String, Object>) jsonObject.toMap();
        url = new URL(strUrlConfigServiceBase + "/get?JSON=VALIDACIONES");
        //url = new URL(URL_EXTERNAL_API + "/json/ValidacionCampos.json");
        urlcon = (HttpURLConnection) url.openConnection();
        jsonObject = new JSONObject(Utilities.readFullyAsString(urlcon.getInputStream()));
        validations = (Map<String, Object>) jsonObject.toMap();
    }

  /**
   * Metodo de separacion de campos recibe un Map de entrada con los campos a separar y retorna en
   * un Map de salida los campos que conforman dicha separacion
   *
   * @param sourceFields Campos que se quiere separar segun la configuracion de separacion de campos
   * @return Campos separados en subcampos como hayan sido encontrados
   */
  public static final Map<String, String> discriminate(Map<String, String> sourceFields)
      throws IOException {
        validateConfigurationLoad();
        Map<String,String> results = new TreeMap<>();
        for( String field: sourceFields.keySet() ) {
            if(definitions.containsKey(field)) {
                String pattern = definitions.get(field).toString();
                String descriptions = definitions.get(field + Constants.STR_SUFIJO_DESCRIPTIONS).toString();
                StringTokenizer st = null;
                if( descriptions == null || descriptions.equals("") ) {
                } else {
                    st = new StringTokenizer(descriptions, Constants.STR_DESCRIPTIONS_DELIM);
                }
                System.out.println("field:[" + field + "], value:[" + pattern + "]");
                Pattern patt = Pattern.compile(pattern);
                Matcher matcher = patt.matcher( sourceFields.get(field));
                if(matcher.matches()) {
                    for( int i=1; i <= matcher.groupCount(); i++ ) {
                        String subfieldSufix = st != null ? st.nextToken() : Constants.STR_SUBFIELD_PREFIX + String.format("%03d", i); ;
                        results.put(field +  Utilities.getProperty(FieldSeparatorTokens.props, "STR_FIELD_NAME_SEPARATOR", ".") + subfieldSufix, matcher.group(i));
                    }
                } else {
                    results.put(field +  Utilities.getProperty(FieldSeparatorTokens.props, "STR_FIELD_NAME_SEPARATOR", ".") + Constants.STR_WARNING_FIELD_EXPRESION, String.format(Constants.STR_VALUE_FOR_NO_MATCH, pattern, sourceFields.get(field) ) );
                }
            } else {
                results.put(field +  Utilities.getProperty(FieldSeparatorTokens.props, "STR_FIELD_NAME_SEPARATOR", ".") + Constants.STR_WARNING_FIELD_EXPRESION, Constants.STR_WARNING_REGULAR_EXPRESION_NOT_FOUND );
            }
            
        }
        return results;
    }
    /**
     * Metodo con el que se realizan validaciones de campo segun el
     * cumplimiento de una expresion regular en un campo
     * @param sourceFields Arreglo con los campos a validar
     * @return Arreglo con la validacion de cada campo, en caso que apruebe
     * ira un TRUE o si no un FALSE en una cadena de salida por campo y de
     * todos modos la descripcion de la regla que se cumple o se incumple
     */
    public static final Map<String,String> validate( Map<String, String> sourceFields ) {
        Map<String,String> results = new TreeMap<>();
        for( String field: sourceFields.keySet() ) {
            String pattern = validations.get(field).toString();
            String descriptions = validations.get(field + Constants.STR_SUFIJO_DESCRIPTIONS).toString();
            StringTokenizer st = null;
            if( descriptions == null || descriptions.equals("") ) {
            } else {
                st = new StringTokenizer(descriptions, Constants.STR_DESCRIPTIONS_DELIM);
            }
            System.out.println("field:[" + field + "], value:[" + pattern + "]");
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher( sourceFields.get(field));
            String subfieldSufix = st != null ? st.nextToken() : Constants.STR_SUBFIELD_PREFIX + String.format("%03d", 0);
            results.put(field + Utilities.getProperty(FieldSeparatorTokens.props, "STR_FIELD_NAME_SEPARATOR", ".") + "DESCRIPTION", ( matcher.matches() ? "TRUE " : "FALSE " ) + subfieldSufix);
        }
        return results;
    }

  public static final void validateConfigurationLoad() throws IOException {
      if( validations == null || definitions == null ) {
        internalInitialize();
      }
    }

}
