package br.com.futebol.infrastructure.util;

import jakarta.enterprise.inject.spi.CDI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitário para obter instâncias via CDI quando a injeção direta não funciona
 */
public class CDIProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CDIProvider.class);
    
    /**
     * Obtém uma instância de uma classe via CDI
     * 
     * @param <T> tipo da classe
     * @param clazz classe a ser instanciada
     * @return instância da classe ou null se não conseguir obter
     */
    public static <T> T getInstance(Class<T> clazz) {
        try {
            CDI<Object> cdi = CDI.current();
            if (cdi != null) {
                return cdi.select(clazz).get();
            }
        } catch (Exception e) {
            LOGGER.warn("Erro ao obter instância via CDI para {}: {}", clazz.getSimpleName(), e.getMessage());
        }
        
        LOGGER.warn("CDI não disponível, retornando null para {}", clazz.getSimpleName());
        return null;
    }
    
    /**
     * Verifica se o CDI está disponível
     * 
     * @return true se CDI estiver disponível
     */
    public static boolean isCDIAvailable() {
        try {
            CDI<Object> cdi = CDI.current();
            return cdi != null;
        } catch (Exception e) {
            return false;
        }
    }
}
