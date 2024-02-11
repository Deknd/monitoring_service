package com.denknd.security.entity;

/**
 * Служит для хранения пред аутентификации из реквеста
 * @param principal принципал для аутентификации
 * @param credentials данные которые послужили получению принципала
 */
public record PreAuthenticatedAuthenticationToken(Object principal, Object credentials) {
}
