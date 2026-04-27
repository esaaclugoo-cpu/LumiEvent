package com.ilerna.novaticket.model;

/**
 * Enumerado que define los roles de usuario del sistema.
 * Determina los permisos y el flujo de navegación tras el login.
 */
public enum UsuarioEnum {

    /** Usuario con rol de cliente: puede ver eventos y comprar tickets. */
    cliente,
    /** Usuario con rol de administrador: tiene acceso completo al panel de gestión. */
    admin;
}
