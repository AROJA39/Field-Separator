package com.bancodebogota.fieldseparator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

/**
 * @author Juan Miguel Chaves
 */
public class IdentifyTransactions {
  static final FieldSeparatorTokens fieldSeparatorTokensObject = new FieldSeparatorTokens();

  /** Reglas de identificacion del tipo transaccional */
  static volatile Map<String, String> rules;

  static String strUrlConfigServiceBase;

  /**
   * Metodo de inicializacion del componente, en el que se cargan los archivos de configuracion en
   * memoria del servicio
   *
   * @param urlServicioConfiguracion URL del servicio por el que se consultan archivos especificos
   *     de configuracion de componentes
   * @throws IOException Si fue imposible comunicarse con el servicio con los archivos de
   *     configuracion
   */
  public static final void initialize(String urlServicioConfiguracion) throws IOException {
    strUrlConfigServiceBase = urlServicioConfiguracion;
    internalInitialize();
  }

  private static final void internalInitialize() throws IOException {
    URL url = new URL(strUrlConfigServiceBase + "/get?JSON=IDENTIFYRULES");
    HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
    JSONObject jsonObject = new JSONObject(Utilities.readFullyAsString(urlcon.getInputStream()));
    rules = new TreeMap();
    for (String strAux : jsonObject.toMap().keySet()) {
      rules.put(strAux, jsonObject.toMap().get(strAux).toString());
    }
    FieldSeparatorSubFields.initialize(strUrlConfigServiceBase);
    fieldSeparatorTokensObject.resync();
  }

  /**
   * Metodo de separacion de campos recibe un Map de entrada con los campos a separar y retorna en
   * un Map de salida los campos que conforman dicha separacion
   *
   * @param sourceFields Campos que se quiere separar segun la configuracion de separacion de campos
   * @return Campos separados en subcampos como hayan sido encontrados
   */
  public static final Map<String, String> identifyTransaction(Map<String, String> sourceFields)
      throws IOException {
    // funcion que identifica univocamente el tipo de la tx
    validateConfigurationLoad();
    Map<String, String> results = new TreeMap<>();
    for (String entry : rules.keySet()) {
      System.out.println("Visitando reglas:[" + entry + "][" + rules.get(entry) + "]");
      if (applyRule(rules.get(entry), sourceFields)) {
        String key = "TX_KEY_" + Integer.toHexString(rules.get(entry).hashCode());
        String description = entry;
        results.put("TRANSACTION_TYPE", key);
        results.put("DESCRIPTION", description);
        break;
      }
    }
    return results;
  }

  public static final boolean applyRule(String strRule, Map<String, String> sourceFields)
      throws IOException {
    StringTokenizer stAux = new StringTokenizer(strRule, "|");
    while (stAux.hasMoreElements()) {
      String critery = stAux.nextToken();
      System.out.println("Visitando criterios:[" + critery + "]");
      if (!applyCritery(critery, sourceFields)) {
        return false;
      }
    }
    return true;
  }

  public static final String STR_PATTERN_MICROLANGUAGE =
      "\\A([\\+\\-])(\\w+)([\\[](?:(?:TOKEN)|(?:SUBFIELD))\\.\\w+(?:\\.\\w+)?[\\]])?([\\[]\\d+\\,\\d+[\\]])?((?:\\=)|(?:\\!\\=))?(\\w+)?\\Z";

  public static final boolean applyCritery(String critery, Map<String, String> sourceFields)
      throws IOException {
    Pattern patt = Pattern.compile(STR_PATTERN_MICROLANGUAGE);
    Matcher matcher = patt.matcher(critery);
    if (matcher.matches()) {
      boolean positiveCriteria = matcher.group(1).equals("+");
      String fieldCriteria = matcher.group(2);
      String subdivisionField = matcher.group(3);
      String rangeField = matcher.group(4);
      String operatorCriteria = matcher.group(5);
      String valueCriteria = matcher.group(6);
      System.out.println(
          "Criterio analizado:["
              + fieldCriteria
              + "]["
              + subdivisionField
              + "]["
              + rangeField
              + "]["
              + operatorCriteria
              + "]["
              + valueCriteria
              + "]");
      if (operatorCriteria == null) {
        if (sourceFields.containsKey(fieldCriteria)) return positiveCriteria;
        else return !positiveCriteria;
      } else {
        if (sourceFields.containsKey(fieldCriteria)) {
          String strValue = sourceFields.get(fieldCriteria);
          System.out.println("Campo tomado [" + fieldCriteria + "][" + strValue + "]");
          if (subdivisionField != null) {
            Pattern pat =
                Pattern.compile("[\\[]((?:SUBFIELD)|(?:TOKEN))\\.(\\w+(?:\\.\\w+)?)[\\]]");
            Matcher mat = pat.matcher(subdivisionField);
            if (mat.matches()) {
              Map<String, String> test = new HashMap<>();
              test.put(fieldCriteria, sourceFields.get(fieldCriteria));
              if (mat.group(1).equals("TOKEN")) {
                test = fieldSeparatorTokensObject.fieldAnalyzer(test);
              } else if (mat.group(1).equals("SUBFIELD")) {
                test = FieldSeparatorSubFields.discriminate(test);
              }
              strValue = test.get(fieldCriteria + "." + mat.group(2));
            }
          }
          if (rangeField != null) {
            Pattern pat = Pattern.compile("[\\[](\\d+)\\,(\\d+)[\\]]");
            Matcher mat = pat.matcher(rangeField);
            if (mat.matches()) {
              strValue =
                  strValue.substring(
                      Integer.parseInt(mat.group(1)), Integer.parseInt(mat.group(2)));
            }
          }

          // SEPARACION EN TOKENS O SUBSTRINGS
          if (strValue.equals(valueCriteria)) {
            return positiveCriteria;
          } else {
            return !positiveCriteria;
          }
        } else {
          return !positiveCriteria;
        }
      }
    } else {
      return false;
    }
  }

  public static final void validateConfigurationLoad() throws IOException {
    if (rules == null) {
      internalInitialize();
    }
  }
}
