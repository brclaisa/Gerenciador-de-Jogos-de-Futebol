package br.com.futebol.infrastructure.util;

import jakarta.enterprise.inject.spi.CDI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilitária que ajuda a obter instâncias via CDI.
 * 
 * Uso isso quando a injeção de dependência automática não
 * funciona e preciso "pegar" as classes na mão.
 */
public class CDIProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CDIProvider.class);
    
    /**
     * Obtém uma instância de uma classe via CDI.
     * 
     * Método principal que tenta "pegar" uma instância
     * da classe que você quiser. Se der certo, retorna a
     * instância. Se der errado, retorna null.
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
     * Verifica se o CDI está funcionando.
     * 
     * Método útil pra testar se o sistema de injeção
     * de dependências está rodando antes de tentar
     * usar.
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
