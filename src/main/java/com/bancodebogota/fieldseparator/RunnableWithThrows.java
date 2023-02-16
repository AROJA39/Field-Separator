/* ARCHIVO: RunnableWithThrows.java
 * Interfaz que sirve de envoltorio para ejecucui�n de programas sin tener
 * que capturar internamente las excepciones, delegandolas al contenedor
 *
 * ULTIMA MODIFICACION:     Febrero 1 de 2023
 */
package com.bancodebogota.fieldseparator;

/**
 * Interfaz que sirve de envoltorio para ejecucui�n de programas sin tener
 * que capturar internamente las excepciones, delegandolas al contenedor
 * @author Juan Miguel Chaves
 */
public interface RunnableWithThrows {
    /**
     * Metodo de ejecuci�n de l�gica de esta interfaz funcional
     * @throws Exception El potencial error que pueda ocurrir
     */
    void run() throws Exception;
}
