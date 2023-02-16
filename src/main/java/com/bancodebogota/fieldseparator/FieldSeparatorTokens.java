/* ARCHIVO: FieldSeparatorTokens.java
 * Separacion de campos en tokens de acuerdo a expresiones regulares de
 * identificacion de tokens que se parametrizan
 *
 * ULTIMA MODIFICACION:     Febrero 2 de 2023
 */

package com.bancodebogota.fieldseparator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

/**
 * Separacion de campos en tokens de acuerdo a expresiones regulares de
 * identificacion de tokens que se parametrizan
 * @author Juan Miguel Chaves
 */
public class FieldSeparatorTokens {
    static volatile Properties props = new Properties();
    public static final boolean F_OPTIMISTIC_OPERATION = true;
    /**
     * 
     * @throws IOException 
     */
    public final void resync()
            throws IOException {
        URL url = new URL(Constants.STR_URL_CONFIG_SERVICE_BASE + "/get?JSON=TOKENS");
        HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
        JSONObject jsonObject = new JSONObject(Utilities.readFullyAsString(urlcon.getInputStream()));
        props = new Properties();
        props.putAll((HashMap) jsonObject.toMap());
    }
    
    /**
     * Metodo analizador de campos tokenizados
     * @param hmStructuredDataInput estructura en la que vienen varios campos tokenizados
     * @return La descomposición de campos
     */
    public Map<String, String> fieldAnalyzer(Map<String, String>  hmStructuredDataInput) {
        Map<String, String> hmStructuredData = new HashMap<>();
        for(String fieldName: hmStructuredDataInput.keySet()) {
            if( fieldName.matches(Constants.STR_FIELD_TOKEN_PATTERN)) {
                String childRoot = fieldName.replaceFirst(Constants.STR_FIELD_TOKEN_PATTERN, Constants.STR_FIELD_TOKEN_ROOT);
                String strDescripcionOptimista = "Mensajes: ";
                long lTiempoIni = System.nanoTime();
                try {
                    hmStructuredData.put(childRoot + Constants.STR_TOKENS_ENCONTRADOS , "" + 0 );
                    hmStructuredData.put(childRoot + Constants.STR_LONGITUD_TOTAL , "" + 0 );
                    String strValorCompleto = hmStructuredDataInput.get(fieldName);
                    final String STR_TAG_BASE2 = Constants.STR_FIELD_NAME_SEPARATOR + Constants.STR_SUBFIELD;
                    if(strValorCompleto == null)
                        throw new NullPointerException( "Campo Tokenizado en null" );
                    Pattern p1 = Pattern.compile(Constants.STR_PATRON_INI_TOKEN);
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
                        hmStructuredData.put(childRoot + Constants.STR_TOKENS_ENCONTRADOS , "" +iNumTokensAdicionales );
                        hmStructuredData.put(childRoot + Constants.STR_LONGITUD_TOTAL , "" + iTamanyo );
                        Pattern p2 = Pattern.compile(Constants.STR_PATRON_OTRO_TOKEN);
                        String strRestoToken;
                        for( int i = 0; i < iNumTokensAdicionales || F_OPTIMISTIC_OPERATION; i++ )
                        {
                            Matcher m2 = p2.matcher(strResto);
                            if(m2.matches()) {
                                String strNombreToken = m2.group(1);
                                int iTamanyoToken = Integer.parseInt(m2.group(2));
                                strRestoToken = m2.group(3);
                                strResto = strRestoToken.substring(iTamanyoToken);
                                strRestoToken = strRestoToken.substring(0, iTamanyoToken);

                                List<String> datos = descomposeToken(strNombreToken, strRestoToken);
                                int j = 1;
                                for(String dat: datos) {
                                    hmStructuredData.put(childRoot + strNombreToken  + STR_TAG_BASE2 + String.format("%03d", j) , dat );
                                    j++;
                                }
                            }
                            else {
                                if( F_OPTIMISTIC_OPERATION ) {
                                    strDescripcionOptimista += "Token no encontrado [" + Constants.STR_PATRON_OTRO_TOKEN + "] en: [" + strResto + "], ";
                                    break;
                                } else {
                                    throw new NoSuchElementException("Token no encontrado [" + Constants.STR_PATRON_OTRO_TOKEN + "] en: [" + strResto + "]");
                                }
                            }
                        }
                    }
                    else {
                        throw new NoSuchElementException("No encontrado token principal: [" + Constants.STR_PATRON_INI_TOKEN + "] en: [" + strValorCompleto + "]");
                    }
                    hmStructuredData.put(childRoot+Constants.STR_STATUS_PROCESS, "OK");
                    if( F_OPTIMISTIC_OPERATION ) {
                        hmStructuredData.put(childRoot+Constants.STR_STATUS_DESCRIPTION, "Procesamiento con novedad. " + strDescripcionOptimista.substring(0, strDescripcionOptimista.length() - 2 ));
                    } else {
                        hmStructuredData.put(childRoot+Constants.STR_STATUS_DESCRIPTION, "Procesamiento todo OK");  
                    }
                }
                catch( Exception ae) {
                    hmStructuredData.put(childRoot+Constants.STR_STATUS_PROCESS, "NO");
                    hmStructuredData.put(childRoot+Constants.STR_STATUS_DESCRIPTION, ae.getMessage());
                }
                final String methodName = new Object() {}
                                    .getClass()
                                    .getEnclosingMethod()
                                    .getName();
                hmStructuredData.put(childRoot+Constants.STR_STATUS_OWNER_PROCESS, FieldSeparatorTokens.class.getName() + ":" + methodName);
                hmStructuredData.put(childRoot+Constants.STR_TIME_PROCESS, "" + (System.nanoTime() - lTiempoIni));
            }
        }
        return hmStructuredData;
    }
    
    /**
     * Esta rutina es especificamente para separar los campos que hay presentes
     * segun la distribución de cada token
     * @param strNombre
     * @param strValorToken
     * @return Descomposicion de un campo particular
     */
    private List<String> descomposeToken( String strNombre, String strValorToken ) {
        List<String> salida = new ArrayList<>();
        Pattern pToUse = Pattern.compile(Constants.STR_PATRON_TOKEN_BASE);
        if( strNombre.equals(Constants.STR_TOKEN_QP)) {
            pToUse = Pattern.compile(Constants.STR_PATRON_TOKEN_QP);
        } else if( strNombre.equals(Constants.STR_TOKEN_QT)) {
            pToUse = Pattern.compile(Constants.STR_PATRON_TOKEN_QT);
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
