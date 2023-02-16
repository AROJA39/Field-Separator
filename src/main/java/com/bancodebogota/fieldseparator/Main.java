/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bancodebogota.fieldseparator;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.ContentType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;

/**
 *
 * @author Juan Miguel Chaves
 */
public class Main {
    public static void main(String[] args)
            throws IOException {
        final int iPuertoEscucha = args.length>0 ? Integer.parseInt(args[0]) : 8901 ;
        final String strUrlServicioConfiguracion = args.length >1 ? args[1] : Constants.STR_URL_CONFIG_SERVICE_BASE;
        try {
            FieldSeparatorSubFields.initialize(strUrlServicioConfiguracion);
        } catch(IOException e) {
            e.printStackTrace();
        }
        final FieldSeparatorTokens fieldSeparatorTokensObject = new FieldSeparatorTokens();
        final AcceptanceCriteria acceptanceCriteriaObject = new AcceptanceCriteria();
        try { 
            fieldSeparatorTokensObject.resync();
        } catch( IOException e ) {
            e.printStackTrace();
        }
        try { 
            acceptanceCriteriaObject.resync();
        } catch( IOException e ) {
            e.printStackTrace();
        }
        Javalin app = Javalin.create().start(iPuertoEscucha);
        app.get("resync", (Context ctx) -> {
            final ContextExecutor ce = new ContextExecutor("RESYNC", "HTTP://localhost:8901/resync", "1.0.0", () -> {
                FieldSeparatorSubFields.initialize(strUrlServicioConfiguracion);
                fieldSeparatorTokensObject.resync();
                System.out.println("RESYNC OK");
                ctx.result("RESYNC OK");
            });
            ce.RunToEnd();
            managePosibleException( ce, ctx);
            //managePosibleException( ce.getExceptionThrow(), ctx);
        }).get("subfields", (Context ctx) -> {
            ContextExecutor ce = new ContextExecutor("SUBFIELDS", "HTTP://localhost:8901/subfields", "1.0.0", () -> {
                Map<String, List<String>> params = ctx.queryParamMap();
                Map<String, String> requestParams = new HashMap<>();
                params.forEach((key, value) -> requestParams.put(key, value.get(0)));
                
                Map<String, String> result = FieldSeparatorSubFields.discriminate(requestParams);
                
                ctx.contentType(ContentType.JSON );
                JSONObject obj = new JSONObject(result);
                ctx.result(obj.toString());
            });
            ce.RunToEnd();
            managePosibleException( ce, ctx);
        }).get("validations", (Context ctx) -> {
            ContextExecutor ce = new ContextExecutor("VALIDATIONS", "HTTP://localhost:8901/tokens", "1.0.0", () -> {
                Map<String, List<String>> params = ctx.queryParamMap();
                Map<String, String> requestParams = new HashMap<>();
                params.forEach((key, value) -> requestParams.put(key, value.get(0)));
                
                Map<String, String> result = FieldSeparatorSubFields.validate(requestParams);
                
                ctx.contentType(ContentType.JSON );
                JSONObject obj = new JSONObject(result);
                ctx.result(obj.toString());
            });
            ce.RunToEnd();
            managePosibleException( ce, ctx);
        }).get("tokens", (Context ctx) -> {
            ContextExecutor ce = new ContextExecutor("TOKENS", "HTTP://localhost:8901/tokens", "1.0.0", () -> {
                Map<String, List<String>> params = ctx.queryParamMap();
                Map<String, String> requestParams = new HashMap<>();
                params.forEach((key, value) -> requestParams.put(key, value.get(0)));
                
                Map<String, String> response = fieldSeparatorTokensObject.fieldAnalyzer(requestParams);
                
                ctx.contentType(ContentType.JSON );
                JSONObject obj = new JSONObject(response);
                ctx.result(obj.toString());
            });
            ce.RunToEnd();
            managePosibleException( ce, ctx);
        }).get("acceptancecriteria", (Context ctx) -> {
            ContextExecutor ce = new ContextExecutor("ACCEPTANCE_CRITERIA", "HTTP://localhost:8901/acceptancecriteria", "1.0.0", () -> {
                Map<String, List<String>> params = ctx.queryParamMap();
                Map<String, String> requestParams = new HashMap<>();
                params.forEach((key, value) -> requestParams.put(key, value.get(0)));
                
                Map<String, String> response = acceptanceCriteriaObject.acceptanceCriteria(requestParams);
                
                ctx.contentType(ContentType.JSON );
                JSONObject obj = new JSONObject(response);
                ctx.result(obj.toString());
            });
            ce.RunToEnd();
            managePosibleException( ce, ctx);
        }).get("write", (Context ctx) -> {
            ContextExecutor ce = new ContextExecutor("WRITE", "HTTP://localhost:8901/write", "1.0.0", () -> {
                acceptanceCriteriaObject.writeChacheFile();
                System.out.println("WRITE TO FILE OK");
                ctx.result("WRITE TO FILE OK");
            });
            ce.RunToEnd();
            managePosibleException( ce, ctx);
        }).get("state", (Context ctx) -> {
            ContextExecutor ce = new ContextExecutor("STATE", "HTTP://localhost:8901/state", "1.0.0", () -> {
                ctx.contentType(ContentType.JSON );
                ctx.result(ContextExecutor.toStringJSON());
            });
            ce.RunToEnd();
            managePosibleException( ce, ctx);
        });

    }
    
    public static void managePosibleException( ContextExecutor ce, Context ctx ) {
        if( ce.getExceptionThrow() != null ) {
            JSONObject response = new JSONObject();
            response.put("EXCEPTION", ce.getExceptionThrow().getClass().getName() );
            response.put("MESSAGE", ce.getExceptionThrow().getMessage());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            ce.getExceptionThrow().printStackTrace(ps);
            ps.flush();
            String stack = new String(baos.toByteArray());
            response.put("STACK_TRACE", stack);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            ce.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
            ce.setMessage(ce.getExceptionThrow().getMessage());
            ctx.contentType(ContentType.JSON);
            ctx.result(response.toString());
        }
    }
    
    public static void mainOLD(String[] args)
            throws IOException {
        FieldSeparatorSubFields.initialize("http://localhost:8085");
        System.out.println("discriminate");
        Map<String, String> sourceFields = new HashMap<>();
        sourceFields.put("F037", "000000008218");
        Map<String, String> expResult  = new HashMap<>();
        expResult.put("F037.IDENTIFICACION_OFICINA_ADQUIRIENTE", "0000");
        expResult.put("F037.NUMERO_TERMINAL_RECEPTORA", "0000");
        expResult.put("F037.NUMERO_DE_SECUENCIA_TRANSACCION", "8218");
        Map<String, String> result = FieldSeparatorSubFields.discriminate(sourceFields);
        System.out.println(expResult);
        System.out.println(result);
        
        sourceFields = new HashMap<>();
        sourceFields.put("F041", "0001792700007   ");
        result = FieldSeparatorSubFields.discriminate(sourceFields);
        JSONObject obj = new JSONObject(result);
        System.out.println(result);
        System.out.println(obj);
        
        
        sourceFields = new HashMap<>();
        //sourceFields.put("F043", "AVV66Cra 30 10-77     00011001          ");
        sourceFields.put("F043", "AVV66Cra 30 10-77     00011001           ");
        result = FieldSeparatorSubFields.discriminate(sourceFields);
        System.out.println(result);

        sourceFields = new HashMap<>();
        sourceFields.put("F048", "00010000000020000000000000000000000000010223");
        result = FieldSeparatorSubFields.discriminate(sourceFields);
        System.out.println(result);

        sourceFields = new HashMap<>();
        sourceFields.put("F054", "000002000000000000000000000000000000");
        result = FieldSeparatorSubFields.discriminate(sourceFields);
        System.out.println(result);

        sourceFields = new HashMap<>();
        sourceFields.put("F102", "000100000000821004108");
        result = FieldSeparatorSubFields.discriminate(sourceFields);
        System.out.println(result);

        sourceFields = new HashMap<>();
        //sourceFields.put("F103", "100230001000001263");
        sourceFields.put("F103", "003015173952008005203323");
        result = FieldSeparatorSubFields.discriminate(sourceFields);
        System.out.println(result);

        sourceFields = new HashMap<>();
        //sourceFields.put("F104", "003015173952008005203323");
        sourceFields.put("F104", "100230001000001263");
        result = FieldSeparatorSubFields.discriminate(sourceFields);
        System.out.println(result);

        sourceFields = new HashMap<>();
        sourceFields.put("F125", "01                              01010123456");
        result = FieldSeparatorSubFields.discriminate(sourceFields);
        System.out.println(result);
        
    }
}
