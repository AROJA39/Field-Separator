/* ARCHIVO: FieldSeparatorTokens.java
 * Separacion de campos en tokens de acuerdo a expresiones regulares de
 * identificacion de tokens que se parametrizan
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

/**
 * Separacion de campos en tokens de acuerdo a expresiones regulares de
 * identificacion de tokens que se parametrizan
 * @author Juan Miguel Chaves
 */
public class FieldSeparatorTokens {
    /**
     * Propiedades de configuracion del componente de separacion de tokens
     */
    static volatile Map<String,Object> props = new TreeMap<>();
    /**
     * Constante para asimilar un comportamiento optimista el algoritmo de
     * reconocimiento de tokes, de ser estricto en tamaños y demarcaciones a
     * ser flexible y reconocer los tokes que sean posibles
     */
    public static final boolean F_OPTIMISTIC_OPERATION = true;
    /**
     * Metodo para retomar la configuracion de persistencia y cargarla en la
     * memoria de proceso
     * @throws IOException 
     */
    public final void resync()
            throws IOException {
        URL url = new URL(Constants.STR_URL_CONFIG_SERVICE_BASE + "/get?JSON=TOKENS");
        HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
        JSONObject jsonObject = new JSONObject(Utilities.readFullyAsString(urlcon.getInputStream()));
        props.clear();
        props.putAll(jsonObject.toMap());
    }
    /**
     * Metodo analizador de campos tokenizados
     * @param hmStructuredDataInput estructura en la que vienen varios campos tokenizados
     * @return La descomposicion de campos
     */
    public Map<String, String> fieldAnalyzer(Map<String, String>  hmStructuredDataInput) {
        Map<String, String> hmStructuredData = new TreeMap<>();
        for(String fieldName: hmStructuredDataInput.keySet()) {
            if( fieldName.matches(Utilities.getProperty(FieldSeparatorTokens.props, "STR_FIELD_TOKEN_PATTERN", "(FIELD_(?:(?:126)|(?:057)))"))) {
                String childRoot = fieldName.replaceFirst(
                    Utilities.getProperty(FieldSeparatorTokens.props, "STR_FIELD_TOKEN_PATTERN", "(FIELD_(?:(?:126)|(?:057)))"),
                    Utilities.getProperty(FieldSeparatorTokens.props, "STR_FIELD_TOKEN_ROOT", "$1."));
                String strDescripcionOptimista = "Mensajes: ";
                long lTiempoIni = System.nanoTime();
                try {
                    hmStructuredData.put(childRoot + 
                        Utilities.getProperty(FieldSeparatorTokens.props, "STR_TOKENS_ENCONTRADOS", "TOKENS_ENCONTRADOS"), "" + 0 );
                    hmStructuredData.put(childRoot + 
                        Utilities.getProperty(FieldSeparatorTokens.props,"STR_LONGITUD_TOTAL", "LONGITUD_TOTAL"), "" + 0 );
                    String strValorCompleto = hmStructuredDataInput.get(fieldName);
                    final String STR_TAG_BASE2 = Utilities.getProperty(FieldSeparatorTokens.props, "STR_FIELD_NAME_SEPARATOR", ".")
                        + Utilities.getProperty(FieldSeparatorTokens.props, "STR_SUBFIELD", "SUBFIELD_");
                    if(strValorCompleto == null)
                        throw new NullPointerException( "Campo Tokenizado en null" );
                    Pattern p1 = Pattern.compile(Utilities.getProperty(FieldSeparatorTokens.props,"STR_PATRON_INI_TOKEN", "\\A[\\&][ ](\\d{5})(\\d{5})(.+)\\Z"));
                    Matcher m1 = p1.matcher(strValorCompleto);
                    if(m1.matches()) {
                        int iNumTokensAdicionales = Integer.parseInt(m1.group(1));
                        int iTamanyo = Integer.parseInt(m1.group(2));
                        String strResto = m1.group(3);
                        if( strValorCompleto.length() != iTamanyo ) {
                            if( F_OPTIMISTIC_OPERATION ) {
                                strDescripcionOptimista += "Tamaño total [" + iTamanyo + "] expresado en el token errado [" + strResto.length() + "], ";
                            }
                            else {
                                throw new ArrayIndexOutOfBoundsException("Tamaño total [" + iTamanyo + "] expresado en el token errado [" + strResto.length() + "]");
                            }
                        }
                        iNumTokensAdicionales--; // El token principal de encabezado se incluye en esta cuenta, por lo que se descarta para validar
                        hmStructuredData.put(childRoot + 
                            Utilities.getProperty(FieldSeparatorTokens.props, "STR_TOKENS_ENCONTRADOS", "TOKENS_ENCONTRADOS"), "" +iNumTokensAdicionales );
                        hmStructuredData.put(childRoot + Utilities.getProperty(FieldSeparatorTokens.props,"STR_LONGITUD_TOTAL", "LONGITUD_TOTAL"), "" + iTamanyo );
                        final String STR_PATRON_OTRO_TOKEN = Utilities.getProperty(FieldSeparatorTokens.props, "STR_PATRON_OTRO_TOKEN", "\\A[\\!][ ](\\w{2})(\\d{5})[ ](.*?)\\Z");
                        Pattern p2 = Pattern.compile(STR_PATRON_OTRO_TOKEN);
                        String strRestoToken;
                        for( int i = 0; i < iNumTokensAdicionales || F_OPTIMISTIC_OPERATION; i++ )
                        {
                            if( strResto == null || strResto.equals(""))
                              break;
                            Matcher m2 = p2.matcher(strResto);
                            if(m2.matches()) {
                                String strNombreToken = m2.group(1);
                                int iTamanyoToken = Integer.parseInt(m2.group(2));
                                strRestoToken = m2.group(3);
                                if(iTamanyoToken > strRestoToken.length()) {
                                  strDescripcionOptimista += "Tamaño del token [" + iTamanyoToken + "] expresado es errado longitud disponible [" + strRestoToken.length() + "], ";
                                  iTamanyoToken = strRestoToken.length();
                                }
                                strResto = strRestoToken.substring(iTamanyoToken);
                                strRestoToken = strRestoToken.substring(0, iTamanyoToken);

                                List<String> datos = descomposeToken(strNombreToken, strRestoToken);
                                String descriptionsConfigForField = Utilities.getProperty(FieldSeparatorTokens.props, "STR_PATRON_TOKEN_BASE", "STR_PATRON_TOKEN_") + strNombreToken + Constants.STR_SUFIJO_DESCRIPTIONS;
                                String descriptions = Utilities.getProperty(FieldSeparatorTokens.props, descriptionsConfigForField, null);
                                StringTokenizer st = null;
                                if( descriptions == null || descriptions.equals("") ) {
                                } else {
                                    st = new StringTokenizer(descriptions, Constants.STR_DESCRIPTIONS_DELIM);
                                }
                                int j = 1;
                                for(String dat: datos) {
                                  String subfieldSufix = st != null
                                      ? Utilities.getProperty(FieldSeparatorTokens.props, "STR_FIELD_NAME_SEPARATOR", ".") +  st.nextToken()
                                      : STR_TAG_BASE2 + String.format("%03d", j);
                                  hmStructuredData.put(childRoot + strNombreToken  + subfieldSufix , dat );
                                  j++;
                                }
                            }
                            else {
                                if( F_OPTIMISTIC_OPERATION ) {
                                    strDescripcionOptimista += "Token no encontrado [" + STR_PATRON_OTRO_TOKEN + "] en: [" + strResto + "], ";
                                    break;
                                } else {
                                    throw new NoSuchElementException("Token no encontrado [" + STR_PATRON_OTRO_TOKEN + "] en: [" + strResto + "]");
                                }
                            }
                        }
                    }
                    else {
                        throw new NoSuchElementException("No encontrado token principal: [" + Utilities.getProperty(FieldSeparatorTokens.props,"STR_PATRON_INI_TOKEN", "\\A[\\&][ ](\\d{5})(\\d{5})(.+)\\Z") + "] en: [" + strValorCompleto + "]");
                    }
                    hmStructuredData.put(childRoot+
                        Utilities.getProperty(FieldSeparatorTokens.props,"STR_STATUS_PROCESS", "STATUS_PROCESS"), "OK");
                    if( F_OPTIMISTIC_OPERATION ) {
                        hmStructuredData.put(childRoot+
                            Utilities.getProperty(FieldSeparatorTokens.props,"STR_STATUS_DESCRIPTION", "STATUS_DESCRIPTION"), strDescripcionOptimista.equals ("Mensajes: ") ? "SIN NOVEDAD" : "Procesamiento con novedad. " + strDescripcionOptimista.substring(0, strDescripcionOptimista.length() - 2 ));
                    } else {
                        hmStructuredData.put(childRoot+
                            Utilities.getProperty(FieldSeparatorTokens.props,"STR_STATUS_DESCRIPTION", "STATUS_DESCRIPTION"), "Procesamiento todo OK");  
                    }
                }
                catch( Exception ae) {
                    hmStructuredData.put(childRoot+
                        Utilities.getProperty(FieldSeparatorTokens.props,"STR_STATUS_PROCESS", "STATUS_PROCESS"), "NO");
                    hmStructuredData.put(childRoot+
                        Utilities.getProperty(FieldSeparatorTokens.props,"STR_STATUS_DESCRIPTION", "STATUS_DESCRIPTION"), ae.getMessage());
                }
                final String methodName = new Object() {}
                                    .getClass()
                                    .getEnclosingMethod()
                                    .getName();
                hmStructuredData.put(childRoot+
                    Utilities.getProperty(FieldSeparatorTokens.props, "STR_STATUS_OWNER_PROCESS", "STATUS_OWNER_PROCESS"), FieldSeparatorTokens.class.getName() + ":" + methodName);
                hmStructuredData.put(childRoot+
                    Utilities.getProperty(FieldSeparatorTokens.props, "STR_TIME_PROCESS", "TIME_PROCESS"), "" + (System.nanoTime() - lTiempoIni));
            }
        }
        return hmStructuredData;
    }
    
    /**
     * Esta rutina es especificamente para separar los campos que hay presentes
     * segun la distribucion de cada token
     * @param strNombre
     * @param strValorToken
     * @return Descomposicion de un campo particular
     */
    private List<String> descomposeToken( String strNombre, String strValorToken ) {
        List<String> salida = new ArrayList<>();
        Pattern pToUse = Pattern.compile(Utilities.getProperty(FieldSeparatorTokens.props, "STR_PATRON_TOKEN_DEFAULT", "\\A(.*?)\\Z"));
        // Modificacion generica
        String strPotencialTokenParam = 
            Utilities.getProperty(FieldSeparatorTokens.props, "STR_PATRON_TOKEN_BASE", "STR_PATRON_TOKEN_") + strNombre;
        String strPattern = (String)props.get(strPotencialTokenParam);
        if( strPattern != null ) {
          pToUse = Pattern.compile(strPattern);
        }
        Matcher mAux = pToUse.matcher(strValorToken);
        if(mAux.matches()) {
            for( int i=0; i < mAux.groupCount(); i++ ) {
                
                salida.add(mAux.group(i+1));
            }    
        }
        return salida;
    }
}
