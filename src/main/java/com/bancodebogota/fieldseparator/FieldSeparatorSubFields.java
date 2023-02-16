/* ARCHIVO: FieldSeparatorSubFields.java
 * Separacion de campos en subcampos de acuerdo a expresiones regulares de
 * grupos que se parametrizan
 *
 * ULTIMA MODIFICACION:     Febrero 2 de 2023
 */

package com.bancodebogota.fieldseparator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

/**
 * Separacion de campos en subcampos de acuerdo a expresiones regulares de
 * grupos que se parametrizan
 * @author Juan Miguel Chaves
 */
public class FieldSeparatorSubFields {
    static volatile Map<String, String> definitions;
    static volatile Map<String, String> validations;
    
    public static final void initialize(String urlServicioConfiguracion)
            throws IOException {
        Constants.STR_URL_CONFIG_SERVICE_BASE = urlServicioConfiguracion;
        URL url = new URL(Constants.STR_URL_CONFIG_SERVICE_BASE + "/get?JSON=SUBCAMPOS");
        HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
        JSONObject jsonObject = new JSONObject(Utilities.readFullyAsString(urlcon.getInputStream()));
        definitions = (Map) jsonObject.toMap();
        url = new URL(Constants.STR_URL_CONFIG_SERVICE_BASE + "/get?JSON=VALIDACIONES");
        //url = new URL(URL_EXTERNAL_API + "/json/ValidacionCampos.json");
        urlcon = (HttpURLConnection) url.openConnection();
        jsonObject = new JSONObject(Utilities.readFullyAsString(urlcon.getInputStream()));
        validations = (Map) jsonObject.toMap();
    }
    
    public static final Map<String,String> discriminate( Map<String, String> sourceFields ) {
        Map<String,String> results = new HashMap<>();
        for( String field: sourceFields.keySet() ) {
            String pattern = definitions.get(field);
            String descriptions = definitions.get(field + Constants.STR_SUFIJO_DESCRIPTIONS);
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
                    results.put(field +  Constants.STR_FIELD_NAME_SEPARATOR + subfieldSufix, matcher.group(i));
                }
            }
        }
        return results;
    }
    
    public static final Map<String,String> validate( Map<String, String> sourceFields ) {
        Map<String,String> results = new HashMap<>();
        for( String field: sourceFields.keySet() ) {
            String pattern = validations.get(field);
            String descriptions = validations.get(field + Constants.STR_SUFIJO_DESCRIPTIONS);
            StringTokenizer st = null;
            if( descriptions == null || descriptions.equals("") ) {
            } else {
                st = new StringTokenizer(descriptions, Constants.STR_DESCRIPTIONS_DELIM);
            }
            System.out.println("field:[" + field + "], value:[" + pattern + "]");
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher( sourceFields.get(field));
            String subfieldSufix = st != null ? st.nextToken() : Constants.STR_SUBFIELD_PREFIX + String.format("%03d", 0);
            results.put(field +  Constants.STR_FIELD_NAME_SEPARATOR + "DESCRIPTION", ( matcher.matches() ? "TRUE " : "FALSE " ) + subfieldSufix);
        }
        return results;
    }

}
