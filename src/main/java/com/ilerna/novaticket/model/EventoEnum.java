package com.ilerna.novaticket.model;

/**
 * Enumerado que define los posibles tipos de evento del sistema.
 * Se usa como discriminador para instanciar el subtipo correcto (Concierto, Teatro o Museo).
 */
public enum EventoEnum {

    /** Evento de tipo concierto musical. */
    concierto,
    /** Evento de tipo obra de teatro. */
    teatro,
    /** Evento de tipo exposición de museo. */
    museo;
}
