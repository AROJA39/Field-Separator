/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.bancodebogota.fieldseparator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juan Miguel Chaves
 */
public class FieldSeparatorTest {
    
//    public FieldSeparatorTest() {
//    }
//    
//    @BeforeClass
//    public static void setUpClass() {
//    }
//    
//    @AfterClass
//    public static void tearDownClass() {
//    }
//    
//    @Before
//    public void setUp() {
//    }
//    
//    @After
//    public void tearDown() {
//    }

    /**
     * Test of discriminate method, of class FieldSeparatorSubFields.
     */
    @Test
    public void testDiscriminate() 
            throws IOException {
        System.out.println("discriminate");
        FieldSeparatorSubFields.initialize(Constants.STR_URL_CONFIG_SERVICE_BASE);
        Map<String, String> sourceFields = new HashMap<>();
        sourceFields.put("FIELD_037", "000000008218");
        Map<String, String> expResult  = new HashMap<>();
        expResult.put("FIELD_037.IDENTIFICACION_OFICINA_ADQUIRIENTE", "0000");
        expResult.put("FIELD_037.NUMERO_TERMINAL_RECEPTORA", "0000");
        expResult.put("FIELD_037.NUMERO_DE_SECUENCIA_TRANSACCION", "8218");
        Map<String, String> result = FieldSeparatorSubFields.discriminate(sourceFields);
        assertEquals(expResult, result);
    }
    
}
